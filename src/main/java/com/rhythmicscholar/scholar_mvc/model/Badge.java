package com.rhythmicscholar.scholar_mvc.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity định nghĩa một loại huy hiệu (Badge) trong hệ thống.
 * Mỗi badge có điều kiện mở khóa riêng (streak, XP, từ đã học...).
 */
@Entity
@Table(name = "badges")
@Data
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tên hiển thị của badge. */
    @Column(nullable = false, unique = true)
    private String name;

    /** Mô tả ngắn về điều kiện đạt được badge. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Icon emoji hiển thị cho badge. */
    @Column(name = "icon_emoji")
    private String iconEmoji;

    /** Màu nền (Tailwind color name: emerald, blue, amber, purple...). */
    @Column(name = "color_theme")
    private String colorTheme;

    /**
     * Loại điều kiện để mở khóa badge:
     * STREAK   — dựa trên currentStreak
     * XP       — dựa trên totalXp
     * MASTERED — dựa trên số từ có learningStatus = MASTERED
     * WORDS    — dựa trên tổng số từ đã học (bất kỳ trạng thái)
     */
    @Column(name = "condition_type", nullable = false)
    private String conditionType;

    /** Ngưỡng giá trị cần đạt để mở khóa badge. */
    @Column(name = "condition_value", nullable = false)
    private Integer conditionValue;

    /** Thứ tự hiển thị (nhỏ hơn = hiển thị trước). */
    @Column(name = "display_order")
    private Integer displayOrder = 0;
}
