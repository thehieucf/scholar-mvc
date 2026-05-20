package com.rhythmicscholar.scholar_mvc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity lưu trữ badge mà một user đã đạt được.
 */
@Entity
@Table(name = "user_badges",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "badge_id"}))
@Data
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    /** Thời điểm user đạt được badge này. */
    @Column(name = "earned_at", insertable = false, updatable = false)
    private LocalDateTime earnedAt;
}
