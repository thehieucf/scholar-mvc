package com.rhythmicscholar.scholar_mvc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho một từ vựng bên trong một nhóm từ vựng cá nhân.
 * Lưu nội dung từ trực tiếp (không FK vào vocabularies) để người dùng
 * có thể thêm cả từ tùy chỉnh lẫn từ có sẵn trong hệ thống.
 */
@Entity
@Table(name = "vocab_group_items")
@Data
public class VocabGroupItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID của nhóm từ vựng chứa từ này.
     */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /**
     * Từ tiếng Hàn (Hangul).
     */
    @Column(name = "korean_word", nullable = false, length = 100)
    private String koreanWord;

    /**
     * Phiên âm Latinh (Romaji).
     */
    @Column(length = 100)
    private String romaji;

    /**
     * Nghĩa tiếng Anh.
     */
    @Column(name = "english_meaning", nullable = false, length = 255)
    private String englishMeaning;

    /**
     * Loại từ: NOUN, VERB, ADJECTIVE, ADVERB, EXPRESSION, OTHER.
     */
    @Column(name = "word_type", length = 50)
    private String wordType;

    /**
     * Thời điểm thêm từ vào nhóm.
     */
    @Column(name = "added_at", insertable = false, updatable = false)
    private LocalDateTime addedAt;
}
