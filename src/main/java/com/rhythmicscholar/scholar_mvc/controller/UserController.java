package com.rhythmicscholar.scholar_mvc.controller;

import com.rhythmicscholar.scholar_mvc.dto.CategoryProgressDto;
import com.rhythmicscholar.scholar_mvc.model.Category;
import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.model.UserProgress;
import com.rhythmicscholar.scholar_mvc.repository.CategoryRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserProgressRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserWordProgressRepository;
import com.rhythmicscholar.scholar_mvc.repository.VocabularyRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller xử lý các chức năng liên quan đến người dùng (Hồ sơ, Tiến độ học tập).
 */
@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private UserWordProgressRepository userWordProgressRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    /**
     * Hiển thị trang hồ sơ cá nhân.
     */
    @GetMapping({"/profile", "/profile.html"})
    public String profile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;
        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Hiển thị trang thống kê tiến độ học tập chi tiết.
     * Tính toán số từ đã học / tổng số từ cho mỗi category từ dữ liệu thực trong DB.
     */
    @GetMapping({"/progress", "/progress.html"})
    public String progress(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;
        User user = userRepository.findById(userId).orElse(null);

        // Lấy tất cả category
        List<Category> categories = categoryRepository.findAll();

        // Xây dựng danh sách DTO với dữ liệu tiến trình thực
        List<CategoryProgressDto> categoryProgressList = new ArrayList<>();
        for (Category category : categories) {
            long totalWords = vocabularyRepository.findByCategoryId(category.getId()).size();
            long studiedWords = userWordProgressRepository
                    .countStudiedWordsByCategoryId(userId, category.getId());

            categoryProgressList.add(new CategoryProgressDto(
                    category.getId(),
                    category.getNameEn(),
                    category.getNameKr(),
                    category.getIconName(),
                    category.getColorTheme(),
                    Boolean.TRUE.equals(category.getPopular()),
                    totalWords,
                    studiedWords
            ));
        }

        // Tổng số từ đã học trên tất cả category
        long totalStudied = categoryProgressList.stream()
                .mapToLong(CategoryProgressDto::getStudiedWords).sum();
        long grandTotal = categoryProgressList.stream()
                .mapToLong(CategoryProgressDto::getTotalWords).sum();

        // Lấy dữ liệu tiến độ cũ (UserProgress) nếu cần
        List<UserProgress> progressList = userProgressRepository.findByUserId(userId);

        model.addAttribute("user", user);
        model.addAttribute("categoryProgressList", categoryProgressList);
        model.addAttribute("progressList", progressList);
        model.addAttribute("totalStudied", totalStudied);
        model.addAttribute("grandTotal", grandTotal);
        return "progress";
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng (không bao gồm password).
     * avatarUrl chỉ được lưu nếu là URL thực (http/https), không lưu base64.
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
            // Chỉ lưu nếu là URL thực (http/https), bỏ qua base64 data URL
            if (avatarUrl != null && !avatarUrl.isBlank()
                    && (avatarUrl.startsWith("http://") || avatarUrl.startsWith("https://"))) {
                user.setAvatarUrl(avatarUrl);
            }
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        }
        return "redirect:/profile";
    }

    /**
     * Hiển thị trang đổi mật khẩu.
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
     * Xử lý đổi mật khẩu.
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
