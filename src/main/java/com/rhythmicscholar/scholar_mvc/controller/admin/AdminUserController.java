package com.rhythmicscholar.scholar_mvc.controller.admin;

import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final com.rhythmicscholar.scholar_mvc.repository.UserWordProgressRepository userWordProgressRepository;

    public AdminUserController(UserRepository userRepository, 
                               com.rhythmicscholar.scholar_mvc.repository.UserWordProgressRepository userWordProgressRepository) {
        this.userRepository = userRepository;
        this.userWordProgressRepository = userWordProgressRepository;
    }

    @GetMapping
    public String listUsers(Model model, 
                            @RequestParam(defaultValue = "0") int page, 
                            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Page<User> userPage = userRepository.findByRoleNot("ADMIN", org.springframework.data.domain.PageRequest.of(page, size));
        model.addAttribute("usersPage", userPage);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        
        java.time.LocalDateTime startOfDay = java.time.LocalDate.now().atStartOfDay();
        java.util.Map<Long, Boolean> studyStatus = new java.util.HashMap<>();
        for (User user : userPage.getContent()) {
            studyStatus.put(user.getId(), userWordProgressRepository.countStudiedToday(user.getId(), startOfDay) > 0);
        }
        model.addAttribute("studyStatus", studyStatus);
        
        return "admin/userManagement";
    }

    @PostMapping("/reset-password/{id}")
    public String resetPassword(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPasswordHash("123456789"); // As requested, set to plain text "123456789"
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute User user) {
        // In a real app, hash the password before saving.
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute User user) {
        // Find existing user to preserve password if not changed, or handle update logic.
        User existingUser = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());
        existingUser.setCurrentLevel(user.getCurrentLevel());
        // For simplicity, we just save the incoming user object here, but usually we handle password carefully.
        userRepository.save(existingUser);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}
