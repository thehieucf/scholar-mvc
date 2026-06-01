package com.rhythmicscholar.scholar_mvc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity theo dõi tiến độ hoàn thành của một User trong một Category cụ thể.
 *
 * <p>Ví dụ: User A đã học được 70% số từ trong Category "Food" →
 * có một bản ghi UserProgress với completionPercentage = 70.</p>
 *
 * <p>Constraint UNIQUE(user_id, category_id) đảm bảo mỗi user chỉ có
 * đúng một bản ghi tiến độ cho mỗi danh mục.</p>
 */
@Entity
@Table(name = "user_progress", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "category_id"})})
@Data
public class UserProgress {

    /** Khóa chính, tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Người dùng đang được theo dõi tiến độ.
     * Quan hệ Many-to-One: nhiều bản ghi UserProgress có thể thuộc về 1 User.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Danh mục từ vựng mà tiến độ này thuộc về.
     * Quan hệ Many-to-One: nhiều bản ghi UserProgress có thể thuộc về 1 Category.
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * Phần trăm hoàn thành (0–100).
     * Tính bằng: (số từ đã học / tổng số từ trong danh mục) × 100.
     * Được cập nhật mỗi khi user học thêm một từ trong danh mục này.
     */
    @Column(name = "completion_percentage")
    private Integer completionPercentage = 0;

    /**
     * Thời điểm cuối cùng user học trong danh mục này.
     * Được database tự động cập nhật, không được ghi trực tiếp từ ứng dụng.
     */
    @Column(name = "last_studied_at", insertable = false, updatable = false)
    private LocalDateTime lastStudiedAt;
}
