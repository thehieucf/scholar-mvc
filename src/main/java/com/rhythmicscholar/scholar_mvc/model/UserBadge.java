package com.rhythmicscholar.scholar_mvc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity lưu trữ badge mà một user đã đạt được.
 * Mỗi bản ghi đại diện cho mối quan hệ giữa một User và một Badge đã mở khóa.
 * Constraint UNIQUE(user_id, badge_id) đảm bảo mỗi user chỉ nhận mỗi badge một lần.
 */
@Entity
@Table(name = "user_badges",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "badge_id"}))
@Data
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Người dùng đã đạt được badge. */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Badge mà người dùng đã mở khóa. */
    @ManyToOne
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    /** Thời điểm user đạt được badge này. */
    @Column(name = "earned_at", insertable = false, updatable = false)
    private LocalDateTime earnedAt;
}
