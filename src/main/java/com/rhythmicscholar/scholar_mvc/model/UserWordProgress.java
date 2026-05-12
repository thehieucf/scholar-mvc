package com.rhythmicscholar.scholar_mvc.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity theo dõi tiến độ học tập của một User đối với một từ vựng (Vocabulary) cụ thể.
 * Lưu trữ trạng thái học (NEW, LEARNING, MASTERED), ngày ôn tập tiếp theo và số lần trả lời đúng liên tiếp.
 */
@Entity
@Table(name = "user_word_progress")
@Data
@NoArgsConstructor
public class UserWordProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Người dùng đang học từ này.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Từ vựng đang được học.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocabulary_id", nullable = false)
    private Vocabulary vocabulary;

    /**
     * Trạng thái học tập: NEW (Mới), LEARNING (Đang học), MASTERED (Đã thành thạo).
     */
    @Column(name = "learning_status")
    private String learningStatus = "NEW";

    /**
     * Ngày dự kiến người dùng cần ôn tập lại từ này (theo phương pháp Spaced Repetition).
     */
    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    /**
     * Đánh dấu nếu người dùng đã hoàn thành phần học Flashcard cho từ này.
     */
    @Column(name = "is_flashcard_done")
    private Boolean isFlashcardDone = false;

    /**
     * Đánh dấu nếu người dùng đã hoàn thành phần Quiz cho từ này.
     */
    @Column(name = "is_quiz_done")
    private Boolean isQuizDone = false;

    /**
     * Số lần người dùng trả lời đúng liên tiếp trong các bài kiểm tra.
     * Dùng để tính toán khoảng thời gian ôn tập tiếp theo.
     */
    @Column(name = "consecutive_correct")
    private Integer consecutiveCorrect = 0;

    /**
     * Thời điểm cuối cùng người dùng học từ này.
     */
    @UpdateTimestamp
    @Column(name = "last_studied_at")
    private LocalDateTime lastStudiedAt;
}
