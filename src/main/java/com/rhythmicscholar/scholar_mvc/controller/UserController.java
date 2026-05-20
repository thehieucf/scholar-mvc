package com.rhythmicscholar.scholar_mvc.controller;

import com.rhythmicscholar.scholar_mvc.dto.CategoryProgressDto;
import com.rhythmicscholar.scholar_mvc.model.Badge;
import com.rhythmicscholar.scholar_mvc.model.Category;
import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.model.UserBadge;
import com.rhythmicscholar.scholar_mvc.model.UserProgress;
import com.rhythmicscholar.scholar_mvc.model.UserWordProgress;
import com.rhythmicscholar.scholar_mvc.model.Vocabulary;
import com.rhythmicscholar.scholar_mvc.repository.BadgeRepository;
import com.rhythmicscholar.scholar_mvc.repository.CategoryRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserBadgeRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserProgressRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserWordProgressRepository;
import com.rhythmicscholar.scholar_mvc.repository.VocabularyRepository;
import com.rhythmicscholar.scholar_mvc.service.BadgeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller xử lý các chức năng liên quan đến người dùng (Hồ sơ, Tiến độ, Bảng xếp hạng).
 */
@Controller
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private UserProgressRepository userProgressRepository;
    @Autowired private UserWordProgressRepository userWordProgressRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private VocabularyRepository vocabularyRepository;
    @Autowired private BadgeService badgeService;
    @Autowired private BadgeRepository badgeRepository;
    @Autowired private UserBadgeRepository userBadgeRepository;

    // ----------------------------------------------------------------
    // XP thresholds cho từng level
    // ----------------------------------------------------------------
    private String calculateLevel(int xp) {
        if (xp >= 2000) return "Master";
        if (xp >= 800)  return "Advanced";
        if (xp >= 250)  return "Intermediate";
        return "Beginner";
    }

    // ----------------------------------------------------------------
    // API ghi nhận tiến độ học một từ vựng
    // ----------------------------------------------------------------
    /**
     * Được gọi từ study.js mỗi khi user lật thẻ flashcard.
     * - Tạo hoặc cập nhật UserWordProgress
     * - Cộng XP (+5 từ mới, +2 ôn lại)
     * - Cập nhật streak (với logic reset nếu bỏ ngày)
     * - Tự động nâng level dựa trên XP
     * - Kiểm tra và trao badge mới
     */
    @PostMapping("/api/study/progress")
    @ResponseBody
    public ResponseEntity<?> recordStudyProgress(
            @RequestParam Long vocabId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body(Map.of("error", "User not found"));

        Vocabulary vocab = vocabularyRepository.findById(vocabId).orElse(null);
        if (vocab == null) return ResponseEntity.badRequest().body(Map.of("error", "Vocabulary not found"));

        // ---- 1. Cập nhật UserWordProgress ----
        Optional<UserWordProgress> existing = userWordProgressRepository.findByUserIdAndVocabularyId(userId, vocabId);
        boolean isNew = existing.isEmpty();

        UserWordProgress progress = existing.orElseGet(UserWordProgress::new);
        if (isNew) {
            progress.setUser(user);
            progress.setVocabulary(vocab);
            progress.setLearningStatus("LEARNING");
        } else {
            int consec = progress.getConsecutiveCorrect() != null ? progress.getConsecutiveCorrect() : 0;
            progress.setConsecutiveCorrect(consec + 1);
            if (consec + 1 >= 5) progress.setLearningStatus("MASTERED");
        }
        progress.setIsFlashcardDone(true);
        progress.setNextReviewDate(LocalDate.now().plusDays(1));
        progress.setLastStudiedAt(java.time.LocalDateTime.now());
        userWordProgressRepository.save(progress);

        // ---- 2. Cộng XP ----
        int xpGain = isNew ? 5 : 2;
        int newXp = (user.getTotalXp() != null ? user.getTotalXp() : 0) + xpGain;
        user.setTotalXp(newXp);

        // ---- 3. Tự động nâng/hạ level theo XP ----
        user.setCurrentLevel(calculateLevel(newXp));

        // ---- 4. Cập nhật streak (logic reset đúng) ----
        LocalDate today = LocalDate.now();
        LocalDate lastDate = user.getLastStudiedDate();

        if (lastDate == null || lastDate.isBefore(today.minusDays(1))) {
            // Bỏ lỡ ít nhất 1 ngày → reset streak về 1
            user.setCurrentStreak(1);
        } else if (lastDate.isBefore(today)) {
            // Học ngày hôm qua → tăng streak
            user.setCurrentStreak((user.getCurrentStreak() != null ? user.getCurrentStreak() : 0) + 1);
        }
        // Nếu lastDate == today → không thay đổi streak (đã tính rồi)

        // Cập nhật longest streak
        int streak = user.getCurrentStreak();
        int longest = user.getLongestStreak() != null ? user.getLongestStreak() : 0;
        if (streak > longest) user.setLongestStreak(streak);

        // Cập nhật ngày học cuối
        user.setLastStudiedDate(today);
        userRepository.save(user);

        // ---- 5. Kiểm tra và trao badge mới ----
        List<Badge> newBadges = badgeService.checkAndAwardBadges(user);
        List<Map<String, String>> newBadgeData = newBadges.stream().map(b -> Map.of(
            "name", b.getName(),
            "emoji", b.getIconEmoji() != null ? b.getIconEmoji() : "🏅",
            "description", b.getDescription() != null ? b.getDescription() : ""
        )).toList();

        return ResponseEntity.ok(Map.of(
            "xpGained",   xpGain,
            "totalXp",    user.getTotalXp(),
            "streak",     user.getCurrentStreak(),
            "level",      user.getCurrentLevel(),
            "newBadges",  newBadgeData,
            "status",     progress.getLearningStatus()
        ));
    }

    // ----------------------------------------------------------------
    // Profile
    // ----------------------------------------------------------------
    @GetMapping({"/profile", "/profile.html"})
    public String profile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;
        User user = userRepository.findById(userId).orElse(null);

        // Lấy badge của user
        List<UserBadge> userBadges = userBadgeRepository.findByUserIdWithBadge(userId);
        // Lấy tất cả badge để hiển thị locked/unlocked
        List<Badge> allBadges = badgeRepository.findAllByOrderByDisplayOrderAsc();
        List<Long> earnedBadgeIds = userBadges.stream().map(ub -> ub.getBadge().getId()).toList();

        model.addAttribute("user", user);
        model.addAttribute("userBadges", userBadges);
        model.addAttribute("allBadges", allBadges);
        model.addAttribute("earnedBadgeIds", earnedBadgeIds);
        return "profile";
    }

    // ----------------------------------------------------------------
    // Progress
    // ----------------------------------------------------------------
    @GetMapping({"/progress", "/progress.html"})
    public String progress(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;
        User user = userRepository.findById(userId).orElse(null);

        List<Category> categories = categoryRepository.findAll();
        List<CategoryProgressDto> categoryProgressList = new ArrayList<>();
        for (Category category : categories) {
            long totalWords   = vocabularyRepository.findByCategoryId(category.getId()).size();
            long studiedWords = userWordProgressRepository.countStudiedWordsByCategoryId(userId, category.getId());
            categoryProgressList.add(new CategoryProgressDto(
                    category.getId(), category.getNameEn(), category.getNameKr(),
                    category.getIconName(), category.getColorTheme(),
                    Boolean.TRUE.equals(category.getPopular()), totalWords, studiedWords));
        }

        long totalStudied = categoryProgressList.stream().mapToLong(CategoryProgressDto::getStudiedWords).sum();
        long grandTotal   = categoryProgressList.stream().mapToLong(CategoryProgressDto::getTotalWords).sum();
        long masteredCount = userWordProgressRepository.countMasteredByUserId(userId);

        List<UserProgress> progressList = userProgressRepository.findByUserId(userId);

        // Badge data
        List<UserBadge> userBadges = userBadgeRepository.findByUserIdWithBadge(userId);
        List<Badge> allBadges = badgeRepository.findAllByOrderByDisplayOrderAsc();
        List<Long> earnedBadgeIds = userBadges.stream().map(ub -> ub.getBadge().getId()).toList();

        // Rank
        long rank = userRepository.countUsersWithMoreXp(user != null && user.getTotalXp() != null ? user.getTotalXp() : 0) + 1;

        model.addAttribute("user", user);
        model.addAttribute("categoryProgressList", categoryProgressList);
        model.addAttribute("progressList", progressList);
        model.addAttribute("totalStudied", totalStudied);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("masteredCount", masteredCount);
        model.addAttribute("allBadges", allBadges);
        model.addAttribute("earnedBadgeIds", earnedBadgeIds);
        model.addAttribute("userBadges", userBadges);
        model.addAttribute("rank", rank);
        return "progress";
    }

    // ----------------------------------------------------------------
    // Leaderboard
    // ----------------------------------------------------------------
    @GetMapping({"/leaderboard", "/leaderboard.html"})
    public String leaderboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;
        User currentUser = userRepository.findById(userId).orElse(null);

        // Top 20 user theo XP
        List<User> topUsers = userRepository.findTopUsersByXp(PageRequest.of(0, 20));

        // Rank của user hiện tại
        int currentXp = currentUser != null && currentUser.getTotalXp() != null ? currentUser.getTotalXp() : 0;
        long rank = userRepository.countUsersWithMoreXp(currentXp) + 1;

        // Badge count cho mỗi user trong top
        Map<Long, Long> badgeCountMap = new HashMap<>();
        for (User u : topUsers) {
            badgeCountMap.put(u.getId(), userBadgeRepository.countByUserId(u.getId()));
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("topUsers", topUsers);
        model.addAttribute("currentRank", rank);
        model.addAttribute("badgeCountMap", badgeCountMap);
        return "leaderboard";
    }

    // ----------------------------------------------------------------
    // Update Profile
    // ----------------------------------------------------------------
    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session,
                                @RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam(required = false) String avatarUrl,
                                RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            user.setFullName(fullName);
            user.setEmail(email);
            if (avatarUrl != null && !avatarUrl.isBlank()
                    && (avatarUrl.startsWith("http://") || avatarUrl.startsWith("https://"))) {
                user.setAvatarUrl(avatarUrl);
            }
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        }
        return "redirect:/profile";
    }

    // ----------------------------------------------------------------
    // Change Password
    // ----------------------------------------------------------------
    @GetMapping({"/change-password"})
    public String changePasswordPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("user", user);
        return "change-password";
    }

    @PostMapping("/change-password")
    public String handleChangePassword(HttpSession session,
                                       @RequestParam String currentPassword,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/change-password";
        }
        if (!user.getPasswordHash().equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/change-password";
        }
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
            return "redirect:/change-password";
        }
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters.");
            return "redirect:/change-password";
        }
        user.setPasswordHash(newPassword);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/change-password";
    }
}
