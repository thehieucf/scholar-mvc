package com.rhythmicscholar.scholar_mvc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho một câu hỏi trắc nghiệm trong hệ thống Quiz.
 * Mỗi câu hỏi có 1 đáp án đúng và 3 đáp án sai để tạo thành bài trắc nghiệm 4 lựa chọn.
 */
@Entity
@Table(name = "quiz_questions")
@Data
public class QuizQuestion {

    /** Khóa chính, tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Chủ đề của câu hỏi (ví dụ: "Food", "Greetings").
     * Dùng để nhóm câu hỏi theo chủ đề khi lọc.
     */
    @Column(nullable = false)
    private String topic;

    /**
     * Loại câu hỏi, xác định cách hiển thị trên giao diện.
     * Ví dụ: "MEANING" (đoán nghĩa), "SPELLING" (đoán cách viết).
     */
    @Column(name = "question_type", nullable = false)
    private String questionType;

    /** Nội dung câu hỏi bằng tiếng Hàn (Hangul). */
    @Column(name = "korean_text", nullable = false)
    private String koreanText;

    /** Phiên âm Latinh của câu hỏi, giúp người học đọc đúng phát âm. */
    @Column(nullable = false)
    private String romaji;

    /** Đáp án đúng của câu hỏi. */
    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    /** Đáp án sai thứ nhất (nhiễu). */
    @Column(name = "wrong_answer_1", nullable = false)
    private String wrongAnswer1;

    /** Đáp án sai thứ hai (nhiễu). */
    @Column(name = "wrong_answer_2", nullable = false)
    private String wrongAnswer2;

    /** Đáp án sai thứ ba (nhiễu). */
    @Column(name = "wrong_answer_3", nullable = false)
    private String wrongAnswer3;

    /**
     * ID của từ vựng liên kết với câu hỏi này (tùy chọn).
     * Dùng để lọc câu hỏi theo danh sách từ vựng đã học.
     */
    @Column(name = "vocabulary_id")
    private Long vocabularyId;

    /**
     * Thời điểm tạo câu hỏi, được database tự động gán.
     * Không cho phép insert/update từ phía ứng dụng.
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
