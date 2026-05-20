package com.rhythmicscholar.scholar_mvc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho một tài khoản người dùng trong hệ thống.
 * Lưu trữ thông tin cá nhân, trạng thái học tập và phân quyền.
 */
@Entity
@Table(name = "users")
@Data
public class User {

    /** Khóa chính, tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Họ và tên đầy đủ của người dùng. Bắt buộc. */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /** Địa chỉ email, dùng để đăng nhập. Phải là duy nhất trong hệ thống. */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Mật khẩu đã được hash (hiện tại lưu plain text cho mục đích demo).
     * Trong môi trường production nên dùng BCrypt hoặc Argon2.
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Trình độ hiện tại của người dùng.
     * Các giá trị: "Beginner", "Intermediate", "Advanced", "Master".
     * Tự động cập nhật dựa trên totalXp.
     */
    @Column(name = "current_level")
    private String currentLevel = "Beginner";

    /**
     * Tổng điểm kinh nghiệm (XP) tích lũy của người dùng.
     * Tăng mỗi khi học từ mới (+5 XP) hoặc ôn lại (+2 XP).
     */
    @Column(name = "total_xp")
    private Integer totalXp = 0;

    /**
     * Số ngày học liên tiếp (streak) hiện tại.
     * Tăng 1 mỗi ngày nếu người dùng học ít nhất 1 từ.
     * Reset về 1 nếu bỏ lỡ một ngày.
     */
    @Column(name = "current_streak")
    private Integer currentStreak = 0;

    /**
     * Chuỗi streak dài nhất từ trước đến nay của người dùng.
     */
    @Column(name = "longest_streak")
    private Integer longestStreak = 0;

    /**
     * Ngày cuối cùng người dùng học (dùng để tính streak reset).
     */
    @Column(name = "last_studied_date")
    private LocalDate lastStudiedDate;

    /** URL ảnh đại diện của người dùng (tùy chọn). */
    @Column(name = "avatar_url")
    private String avatarUrl;

    /**
     * Vai trò của người dùng trong hệ thống.
     * "USER" – người dùng thông thường.
     * "ADMIN" – quản trị viên, có quyền truy cập khu vực /admin.
     * Mặc định là "USER" khi đăng ký mới.
     */
    @Column(nullable = false)
    private String role = "USER";

    /**
     * Thời điểm tạo tài khoản, được database tự động gán.
     * Không cho phép insert/update từ phía ứng dụng.
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
