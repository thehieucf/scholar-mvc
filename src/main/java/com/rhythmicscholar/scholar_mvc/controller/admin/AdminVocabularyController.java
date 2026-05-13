package com.rhythmicscholar.scholar_mvc.controller.admin;

import com.rhythmicscholar.scholar_mvc.model.Category;
import com.rhythmicscholar.scholar_mvc.model.Vocabulary;
import com.rhythmicscholar.scholar_mvc.repository.CategoryRepository;
import com.rhythmicscholar.scholar_mvc.repository.VocabularyRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/vocabularies")
public class AdminVocabularyController {

    private final VocabularyRepository vocabularyRepository;
    private final CategoryRepository categoryRepository;

    public AdminVocabularyController(VocabularyRepository vocabularyRepository, CategoryRepository categoryRepository) {
        this.vocabularyRepository = vocabularyRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String listVocabularies(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        try {
            org.springframework.data.domain.Page<Vocabulary> vocabularyPage = vocabularyRepository.findAll(org.springframework.data.domain.PageRequest.of(page, size));
            model.addAttribute("vocabulariesPage", vocabularyPage);
            model.addAttribute("vocabularies", vocabularyPage.getContent());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", vocabularyPage.getTotalPages());
            model.addAttribute("totalItems", vocabularyPage.getTotalElements());
            return "admin/vocabularyManagement";
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/add")
    public String addVocabulary(@ModelAttribute Vocabulary vocabulary, @RequestParam Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        vocabulary.setCategory(category);
        vocabularyRepository.save(vocabulary);
        return "redirect:/admin/vocabularies";
    }

    @PostMapping("/edit")
    public String editVocabulary(@ModelAttribute Vocabulary vocabulary, @RequestParam Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        vocabulary.setCategory(category);
        vocabularyRepository.save(vocabulary);
        return "redirect:/admin/vocabularies";
    }

    @PostMapping("/delete/{id}")
    public String deleteVocabulary(@PathVariable Long id) {
        vocabularyRepository.deleteById(id);
        return "redirect:/admin/vocabularies";
    }
}
