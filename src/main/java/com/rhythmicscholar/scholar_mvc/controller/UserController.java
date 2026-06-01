package com.rhythmicscholar.scholar_mvc.controller;

import com.rhythmicscholar.scholar_mvc.dto.CategoryProgressDto;
import com.rhythmicscholar.scholar_mvc.model.Badge;
import com.rhythmicscholar.scholar_mvc.model.Category;
import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.model.UserBadge;
import com.rhythmicscholar.scholar_mvc.model.UserProgress;
import com.rhythmicscholar.scholar_mvc.repository.BadgeRepository;
import com.rhythmicscholar.scholar_mvc.repository.CategoryRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserBadgeRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserProgressRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserWordProgressRepository;
import com.rhythmicscholar.scholar_mvc.repository.VocabularyRepository;
import com.rhythmicscholar.scholar_mvc.service.BadgeService;
import com.rhythmicscholar.scholar_mvc.service.StudyProgressService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired private StudyProgressService studyProgressService;

    // ----------------------------------------------------------------
    // API ghi nhận tiến độ học một từ vựng (Flashcard)
    // ----------------------------------------------------------------
    /**
     * Được gọi từ study.js mỗi khi user lật thẻ flashcard.
     * Dùng StudyProgressService để xử lý XP, streak, SM-2, UserProgress.
     */
    @PostMapping("/api/study/progress")
    @ResponseBody
    public ResponseEntity<?> recordStudyProgress(
            @RequestParam Long vocabId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        StudyProgressService.ProgressResult result = studyProgressService.recordFlashcard(userId, vocabId);
        if (result.isError()) return ResponseEntity.badRequest().body(Map.of("error", result.errorMessage));

        User user = userRepository.findById(userId).orElse(null);

        // Kiểm tra và trao badge mới
        List<Badge> newBadges = user != null ? badgeService.checkAndAwardBadges(user) : List.of();
        List<Map<String, String>> newBadgeData = newBadges.stream().map(b -> Map.of(
            "name",        b.getName(),
            "emoji",       b.getIconEmoji() != null ? b.getIconEmoji() : "🏅",
            "description", b.getDescription() != null ? b.getDescription() : ""
        )).toList();

        return ResponseEntity.ok(Map.of(
            "xpGained",  result.xpGained,
            "totalXp",   result.totalXp,
            "streak",    result.streak,
            "level",     result.level,
            "newBadges", newBadgeData,
            "status",    result.status
        ));
    }

    // ----------------------------------------------------------------
    // API ghi nhận kết quả một câu trả lời trong Quiz
    // ----------------------------------------------------------------
    /**
     * Được gọi từ game.js sau mỗi câu trả lời.
     * Cập nhật UserWordProgress, XP, streak, SM-2 và UserProgress.
     *
     * @param vocabId  ID từ vựng liên kết với câu hỏi (từ QuizQuestion.vocabularyId)
     * @param correct  true nếu user trả lời đúng
     */
    @PostMapping("/api/game/result")
    @ResponseBody
    public ResponseEntity<?> recordQuizResult(
            @RequestParam Long vocabId,
            @RequestParam boolean correct,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        StudyProgressService.ProgressResult result = studyProgressService.recordQuizAnswer(userId, vocabId, correct);
        if (result.isError()) return ResponseEntity.badRequest().body(Map.of("error", result.errorMessage));

        User user = userRepository.findById(userId).orElse(null);

        // Kiểm tra badge chỉ khi trả lời đúng (có XP mới)
        List<Map<String, String>> newBadgeData = List.of();
        if (correct && user != null) {
            List<Badge> newBadges = badgeService.checkAndAwardBadges(user);
            newBadgeData = newBadges.stream().map(b -> Map.of(
                "name",        b.getName(),
                "emoji",       b.getIconEmoji() != null ? b.getIconEmoji() : "🏅",
                "description", b.getDescription() != null ? b.getDescription() : ""
            )).toList();
        }

        return ResponseEntity.ok(Map.of(
            "xpGained",  result.xpGained,
            "totalXp",   result.totalXp,
            "streak",    result.streak,
            "level",     result.level,
            "status",    result.status,
            "correct",   result.correct,
            "newBadges", newBadgeData
        ));
    }

    // ----------------------------------------------------------------
    // Trang hồ sơ cá nhân
    // ----------------------------------------------------------------
    /**
     * Hiển thị trang hồ sơ cá nhân của người dùng đang đăng nhập.
     * Bao gồm thông tin cá nhân, danh sách badge đã đạt và tất cả badge (locked/unlocked).
     */
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
    // Trang tiến độ học tập
    // ----------------------------------------------------------------
    /**
     * Hiển thị tiến độ học tập của người dùng theo từng danh mục từ vựng.
     * Bao gồm:
     * <ul>
     *   <li>Phần trăm hoàn thành từng category</li>
     *   <li>Tổng số từ đã học và tổng số từ</li>
     *   <li>Số từ MASTERED</li>
     *   <li>Danh sách badge và xếp hạng (rank)</li>
     * </ul>
     */
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

        // Dữ liệu badge của user
        List<UserBadge> userBadges = userBadgeRepository.findByUserIdWithBadge(userId);
        List<Badge> allBadges = badgeRepository.findAllByOrderByDisplayOrderAsc();
        List<Long> earnedBadgeIds = userBadges.stream().map(ub -> ub.getBadge().getId()).toList();

        // Tính thứ hạng (rank) của user dựa trên XP
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
    // Bảng xếp hạng
    // ----------------------------------------------------------------
    /**
     * Hiển thị bảng xếp hạng top 20 người dùng có XP cao nhất.
     * Cũng hiển thị rank của người dùng hiện tại và số badge mỗi user.
     */
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
    // Cập nhật hồ sơ cá nhân
    // ----------------------------------------------------------------
    /**
     * Xử lý cập nhật thông tin hồ sơ cá nhân (tên, email, avatar).
     * Avatar chỉ được cập nhật nếu URL bắt đầu bằng http:// hoặc https://.
     *
     * @param fullName Tên hiển thị mới
     * @param email    Email mới
     * @param avatarUrl URL ảnh đại diện (tùy chọn)
     */
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
    // Đổi mật khẩu
    // ----------------------------------------------------------------
    /**
     * Hiển thị trang đổi mật khẩu.
     * Redirect về /login nếu chưa đăng nhập.
     */
    @GetMapping({"/change-password"})
    public String changePasswordPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("user", user);
        return "change-password";
    }

    /**
     * Xử lý yêu cầu đổi mật khẩu.
     * Kiểm tra:
     * <ul>
     *   <li>Mật khẩu hiện tại phải khớp</li>
     *   <li>Mật khẩu mới và xác nhận phải giống nhau</li>
     *   <li>Mật khẩu mới phải có ít nhất 6 ký tự</li>
     * </ul>
     *
     * @param currentPassword  Mật khẩu hiện tại của user
     * @param newPassword      Mật khẩu mới muốn đổi
     * @param confirmPassword  Xác nhận lại mật khẩu mới
     */
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
