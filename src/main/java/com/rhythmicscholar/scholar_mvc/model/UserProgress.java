package com.rhythmicscholar.scholar_mvc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "category_id"})})
@Data
public class UserProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "completion_percentage")
    private Integer completionPercentage = 0;

    @Column(name = "last_studied_at", insertable = false, updatable = false)
    private LocalDateTime lastStudiedAt;
}
