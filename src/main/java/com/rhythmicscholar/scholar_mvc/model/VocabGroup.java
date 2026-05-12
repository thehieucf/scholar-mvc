package com.rhythmicscholar.scholar_mvc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho một nhóm từ vựng do người dùng tự tạo.
 * Ví dụ: "Từ vựng du lịch cần nhớ", "Bộ từ ôn thi TOPIK", v.v.
 */
@Entity
@Table(name = "vocab_groups")
@Data
public class VocabGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID của người dùng sở hữu nhóm này.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Tên nhóm từ vựng (do người dùng đặt).
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Màu chủ đề của card nhóm (vd: "emerald", "violet", "rose", "amber").
     */
    @Column(name = "color_theme", length = 50)
    private String colorTheme = "emerald";

    /**
     * Emoji icon đại diện cho nhóm (vd: "📚", "✈️", "🍜").
     */
    @Column(name = "icon_emoji", length = 10)
    private String iconEmoji = "📚";

    /**
     * Thời điểm tạo nhóm.
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
