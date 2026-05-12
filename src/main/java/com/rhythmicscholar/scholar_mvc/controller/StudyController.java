package com.rhythmicscholar.scholar_mvc.controller;

import com.rhythmicscholar.scholar_mvc.model.User;
import com.rhythmicscholar.scholar_mvc.model.Vocabulary;
import com.rhythmicscholar.scholar_mvc.model.QuizQuestion;
import com.rhythmicscholar.scholar_mvc.model.UserWordProgress;
import com.rhythmicscholar.scholar_mvc.repository.QuizQuestionRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserRepository;
import com.rhythmicscholar.scholar_mvc.repository.UserWordProgressRepository;
import com.rhythmicscholar.scholar_mvc.repository.VocabularyRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Controller xử lý các chức năng học tập (Study) và trò chơi (Game/Quiz).
 */
@Controller
public class StudyController {

    private final VocabularyRepository vocabularyRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final UserRepository userRepository;
    private final UserWordProgressRepository userWordProgressRepository;

    public StudyController(VocabularyRepository vocabularyRepository,
                           QuizQuestionRepository quizQuestionRepository,
                           UserRepository userRepository,
                           UserWordProgressRepository userWordProgressRepository) {
        this.vocabularyRepository = vocabularyRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.userRepository = userRepository;
        this.userWordProgressRepository = userWordProgressRepository;
    }

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
            // Lấy 10 từ vựng đầu tiên thay vì lấy tất cả để tối ưu hiệu năng
            vocabList = vocabularyRepository.findTop10ByOrderById();
        }
        
        // Giới hạn tối đa 10 từ mỗi lượt học để tránh quá tải
        if (vocabList.size() > 10) {
            vocabList = vocabList.subList(0, 10);
        }
        
        // Lấy thông tin người dùng từ session
        Long userId = getUserIdFromSession(session);
        User user = userRepository.findById(userId).orElse(null);
        
        model.addAttribute("user", user);
        model.addAttribute("vocabList", vocabList);
        return "user/study";
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
            // Mặc định lấy 10 câu hỏi đầu tiên để tối ưu hiệu năng
            questions = quizQuestionRepository.findTop10ByOrderById();
        }
        
        Long userId = getUserIdFromSession(session);
        User user = userRepository.findById(userId).orElse(null);
        
        model.addAttribute("user", user);
        model.addAttribute("questions", questions);
        return "user/game";
    }

    /**
     * API lưu tiến độ học tập (từ Flashcard hoặc Quiz).
     */
    @PostMapping("/api/progress/save")
    public ResponseEntity<?> saveProgress(HttpSession session, @RequestBody Map<String, Object> payload) {
        Long userId = getUserIdFromSession(session);

        Object vocabIdsObj = payload.get("vocabIds");
        if (!(vocabIdsObj instanceof List<?> vocabIds) || vocabIds.isEmpty()) {
            return ResponseEntity.badRequest().body("No vocab IDs provided");
        }
        String type = (String) payload.get("type"); // "flashcard" or "quiz"

        User user = userRepository.findById(userId).orElseThrow();

        for (Object idObj : vocabIds) {
            Long vocabId = ((Number) idObj).longValue();
            Vocabulary vocab = vocabularyRepository.findById(vocabId).orElse(null);
            if (vocab == null) continue;

            UserWordProgress progress = userWordProgressRepository
                    .findByUserIdAndVocabularyId(userId, vocabId)
                    .orElse(new UserWordProgress());

            progress.setUser(user);
            progress.setVocabulary(vocab);

            if ("flashcard".equals(type)) {
                progress.setIsFlashcardDone(true);
            } else if ("quiz".equals(type)) {
                progress.setIsQuizDone(true);
                // Tăng số lần đúng liên tiếp (giả định đây là bài học thành công)
                progress.setConsecutiveCorrect(progress.getConsecutiveCorrect() + 1);
            }

            // Mặc định gán trạng thái LEARNING nếu chưa MASTERED
            if (!"MASTERED".equals(progress.getLearningStatus())) {
                progress.setLearningStatus("LEARNING");
            }

            // Gán ngày ôn tập tiếp theo (giả định 1 ngày sau)
            progress.setNextReviewDate(LocalDate.now().plusDays(1));

            userWordProgressRepository.save(progress);
        }

        return ResponseEntity.ok().body(Map.of("success", true, "message", "Progress saved"));
    }

    private Long getUserIdFromSession(HttpSession session) {
        Object sessionUserId = session.getAttribute("userId");
        if (sessionUserId instanceof Long) return (Long) sessionUserId;
        if (sessionUserId instanceof Integer) return ((Integer) sessionUserId).longValue();
        if (sessionUserId instanceof String) return Long.parseLong((String) sessionUserId);
        return 1L; // Fallback
    }
}
