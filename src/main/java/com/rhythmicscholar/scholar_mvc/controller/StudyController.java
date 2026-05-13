package com.rhythmicscholar.scholar_mvc.controller;

import com.rhythmicscholar.scholar_mvc.model.QuizQuestion;
import com.rhythmicscholar.scholar_mvc.model.Vocabulary;
import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.repository.QuizQuestionRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserRepository;
import com.rhythmicscholar.scholar_mvc.repository.VocabularyRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller xử lý các chức năng học tập (Study) và trò chơi (Game/Quiz).
 */
@Controller
public class StudyController {

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Trang học tập (Flashcards).
     * @param categoryId ID của danh mục muốn học (tùy chọn)
     * @param filter Bộ lọc (VD: "today" để lấy từ mới thêm hôm nay)
     */
    @GetMapping({"/study", "/study.html"})
    public String study(HttpSession session,
                        @RequestParam(value = "id", required = false) Long categoryId,
                        @RequestParam(value = "filter", required = false) String filter,
                        Model model) {
        List<Vocabulary> vocabList;
        
        // Lọc từ vựng dựa trên tham số filter hoặc categoryId
        if ("today".equals(filter)) {
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
            vocabList = vocabularyRepository.findByCreatedAtAfter(todayStart);
            
            // Nếu hôm nay chưa có từ mới, lấy mặc định tất cả để người dùng vẫn có thể học
            if (vocabList.isEmpty()) {
                vocabList = vocabularyRepository.findAll();
            }
        } else if (categoryId != null) {
            // Lấy từ vựng theo danh mục cụ thể
            vocabList = vocabularyRepository.findByCategoryId(categoryId);
        } else {
            // Lấy tất cả từ vựng
            vocabList = vocabularyRepository.findAll();
        }
        
        // Giới hạn tối đa 10 từ mỗi lượt học để tránh quá tải
        if (vocabList.size() > 10) {
            vocabList = vocabList.subList(0, 10);
        }
        
        // Lấy thông tin người dùng từ session
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;
        User user = userRepository.findById(userId).orElse(null);
        
        model.addAttribute("user", user);
        model.addAttribute("vocabList", vocabList);
        return "study";
    }

    /**
     * Trang trò chơi trắc nghiệm (Quiz Game).
     * @param vocabIds Danh sách ID từ vựng để tạo câu hỏi tương ứng (tùy chọn)
     */
    @GetMapping({"/game", "/game.html"})
    public String game(HttpSession session, @RequestParam(value = "vocabIds", required = false) List<Long> vocabIds, Model model) {
        List<QuizQuestion> questions;
        
        // Lấy danh sách câu hỏi dựa trên các từ vựng đã chọn
        if (vocabIds != null && !vocabIds.isEmpty()) {
            questions = quizQuestionRepository.findByVocabularyIdIn(vocabIds);
        } else {
            // Mặc định lấy tất cả câu hỏi nếu không có bộ lọc
            questions = quizQuestionRepository.findAll();
        }
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) userId = 1L;
        User user = userRepository.findById(userId).orElse(null);
        
        model.addAttribute("user", user);
        model.addAttribute("questions", questions);
        return "game";
    }
}
