package com.rhythmicscholar.scholar_mvc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho một từ vựng trong hệ thống.
 * Chứa thông tin về từ tiếng Hàn, phiên âm, nghĩa tiếng Anh và ví dụ.
 */
@Entity
@Table(name = "vocabularies")
@Data
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Danh mục mà từ vựng này thuộc về (VD: Food, Travel).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;

    /**
     * Từ tiếng Hàn (Hangul).
     */
    @Column(name = "korean_word", nullable = false)
    private String koreanWord;

    /**
     * Phiên âm Latinh (Romaji).
     */
    @Column(nullable = false)
    private String romaji;

    /**
     * Nghĩa tiếng Anh.
     */
    @Column(name = "english_meaning", nullable = false)
    private String englishMeaning;

    /**
     * Loại từ (Noun, Verb, Adjective, v.v.).
     */
    @Column(name = "word_type")
    private String wordType;

    /**
     * Câu ví dụ bằng tiếng Hàn.
     */
    @Column(name = "example_kr", columnDefinition = "TEXT")
    private String exampleKr;

    /**
     * Dịch câu ví dụ sang tiếng Anh.
     */
    @Column(name = "example_en", columnDefinition = "TEXT")
    private String exampleEn;

    /**
     * Ghi chú hoặc cách ghi nhớ từ (Mnemonic).
     */
    @Column(columnDefinition = "TEXT")
    private String mnemonic;

    /**
     * Thời điểm tạo bản ghi.
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
