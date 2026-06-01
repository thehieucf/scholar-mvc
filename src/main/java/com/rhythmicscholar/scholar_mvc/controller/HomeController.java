package com.rhythmicscholar.scholar_mvc.controller;

import com.rhythmicscholar.scholar_mvc.model.Badge;
import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.model.UserBadge;
import com.rhythmicscholar.scholar_mvc.model.UserProgress;
import com.rhythmicscholar.scholar_mvc.repository.BadgeRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserBadgeRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserProgressRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserWordProgressRepository;
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

    @Autowired private UserRepository userRepository;
    @Autowired private UserProgressRepository userProgressRepository;
    @Autowired private UserWordProgressRepository userWordProgressRepository;
    @Autowired private BadgeRepository badgeRepository;
    @Autowired private UserBadgeRepository userBadgeRepository;

    /**
     * Hiển thị trang chủ (Dashboard) của người dùng.
     * Bao gồm: tiến độ hôm nay, mục tiêu hàng ngày, streak, XP và danh sách badge.
     */
    @GetMapping({"/", "/index", "/index.html"})
    public String index(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;

        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("user", user);

        List<UserProgress> progressList = userProgressRepository.findByUserId(userId);
        model.addAttribute("progressList", progressList);

        // Tiến độ hôm nay — dailyGoal khớp với số từ tối đa mỗi session
        java.time.LocalDateTime startOfDay = java.time.LocalDate.now().atStartOfDay();
        long studiedToday = userWordProgressRepository.countStudiedToday(userId, startOfDay);
        int dailyGoal = 10;  // khớp với giới hạn session trong StudyController
        int percentage = (int) Math.min(100, (studiedToday * 100) / dailyGoal);

        model.addAttribute("studiedToday", studiedToday);
        model.addAttribute("dailyGoal", dailyGoal);
        model.addAttribute("percentage", percentage);
        model.addAttribute("remaining", Math.max(0, dailyGoal - (int) studiedToday));

        // Dữ liệu badge cho phần Milestones
        List<Badge> allBadges = badgeRepository.findAllByOrderByDisplayOrderAsc();
        List<UserBadge> userBadges = userBadgeRepository.findByUserIdWithBadge(userId);
        List<Long> earnedBadgeIds = userBadges.stream().map(ub -> ub.getBadge().getId()).toList();

        model.addAttribute("allBadges", allBadges);
        model.addAttribute("earnedBadgeIds", earnedBadgeIds);
        model.addAttribute("earnedBadgeCount", earnedBadgeIds.size());

        return "index";
    }
}
