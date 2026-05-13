package com.rhythmicscholar.scholar_mvc.controller.user;

import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller xử lý các chức năng xác thực người dùng (Đăng nhập, Đăng ký, Đăng xuất).
 */
@Controller
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Hiển thị trang đăng nhập.
     */
    @GetMapping({"/login", "/login.html"})
    public String login(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/";
        }
        return "user/login";
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
                              Model model) {
        // Tìm người dùng theo email trong database
        User user = userRepository.findByEmail(email).orElse(null);
        
        // Kiểm tra người dùng tồn tại và mật khẩu khớp (Lưu ý: Thực tế nên mã hóa mật khẩu)
        if (user != null && user.getPasswordHash().equals(password)) {
            // Lưu thông tin người dùng vào session để đánh dấu đã đăng nhập
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole()); // Lưu vai trò
            return "redirect:/";
        }
        
        // Trả về lỗi nếu đăng nhập thất bại
        model.addAttribute("error", "Email hoặc mật khẩu không chính xác.");
        return "user/login";
    }

    /**
     * Hiển thị trang đăng ký tài khoản mới.
     */
    @GetMapping({"/register", "/register.html"})
    public String register() {
        return "user/register";
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
                                 Model model) {
        // Kiểm tra xem email đã được sử dụng chưa
        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email này đã được sử dụng cho một tài khoản khác.");
            return "user/register";
        }
        
        // Tạo đối tượng User mới và lưu vào database
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setCurrentLevel(currentLevel);
        user.setTotalXp(0);
        user.setCurrentStreak(0);
        userRepository.save(user);
        
        // Tự động đăng nhập sau khi đăng ký thành công
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole()); // Lưu vai trò
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
