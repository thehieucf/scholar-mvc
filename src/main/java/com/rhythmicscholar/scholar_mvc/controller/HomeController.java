package com.rhythmicscholar.scholar_mvc.controller;

import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.model.UserProgress;
import com.rhythmicscholar.scholar_mvc.repository.UserProgressRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controller xử lý trang chủ (Dashboard).
 */
@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private com.rhythmicscholar.scholar_mvc.repository.UserWordProgressRepository userWordProgressRepository;

    /**
     * Hiển thị trang chủ với thông tin cá nhân và tiến độ học tập.
     */
    @GetMapping({"/", "/index", "/index.html"})
    public String index(HttpSession session, Model model) {
        // Lấy userId từ session, mặc định là 1 (cho mục đích demo/dev)
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;
        
        // Lấy thông tin chi tiết người dùng
        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("user", user);
        
        // Lấy danh sách tiến độ chung của người dùng
        List<UserProgress> progressList = userProgressRepository.findByUserId(userId);
        model.addAttribute("progressList", progressList);
        
        // Tính toán tiến độ học tập trong ngày hôm nay
        java.time.LocalDateTime startOfDay = java.time.LocalDate.now().atStartOfDay();
        long studiedToday = userWordProgressRepository.countStudiedToday(userId, startOfDay);
        int dailyGoal = 20; // Mục tiêu học 20 từ mỗi ngày
        int percentage = (int) Math.min(100, (studiedToday * 100) / dailyGoal);
        
        // Gửi các thông số tính toán được sang view (Thymeleaf)
        model.addAttribute("studiedToday", studiedToday);
        model.addAttribute("dailyGoal", dailyGoal);
        model.addAttribute("percentage", percentage);
        model.addAttribute("remaining", Math.max(0, dailyGoal - (int) studiedToday));
        
        return "index";
    }
}
