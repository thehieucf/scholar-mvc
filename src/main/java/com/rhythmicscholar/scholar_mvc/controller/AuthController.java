package com.rhythmicscholar.scholar_mvc.controller;

import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

/**
 * Controller xử lý các chức năng xác thực người dùng (Đăng nhập, Đăng ký, Đăng xuất).
 */
@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    /**
     * Hiển thị trang đăng nhập.
     */
    @GetMapping({"/login", "/login.html"})
    public String login() {
        return "login";
    }

    /**
     * Xử lý yêu cầu đăng nhập.
     * @param email Email người dùng nhập
     * @param password Mật khẩu người dùng nhập
     */
    @PostMapping({"/login", "/login.html"})
    public String handleLogin(@RequestParam String email, 
                              @RequestParam String password, 
                              HttpSession session,
                              HttpServletRequest request,
                              Model model) {
        // Tìm người dùng theo email trong database
        User user = userRepository.findByEmail(email).orElse(null);
        
        // Kiểm tra người dùng tồn tại và mật khẩu khớp (Lưu ý: Thực tế nên mã hóa mật khẩu)
        if (user != null && user.getPasswordHash().equals(password)) {
            // Lưu thông tin người dùng vào session để đánh dấu đã đăng nhập
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());
            // Redirect theo role
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/";
        }
        
        // Lấy locale hiện tại để hiển thị thông báo lỗi đúng ngôn ngữ
        Locale locale = localeResolver.resolveLocale(request);
        model.addAttribute("error", messageSource.getMessage("login.error.invalid", null, locale));
        return "login";
    }

    /**
     * Hiển thị trang đăng ký tài khoản mới.
     */
    @GetMapping({"/register", "/register.html"})
    public String register() {
        return "register";
    }

    /**
     * Xử lý yêu cầu đăng ký.
     */
    @PostMapping({"/register", "/register.html"})
    public String handleRegister(@RequestParam String fullName,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam(defaultValue = "Beginner") String currentLevel,
                                 HttpSession session,
                                 HttpServletRequest request,
                                 Model model) {
        // Check if email is already in use
        if (userRepository.findByEmail(email).isPresent()) {
            Locale locale = localeResolver.resolveLocale(request);
            model.addAttribute("error", messageSource.getMessage("register.error.email.taken", null, locale));
            return "register";
        }
        
        // Tạo đối tượng User mới và lưu vào database
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setCurrentLevel(currentLevel);
        user.setTotalXp(0);
        user.setCurrentStreak(0);
        user.setRole("USER"); // Mặc định role USER khi đăng ký
        userRepository.save(user);
        
        // Tự động đăng nhập sau khi đăng ký thành công
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());
        return "redirect:/";
    }

    /**
     * Xử lý yêu cầu đăng xuất.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Xóa tất cả dữ liệu trong session
        session.invalidate();
        return "redirect:/login";
    }
}
