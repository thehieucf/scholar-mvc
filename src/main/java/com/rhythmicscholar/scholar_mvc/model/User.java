package com.rhythmicscholar.scholar_mvc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "current_level")
    private String currentLevel = "Beginner";

    @Column(name = "total_xp")
    private Integer totalXp = 0;

    @Column(name = "current_streak")
    private Integer currentStreak = 0;

    @Column(name = "avatar_url")
    private String avatarUrl;

    /**
     * Role của người dùng: "USER" hoặc "ADMIN"
     * Mặc định là "USER" khi đăng ký mới.
     */
    @Column(nullable = false)
    private String role = "USER";

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
