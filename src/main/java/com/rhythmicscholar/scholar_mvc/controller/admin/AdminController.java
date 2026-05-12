package com.rhythmicscholar.scholar_mvc.controller.admin;

import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final VocabularyRepository vocabularyRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    public AdminController(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           VocabularyRepository vocabularyRepository,
                           QuizQuestionRepository quizQuestionRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.quizQuestionRepository = quizQuestionRepository;
    }

    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String adminLogin(@RequestParam String email,
                             @RequestParam String password,
                             HttpSession session,
                             Model model) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // In a real app, use password encoder. Here we follow existing logic.
            if (user.getPasswordHash().equals(password) && "ADMIN".equals(user.getRole())) {
                session.setAttribute("userId", user.getId());
                session.setAttribute("userRole", user.getRole());
                return "redirect:/admin";
            }
        }
        model.addAttribute("error", "Email hoặc mật khẩu không đúng, hoặc tài khoản không có quyền quản trị.");
        return "admin/login";
    }

    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalCategories", categoryRepository.count());
        model.addAttribute("totalVocabularies", vocabularyRepository.count());
        model.addAttribute("totalQuizzes", quizQuestionRepository.count());
        return "admin/index";
    }

    @GetMapping("/userManagement")
    public String userManagement(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/userManagement";
    }

    @GetMapping("/categoryManagement")
    public String categoryManagement(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/categoryManagement";
    }

    @GetMapping("/vocabularyManagement")
    public String vocabularyManagement(Model model) {
        model.addAttribute("vocabularies", vocabularyRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/vocabularyManagement";
    }

    @GetMapping("/quizManagement")
    public String quizManagement(Model model) {
        model.addAttribute("quizzes", quizQuestionRepository.findAll());
        return "admin/quizManagement";
    }
}
