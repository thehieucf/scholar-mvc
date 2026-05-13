package com.rhythmicscholar.scholar_mvc.controller.admin;

import com.rhythmicscholar.scholar_mvc.model.QuizQuestion;
import com.rhythmicscholar.scholar_mvc.repository.QuizQuestionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/quizzes")
public class AdminQuizController {

    private final QuizQuestionRepository quizQuestionRepository;

    public AdminQuizController(QuizQuestionRepository quizQuestionRepository) {
        this.quizQuestionRepository = quizQuestionRepository;
    }

    @GetMapping
    public String listQuizzes(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Page<QuizQuestion> quizPage = quizQuestionRepository.findAll(org.springframework.data.domain.PageRequest.of(page, size));
        model.addAttribute("quizzesPage", quizPage);
        model.addAttribute("quizzes", quizPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", quizPage.getTotalPages());
        model.addAttribute("totalItems", quizPage.getTotalElements());
        return "admin/quizManagement";
    }

    @PostMapping("/add")
    public String addQuiz(@ModelAttribute QuizQuestion quizQuestion) {
        quizQuestionRepository.save(quizQuestion);
        return "redirect:/admin/quizzes";
    }

    @PostMapping("/edit")
    public String editQuiz(@ModelAttribute QuizQuestion quizQuestion) {
        quizQuestionRepository.save(quizQuestion);
        return "redirect:/admin/quizzes";
    }

    @PostMapping("/delete/{id}")
    public String deleteQuiz(@PathVariable Long id) {
        quizQuestionRepository.deleteById(id);
        return "redirect:/admin/quizzes";
    }
}
