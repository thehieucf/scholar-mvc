package com.rhythmicscholar.scholar_mvc.controller;

import com.rhythmicscholar.scholar_mvc.model.Category;
import com.rhythmicscholar.scholar_mvc.model.QuizQuestion;
import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.model.VocabGroup;
import com.rhythmicscholar.scholar_mvc.model.Vocabulary;
import com.rhythmicscholar.scholar_mvc.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Admin area.
 * All routes are prefixed with /admin — uses its own CSS/JS, independent of the user area.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private UserWordProgressRepository userWordProgressRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private VocabGroupRepository vocabGroupRepository;

    @Autowired
    private VocabGroupItemRepository vocabGroupItemRepository;

    // ----------------------------------------------------------------
    // Helper: get the currently logged-in admin from session
    // ----------------------------------------------------------------
    private User getCurrentAdmin(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return null;
        return userRepository.findById(userId).orElse(null);
    }

    // ----------------------------------------------------------------
    // Dashboard
    // ----------------------------------------------------------------
    @GetMapping({"/", "/dashboard"})
    public String dashboard(HttpSession session, Model model) {
        User admin = getCurrentAdmin(session);
        model.addAttribute("admin", admin);

        long totalUsers = userRepository.count();
        long totalVocab = vocabularyRepository.count();
        long adminCount = userRepository.countByRole("ADMIN");
        long userCount  = userRepository.countByRole("USER");

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalVocab", totalVocab);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("userCount", userCount);

        // 6 most recently registered users
        List<User> recentUsers = userRepository.findTop6ByOrderByIdDesc();
        model.addAttribute("recentUsers", recentUsers);

        // Check which recent users studied today
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        Map<Long, Boolean> studiedTodayMap = new HashMap<>();
        for (User u : recentUsers) {
            long count = userWordProgressRepository.countStudiedToday(u.getId(), startOfToday);
            studiedTodayMap.put(u.getId(), count > 0);
        }
        model.addAttribute("studiedTodayMap", studiedTodayMap);

        // ---- Chart 1: Active learners per day (last 7 days) ----
        List<String> chartDayLabels = new ArrayList<>();
        List<Long>   chartDayCounts = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day      = LocalDate.now().minusDays(i);
            LocalDateTime from = day.atStartOfDay();
            LocalDateTime to   = from.plusDays(1);
            long cnt = userWordProgressRepository.countDistinctUsersByDay(from, to);
            chartDayLabels.add(day.getMonthValue() + "/" + day.getDayOfMonth());
            chartDayCounts.add(cnt);
        }
        model.addAttribute("chartDayLabels", chartDayLabels);
        model.addAttribute("chartDayCounts", chartDayCounts);

        // ---- Chart 2: Word status distribution (NEW / LEARNING / MASTERED) ----
        List<Object[]> statusRows = userWordProgressRepository.countByLearningStatus();
        Map<String, Long> statusMap = new HashMap<>();
        statusMap.put("NEW", 0L);
        statusMap.put("LEARNING", 0L);
        statusMap.put("MASTERED", 0L);
        for (Object[] row : statusRows) {
            statusMap.put((String) row[0], (Long) row[1]);
        }
        model.addAttribute("statusNew",      statusMap.get("NEW"));
        model.addAttribute("statusLearning", statusMap.get("LEARNING"));
        model.addAttribute("statusMastered", statusMap.get("MASTERED"));

        // ---- Chart 3: Doughnut — User level distribution ----
        List<Object[]> levelRows = userRepository.countByLevel();
        List<String> chartLevelLabels = new ArrayList<>();
        List<Long>   chartLevelCounts = new ArrayList<>();
        for (Object[] row : levelRows) {
            chartLevelLabels.add((String) row[0]);
            chartLevelCounts.add((Long) row[1]);
        }
        model.addAttribute("chartLevelLabels", chartLevelLabels);
        model.addAttribute("chartLevelCounts", chartLevelCounts);

        return "admin/dashboard";
    }

    // ----------------------------------------------------------------
    // Manage Users
    // ----------------------------------------------------------------
    @GetMapping("/users")
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(defaultValue = "") String search,
                            HttpSession session,
                            Model model) {
        User admin = getCurrentAdmin(session);
        model.addAttribute("admin", admin);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<User> userPage;
        if (search != null && !search.isBlank()) {
            userPage = userRepository.searchByNameOrEmail(search.trim(), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        model.addAttribute("userPage", userPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalElements", userPage.getTotalElements());
        model.addAttribute("search", search);
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Long currentAdminId = (Long) session.getAttribute("userId");
        if (id.equals(currentAdminId)) {
            redirectAttributes.addFlashAttribute("error", "You cannot delete your own account.");
            return "redirect:/admin/users?page=" + page + "&size=" + size;
        }
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        return "redirect:/admin/users?page=" + page + "&size=" + size;
    }

    // ----------------------------------------------------------------
    // User Detail — learning status
    // ----------------------------------------------------------------
    @GetMapping("/users/{id}")
    public String userDetail(@PathVariable Long id,
                             HttpSession session,
                             Model model) {
        User admin = getCurrentAdmin(session);
        model.addAttribute("admin", admin);

        User target = userRepository.findById(id).orElse(null);
        if (target == null) return "redirect:/admin/users";
        model.addAttribute("target", target);

        // Tổng số từ đã học
        long totalStudied  = userWordProgressRepository.countStudiedByUserId(id);
        long totalMastered = userWordProgressRepository.countMasteredByUserId(id);
        long totalDays     = userWordProgressRepository.countDistinctStudyDaysByUserId(id);

        // Phân bổ trạng thái học (NEW / LEARNING / MASTERED)
        List<Object[]> statusRows = userWordProgressRepository.countByLearningStatusForUser(id);
        Map<String, Long> statusMap = new HashMap<>();
        statusMap.put("LEARNING", 0L);
        statusMap.put("MASTERED", 0L);
        for (Object[] row : statusRows) {
            statusMap.put((String) row[0], (Long) row[1]);
        }

        // Tiến độ theo từng category
        List<com.rhythmicscholar.scholar_mvc.model.Category> categories = categoryRepository.findAll();
        List<Map<String, Object>> categoryProgress = new ArrayList<>();
        for (com.rhythmicscholar.scholar_mvc.model.Category cat : categories) {
            long total   = vocabularyRepository.countByCategoryId(cat.getId());
            long studied = userWordProgressRepository.countStudiedWordsByCategoryId(id, cat.getId());
            if (total == 0) continue;
            Map<String, Object> row = new HashMap<>();
            row.put("nameEn",     cat.getNameEn());
            row.put("colorTheme", cat.getColorTheme());
            row.put("iconName",   cat.getIconName());
            row.put("total",      total);
            row.put("studied",    studied);
            row.put("pct",        (int) Math.min(100, studied * 100 / total));
            categoryProgress.add(row);
        }
        // Sắp xếp: category có tiến độ cao nhất lên đầu
        categoryProgress.sort((a, b) -> Integer.compare((int) b.get("pct"), (int) a.get("pct")));

        // 20 từ học gần nhất
        List<com.rhythmicscholar.scholar_mvc.model.UserWordProgress> recentWordsRaw =
            userWordProgressRepository.findRecentByUserId(id, PageRequest.of(0, 20));

        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        List<Map<String, Object>> recentWords = recentWordsRaw.stream().map(wp -> {
            Map<String, Object> m = new HashMap<>();
            m.put("koreanWord",      wp.getVocabulary().getKoreanWord());
            m.put("romaji",          wp.getVocabulary().getRomaji());
            m.put("englishMeaning",  wp.getVocabulary().getEnglishMeaning());
            m.put("categoryName",    wp.getVocabulary().getCategory().getNameEn());
            m.put("learningStatus",  wp.getLearningStatus());
            m.put("consecutiveCorrect", wp.getConsecutiveCorrect() != null ? wp.getConsecutiveCorrect() : 0);
            m.put("lastStudiedAt",   wp.getLastStudiedAt() != null ? wp.getLastStudiedAt().format(dtf) : "—");
            return m;
        }).toList();

        // Badge của user
        List<com.rhythmicscholar.scholar_mvc.model.UserBadge> userBadges =
            userBadgeRepository.findByUserIdWithBadge(id);

        // Học hôm nay
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        long studiedToday = userWordProgressRepository.countStudiedToday(id, startOfToday);

        // Format joined date
        String joinedDate = target.getCreatedAt() != null
            ? target.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"))
            : "—";

        model.addAttribute("totalStudied",    totalStudied);
        model.addAttribute("totalMastered",   totalMastered);
        model.addAttribute("totalDays",       totalDays);
        model.addAttribute("studiedToday",    studiedToday);
        model.addAttribute("statusLearning",  statusMap.get("LEARNING"));
        model.addAttribute("statusMastered",  statusMap.get("MASTERED"));
        model.addAttribute("categoryProgress", categoryProgress);
        model.addAttribute("recentWords",     recentWords);
        model.addAttribute("userBadges",      userBadges);
        model.addAttribute("joinedDate",      joinedDate);
        return "admin/user-detail";
    }

    @PostMapping("/users/{id}/reset-password")
    public String resetPassword(@PathVariable Long id,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Long currentAdminId = (Long) session.getAttribute("userId");
        if (id.equals(currentAdminId)) {
            redirectAttributes.addFlashAttribute("error", "You cannot reset your own password from here.");
            return "redirect:/admin/users?page=" + page + "&size=" + size;
        }
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setPasswordHash("123456789");
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success",
                "Password for " + user.getFullName() + " has been reset to \"123456789\".");
        }
        return "redirect:/admin/users?page=" + page + "&size=" + size;
    }

    // ----------------------------------------------------------------
    // Manage Vocabulary
    // ----------------------------------------------------------------
    @GetMapping("/vocabulary")
    public String listVocabulary(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "") String search,
                                 HttpSession session,
                                 Model model) {
        User admin = getCurrentAdmin(session);
        model.addAttribute("admin", admin);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Vocabulary> vocabPage;
        if (search != null && !search.isBlank()) {
            vocabPage = vocabularyRepository.searchPaged(search.trim(), pageable);
        } else {
            vocabPage = vocabularyRepository.findAllWithCategory(pageable);
        }

        model.addAttribute("vocabPage", vocabPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", vocabPage.getTotalPages());
        model.addAttribute("totalElements", vocabPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("nameEn").ascending()));
        return "admin/vocabulary";
    }

    @PostMapping("/vocabulary/add")
    public String addVocabulary(@RequestParam String koreanWord,
                                @RequestParam String romaji,
                                @RequestParam String englishMeaning,
                                @RequestParam(required = false) String wordType,
                                @RequestParam Long categoryId,
                                @RequestParam(required = false) String exampleKr,
                                @RequestParam(required = false) String exampleEn,
                                @RequestParam(required = false) String mnemonic,
                                @RequestParam(required = false) String vietnameseMeaning,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                RedirectAttributes redirectAttributes) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid category selected.");
            return "redirect:/admin/vocabulary";
        }
        Vocabulary vocab = new Vocabulary();
        vocab.setKoreanWord(koreanWord.trim());
        vocab.setRomaji(romaji.trim());
        vocab.setEnglishMeaning(englishMeaning.trim());
        vocab.setWordType(wordType != null && !wordType.isBlank() ? wordType.trim() : null);
        vocab.setCategory(category);
        vocab.setExampleKr(exampleKr != null && !exampleKr.isBlank() ? exampleKr.trim() : null);
        vocab.setExampleEn(exampleEn != null && !exampleEn.isBlank() ? exampleEn.trim() : null);
        vocab.setMnemonic(mnemonic != null && !mnemonic.isBlank() ? mnemonic.trim() : null);
        vocab.setVietnameseMeaning(vietnameseMeaning != null && !vietnameseMeaning.isBlank() ? vietnameseMeaning.trim() : null);
        vocabularyRepository.save(vocab);
        redirectAttributes.addFlashAttribute("success", "Word \"" + vocab.getKoreanWord() + "\" added successfully.");
        return "redirect:/admin/vocabulary?page=" + page + "&size=" + size;
    }

    @PostMapping("/vocabulary/{id}/delete")
    public String deleteVocabulary(@PathVariable Long id,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   RedirectAttributes redirectAttributes) {
        vocabularyRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Word deleted successfully.");
        return "redirect:/admin/vocabulary?page=" + page + "&size=" + size;
    }

    // ----------------------------------------------------------------
    // Manage Quiz Questions
    // ----------------------------------------------------------------
    @GetMapping("/questions")
    public String listQuestions(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "") String search,
                                HttpSession session,
                                Model model) {
        User admin = getCurrentAdmin(session);
        model.addAttribute("admin", admin);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<QuizQuestion> questionPage;
        if (search != null && !search.isBlank()) {
            questionPage = quizQuestionRepository.searchPaged(search.trim(), pageable);
        } else {
            questionPage = quizQuestionRepository.findAll(pageable);
        }

        model.addAttribute("questionPage", questionPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", questionPage.getTotalPages());
        model.addAttribute("totalElements", questionPage.getTotalElements());
        model.addAttribute("search", search);
        return "admin/questions";
    }

    @PostMapping("/questions/{id}/delete")
    public String deleteQuestion(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        quizQuestionRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Question deleted successfully.");
        return "redirect:/admin/questions";
    }

    // ----------------------------------------------------------------
    // Manage Categories
    // ----------------------------------------------------------------
    @GetMapping("/categories")
    public String listCategories(@RequestParam(defaultValue = "") String search,
                                 HttpSession session,
                                 Model model) {
        User admin = getCurrentAdmin(session);
        model.addAttribute("admin", admin);
        List<Category> categories;
        if (search != null && !search.isBlank()) {
            categories = categoryRepository.searchByName(search.trim());
        } else {
            categories = categoryRepository.findAll(Sort.by("id").ascending());
        }
        // Word count per category
        Map<Long, Long> wordCountMap = new HashMap<>();
        for (Category c : categories) {
            wordCountMap.put(c.getId(), vocabularyRepository.countByCategoryId(c.getId()));
        }
        model.addAttribute("categories", categories);
        model.addAttribute("wordCountMap", wordCountMap);
        model.addAttribute("search", search);
        return "admin/categories";
    }

    @PostMapping("/categories/add")
    public String addCategory(@RequestParam String nameEn,
                              @RequestParam String nameKr,
                              @RequestParam String iconName,
                              @RequestParam String colorTheme,
                              @RequestParam(required = false) String description,
                              @RequestParam(defaultValue = "false") boolean isPopular,
                              RedirectAttributes redirectAttributes) {
        Category cat = new Category();
        cat.setNameEn(nameEn.trim());
        cat.setNameKr(nameKr.trim());
        cat.setIconName(iconName.trim());
        cat.setColorTheme(colorTheme.trim());
        cat.setDescription(description != null && !description.isBlank() ? description.trim() : null);
        cat.setPopular(isPopular);
        categoryRepository.save(cat);
        redirectAttributes.addFlashAttribute("success", "Category \"" + cat.getNameEn() + "\" added successfully.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        long wordCount = vocabularyRepository.countByCategoryId(id);
        if (wordCount > 0) {
            redirectAttributes.addFlashAttribute("error",
                "Cannot delete: this category still has " + wordCount + " vocabulary word(s).");
            return "redirect:/admin/categories";
        }
        categoryRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Category deleted successfully.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/edit")
    public String editCategory(@PathVariable Long id,
                               @RequestParam String nameEn,
                               @RequestParam String nameKr,
                               @RequestParam String iconName,
                               @RequestParam String colorTheme,
                               @RequestParam(required = false) String description,
                               @RequestParam(defaultValue = "false") boolean isPopular,
                               RedirectAttributes redirectAttributes) {
        Category cat = categoryRepository.findById(id).orElse(null);
        if (cat == null) {
            redirectAttributes.addFlashAttribute("error", "Category not found.");
            return "redirect:/admin/categories";
        }
        cat.setNameEn(nameEn.trim());
        cat.setNameKr(nameKr.trim());
        cat.setIconName(iconName.trim());
        cat.setColorTheme(colorTheme.trim());
        cat.setDescription(description != null && !description.isBlank() ? description.trim() : null);
        cat.setPopular(isPopular);
        categoryRepository.save(cat);
        redirectAttributes.addFlashAttribute("success", "Category updated successfully.");
        return "redirect:/admin/categories";
    }

    // ----------------------------------------------------------------
    // Edit Vocabulary
    // ----------------------------------------------------------------
    @PostMapping("/vocabulary/{id}/edit")
    public String editVocabulary(@PathVariable Long id,
                                 @RequestParam String koreanWord,
                                 @RequestParam String romaji,
                                 @RequestParam String englishMeaning,
                                 @RequestParam(required = false) String wordType,
                                 @RequestParam Long categoryId,
                                 @RequestParam(required = false) String exampleKr,
                                 @RequestParam(required = false) String exampleEn,
                                 @RequestParam(required = false) String mnemonic,
                                 @RequestParam(required = false) String vietnameseMeaning,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "") String search,
                                 RedirectAttributes redirectAttributes) {
        Vocabulary vocab = vocabularyRepository.findById(id).orElse(null);
        if (vocab == null) {
            redirectAttributes.addFlashAttribute("error", "Word not found.");
            return "redirect:/admin/vocabulary";
        }
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid category selected.");
            return "redirect:/admin/vocabulary";
        }
        vocab.setKoreanWord(koreanWord.trim());
        vocab.setRomaji(romaji.trim());
        vocab.setEnglishMeaning(englishMeaning.trim());
        vocab.setWordType(wordType != null && !wordType.isBlank() ? wordType.trim() : null);
        vocab.setCategory(category);
        vocab.setExampleKr(exampleKr != null && !exampleKr.isBlank() ? exampleKr.trim() : null);
        vocab.setExampleEn(exampleEn != null && !exampleEn.isBlank() ? exampleEn.trim() : null);
        vocab.setMnemonic(mnemonic != null && !mnemonic.isBlank() ? mnemonic.trim() : null);
        vocab.setVietnameseMeaning(vietnameseMeaning != null && !vietnameseMeaning.isBlank() ? vietnameseMeaning.trim() : null);
        vocabularyRepository.save(vocab);
        redirectAttributes.addFlashAttribute("success", "Word \"" + vocab.getKoreanWord() + "\" updated successfully.");
        return "redirect:/admin/vocabulary?page=" + page + "&size=" + size
               + (search.isBlank() ? "" : "&search=" + search);
    }

    // ----------------------------------------------------------------
    // Manage Vocab Groups
    // ----------------------------------------------------------------
    @GetMapping("/vocab-groups")
    public String listVocabGroups(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  HttpSession session,
                                  Model model) {
        User admin = getCurrentAdmin(session);
        model.addAttribute("admin", admin);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<VocabGroup> groupPage = vocabGroupRepository.findAll(pageable);

        // Word count per group
        Map<Long, Long> wordCountMap = new HashMap<>();
        for (VocabGroup g : groupPage.getContent()) {
            wordCountMap.put(g.getId(), vocabGroupItemRepository.countByGroupId(g.getId()));
        }

        // Owner name per group
        Map<Long, String> ownerNameMap = new HashMap<>();
        for (VocabGroup g : groupPage.getContent()) {
            userRepository.findById(g.getUserId()).ifPresent(u -> ownerNameMap.put(g.getId(), u.getFullName()));
        }

        model.addAttribute("groupPage", groupPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", groupPage.getTotalPages());
        model.addAttribute("totalElements", groupPage.getTotalElements());
        model.addAttribute("wordCountMap", wordCountMap);
        model.addAttribute("ownerNameMap", ownerNameMap);
        return "admin/vocab-groups";
    }

    @PostMapping("/vocab-groups/{id}/delete")
    public String deleteVocabGroup(@PathVariable Long id,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   RedirectAttributes redirectAttributes) {
        vocabGroupItemRepository.deleteByGroupId(id);
        vocabGroupRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Vocab group deleted successfully.");
        return "redirect:/admin/vocab-groups?page=" + page + "&size=" + size;
    }
}
