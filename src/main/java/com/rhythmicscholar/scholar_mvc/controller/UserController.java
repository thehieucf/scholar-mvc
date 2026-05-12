package com.rhythmicscholar.scholar_mvc.controller;

import com.rhythmicscholar.scholar_mvc.dto.CategoryProgressDto;
import com.rhythmicscholar.scholar_mvc.model.Category;
import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.model.UserWordProgress;
import com.rhythmicscholar.scholar_mvc.model.Vocabulary;
import com.rhythmicscholar.scholar_mvc.repository.CategoryRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserProgressRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserWordProgressRepository;
import com.rhythmicscholar.scholar_mvc.repository.VocabularyRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.io.IOException;

/**
 * Controller xử lý các chức năng liên quan đến người dùng (Hồ sơ, Tiến độ học tập).
 */
@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final UserWordProgressRepository userWordProgressRepository;
    private final CategoryRepository categoryRepository;
    private final VocabularyRepository vocabularyRepository;

    public UserController(UserRepository userRepository,
                          UserProgressRepository userProgressRepository,
                          UserWordProgressRepository userWordProgressRepository,
                          CategoryRepository categoryRepository,
                          VocabularyRepository vocabularyRepository) {
        this.userRepository = userRepository;
        this.userProgressRepository = userProgressRepository;
        this.userWordProgressRepository = userWordProgressRepository;
        this.categoryRepository = categoryRepository;
        this.vocabularyRepository = vocabularyRepository;
    }

    /**
     * Hiển thị trang hồ sơ cá nhân.
     */
    @GetMapping({"/profile", "/profile.html"})
    public String profile(HttpSession session, Model model) {
        Long userId = getUserIdFromSession(session);
        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("user", user);
        return "user/profile";
    }

    private Long getUserIdFromSession(HttpSession session) {
        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId instanceof Long) return (Long) sessionUserId;
        if (sessionUserId instanceof Integer) return ((Integer) sessionUserId).longValue();
        if (sessionUserId instanceof String) return Long.parseLong((String) sessionUserId);
        return 1L; // Fallback
    }

    /**
     * Hiển thị trang thống kê tiến độ học tập chi tiết.
     * Tính toán số từ đã học / tổng số từ cho mỗi category từ dữ liệu thực trong DB.
     */
    @GetMapping({"/progress", "/progress.html"})
    public String progress(HttpSession session, Model model) {
        Long userId = getUserIdFromSession(session);

        User user = userRepository.findById(userId).orElse(null);

        // Lấy tất cả category
        List<Category> categories = categoryRepository.findAll();
        if (categories == null) categories = new ArrayList<>();

        // Lấy dữ liệu tổng hợp từ database trong 2 query thay vì N*2 query
        List<Object[]> totalCountsRaw = vocabularyRepository.countAllWordsGroupedByCategory();
        List<Object[]> studiedCountsRaw = userWordProgressRepository.countStudiedWordsGroupedByCategory(userId);

        Map<Long, Long> totalCountsMap = new HashMap<>();
        for (Object[] row : totalCountsRaw) {
            totalCountsMap.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
        }

        Map<Long, Long> studiedCountsMap = new HashMap<>();
        for (Object[] row : studiedCountsRaw) {
            studiedCountsMap.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
        }

        // Xây dựng danh sách DTO
        List<CategoryProgressDto> categoryProgressList = new ArrayList<>();
        for (Category category : categories) {
            long totalWords = totalCountsMap.getOrDefault(category.getId(), 0L);
            long studiedWords = studiedCountsMap.getOrDefault(category.getId(), 0L);

            categoryProgressList.add(new CategoryProgressDto(
                    category.getId(),
                    category.getNameEn(),
                    category.getNameKr(),
                    category.getIconName(),
                    category.getColorTheme(),
                    Boolean.TRUE.equals(category.getIsPopular()),
                    totalWords,
                    studiedWords
            ));
        }

        // Tổng số từ đã học trên tất cả category
        long totalStudied = 0;
        long grandTotal = 0;
        for (CategoryProgressDto dto : categoryProgressList) {
            totalStudied += dto.getStudiedWords();
            grandTotal += dto.getTotalWords();
        }

        // Lấy danh sách từ vựng đã học gần đây
        List<UserWordProgress> recentWords = new ArrayList<>();
        try {
            recentWords = userWordProgressRepository.findTop10ByUserIdOrderByLastStudiedAtDesc(userId);
            if (recentWords == null) recentWords = new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error fetching recent words: {}", e.getMessage());
        }

        model.addAttribute("user", user);
        model.addAttribute("categoryProgressList", categoryProgressList);
        model.addAttribute("recentWords", recentWords);
        model.addAttribute("totalStudied", totalStudied);
        model.addAttribute("grandTotal", grandTotal);
        return "user/progress";
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng.
     */
    @PostMapping("/profile/update")
    public String updateProfile(HttpSession session, @RequestParam String fullName, @RequestParam String email,
                                @RequestParam(required = false) MultipartFile avatarFile) {
        Long userId = getUserIdFromSession(session);
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            user.setFullName(fullName);
            user.setEmail(email);
            
            if (avatarFile != null && !avatarFile.isEmpty()) {
                try {
                    String uploadDir = "uploads/avatars/";
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    String originalFilename = avatarFile.getOriginalFilename();
                    String extension = "";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    }
                    String newFilename = UUID.randomUUID().toString() + extension;
                    
                    Path filePath = uploadPath.resolve(newFilename);
                    Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    
                    user.setAvatarUrl("/uploads/avatars/" + newFilename);
                } catch (IOException e) {
                    logger.error("Error uploading avatar", e);
                }
            }
            userRepository.save(user);
        }
        return "redirect:/profile.html";
    }

    /**
     * Hiển thị trang đổi mật khẩu.
     */
    @GetMapping({"/change-password", "/change-password.html"})
    public String changePasswordPage(HttpSession session, Model model) {
        Long userId = getUserIdFromSession(session);
        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("user", user);
        return "user/change-password";
    }

    /**
     * Xử lý đổi mật khẩu.
     */
    @PostMapping("/change-password")
    public String changePassword(HttpSession session,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        Long userId = getUserIdFromSession(session);
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            // Note: In a real app, you would hash and compare passwords using BCrypt.
            // Here we assume plaintext/simple matching for demonstration, matching existing logic.
            if (!user.getPasswordHash().equals(currentPassword)) {
                model.addAttribute("error", "Current password is incorrect.");
                model.addAttribute("user", user);
                return "user/change-password";
            }
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "New passwords do not match.");
                model.addAttribute("user", user);
                return "user/change-password";
            }
            
            user.setPasswordHash(newPassword);
            userRepository.save(user);
        }
        
        return "redirect:/profile.html?passwordChanged=true";
    }
}
