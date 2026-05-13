package com.rhythmicscholar.scholar_mvc.controller.user;

import com.rhythmicscholar.scholar_mvc.model.Category;
import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.model.Vocabulary;
import com.rhythmicscholar.scholar_mvc.model.VocabGroup;
import com.rhythmicscholar.scholar_mvc.repository.CategoryRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserRepository;
import com.rhythmicscholar.scholar_mvc.repository.VocabularyRepository;
import com.rhythmicscholar.scholar_mvc.repository.VocabGroupItemRepository;
import com.rhythmicscholar.scholar_mvc.repository.VocabGroupRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller xử lý các yêu cầu liên quan đến từ vựng (Vocabulary).
 * Bao gồm các trang thư viện, từ điển và API lấy từ vựng theo danh mục.
 */
@Controller
public class VocabularyController {
    private static final Logger logger = LoggerFactory.getLogger(VocabularyController.class);

    private final CategoryRepository categoryRepository;
    private final VocabularyRepository vocabularyRepository;
    private final UserRepository userRepository;
    private final VocabGroupRepository vocabGroupRepository;
    private final VocabGroupItemRepository vocabGroupItemRepository;

    public VocabularyController(CategoryRepository categoryRepository,
                                VocabularyRepository vocabularyRepository,
                                UserRepository userRepository,
                                VocabGroupRepository vocabGroupRepository,
                                VocabGroupItemRepository vocabGroupItemRepository) {
        this.categoryRepository = categoryRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.userRepository = userRepository;
        this.vocabGroupRepository = vocabGroupRepository;
        this.vocabGroupItemRepository = vocabGroupItemRepository;
    }

    /**
     * Hiển thị trang từ vựng cá nhân của người dùng.
     * Truyền danh sách nhóm từ vựng và từng số lượng từ vào model.
     * Endpoint: /vocabulary hoặc /vocabulary.html
     */
    @GetMapping({"/vocabulary", "/vocabulary.html"})
    public String vocabulary(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;

        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("user", user);

        // Lấy danh sách nhóm của user kèm wordCount
        List<VocabGroup> groups = vocabGroupRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Map<String, Object>> groupsWithCount = groups.stream().map(g -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", g.getId());
            map.put("name", g.getName());
            map.put("colorTheme", g.getColorTheme());
            map.put("iconEmoji", g.getIconEmoji());
            map.put("wordCount", vocabGroupItemRepository.countByGroupId(g.getId()));
            return map;
        }).toList();
        model.addAttribute("groups", groupsWithCount);

        return "user/vocabulary";
    }

    /**
     * Trang từ điển cho phép tìm kiếm từ vựng.
     * Endpoint: /dictionary hoặc /dictionary.html
     * @param query Từ khóa tìm kiếm (tiếng Hàn hoặc nghĩa tiếng Anh)
     */
    @GetMapping({"/dictionary", "/dictionary.html"})
    public String dictionary(HttpSession session, @RequestParam(value = "q", required = false) String query, Model model) {
        // Nếu có từ khóa tìm kiếm, thực hiện truy vấn
        if (query != null && !query.isEmpty()) {
            List<Vocabulary> results = vocabularyRepository.findByKoreanWordContainingOrEnglishMeaningContaining(query, query);
            model.addAttribute("results", results);
            model.addAttribute("query", query);
        }
        
        // Lấy thông tin user (để hiển thị avatar/level trên header)
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;
        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("user", user);
        
        return "user/dictionary";
    }

    /**
     * Trang thư viện hiển thị các danh mục từ vựng (Categories).
     * Endpoint: /library hoặc /library.html
     */
    @GetMapping({"/library", "/library.html"})
    public String library(HttpSession session, Model model) {
        // Lấy tất cả danh mục (Food, Travel, Work, v.v.)
        List<Category> categories = categoryRepository.findAll();
        
        // Lấy thông tin user
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;
        User user = userRepository.findById(userId).orElse(null);
        
        model.addAttribute("categories", categories);
        model.addAttribute("user", user);
        
        return "user/library";
    }

    /**
     * API trả về danh sách từ vựng thuộc một danh mục cụ thể.
     * Được gọi bởi JavaScript (AJAX) khi người dùng nhấn vào một danh mục trong Library.
     * Endpoint: /api/categories/{id}/vocabularies
     */
    @GetMapping("/api/categories/{id}/vocabularies")
    @ResponseBody
    public List<Vocabulary> getVocabulariesByCategoryId(@PathVariable("id") Long id) {
        logger.info("Fetching vocabularies for category ID: {}", id);
        List<Vocabulary> vocabularies = vocabularyRepository.findByCategoryId(id);
        logger.info("Found {} vocabularies", vocabularies.size());
        return vocabularies;
    }

    /**
     * API trả về toàn bộ từ vựng trong hệ thống (dùng cho modal tìm kiếm trong vocabulary page).
     * Endpoint: /api/vocabularies
     */
    @GetMapping("/api/vocabularies")
    @ResponseBody
    public List<Map<String, Object>> getAllVocabularies() {
        return vocabularyRepository.findAll().stream().map(v -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", v.getId());
            map.put("koreanWord", v.getKoreanWord());
            map.put("romaji", v.getRomaji());
            map.put("englishMeaning", v.getEnglishMeaning());
            map.put("wordType", v.getWordType());
            return map;
        }).toList();
    }
}
