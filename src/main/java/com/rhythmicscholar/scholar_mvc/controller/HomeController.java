package com.rhythmicscholar.scholar_mvc.controller;

import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.model.UserProgress;
import com.rhythmicscholar.scholar_mvc.repository.UserProgressRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.time.LocalDateTime;
import java.time.LocalDate;
import com.rhythmicscholar.scholar_mvc.repository.UserWordProgressRepository;


/**
 * Controller xử lý trang chủ (Dashboard).
 */
@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final UserWordProgressRepository userWordProgressRepository;

    public HomeController(UserRepository userRepository,
                          UserProgressRepository userProgressRepository,
                          UserWordProgressRepository userWordProgressRepository) {
        this.userRepository = userRepository;
        this.userProgressRepository = userProgressRepository;
        this.userWordProgressRepository = userWordProgressRepository;
    }

    /**
     * Hiển thị trang chủ với thông tin cá nhân và tiến độ học tập.
     */
    @GetMapping({"/", "/index", "/index.html"})
    public String index(HttpSession session, Model model) {
        // Lấy userId từ session an toàn
        Object sessionUserId = session.getAttribute("userId");
        Long userId;
        if (sessionUserId instanceof Long) {
            userId = (Long) sessionUserId;
        } else if (sessionUserId instanceof Integer) {
            userId = ((Integer) sessionUserId).longValue();
        } else if (sessionUserId instanceof String) {
            userId = Long.parseLong((String) sessionUserId);
        } else {
            userId = 1L; // Fallback
        }
        
        // Lấy thông tin chi tiết người dùng
        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("user", user);
        
        // Lấy danh sách tiến độ chung của người dùng
        List<UserProgress> progressList = userProgressRepository.findByUserId(userId);
        model.addAttribute("progressList", progressList);
        
        // Tính toán tiến độ học tập trong ngày hôm nay
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long studiedToday = userWordProgressRepository.countStudiedToday(userId, startOfDay);
        int dailyGoal = 20; // Mục tiêu học 20 từ mỗi ngày
        int percentage = (int) Math.min(100, (studiedToday * 100) / dailyGoal);
        
        // Gửi các thông số tính toán được sang view (Thymeleaf)
        model.addAttribute("studiedToday", studiedToday);
        model.addAttribute("dailyGoal", dailyGoal);
        model.addAttribute("percentage", percentage);
        
        return "user/index";
    }
}
