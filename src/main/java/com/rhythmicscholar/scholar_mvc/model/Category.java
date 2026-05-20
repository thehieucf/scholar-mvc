package com.rhythmicscholar.scholar_mvc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho một danh mục từ vựng trong hệ thống.
 * Ví dụ: Food (음식), Travel (여행), Work (직장), v.v.
 * Mỗi từ vựng (Vocabulary) thuộc về đúng một Category.
 */
@Entity
@Table(name = "categories")
@Data
public class Category {

    /** Khóa chính, tự động tăng. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tên danh mục bằng tiếng Anh (ví dụ: "Food"). Bắt buộc. */
    @Column(name = "name_en", nullable = false)
    private String nameEn;

    /** Tên danh mục bằng tiếng Hàn (ví dụ: "음식"). Bắt buộc. */
    @Column(name = "name_kr", nullable = false)
    private String nameKr;

    /** Tên icon Material Symbols dùng để hiển thị trên UI (ví dụ: "restaurant"). */
    @Column(name = "icon_name", nullable = false)
    private String iconName;

    /**
     * Màu chủ đề của danh mục, dùng để tô màu card trên giao diện.
     * Các giá trị hợp lệ: "emerald", "blue", "violet", "rose", "amber", "cyan", "orange", "pink".
     */
    @Column(name = "color_theme", nullable = false)
    private String colorTheme;

    /**
     * Đánh dấu danh mục có phải là danh mục phổ biến hay không.
     * Danh mục phổ biến sẽ được hiển thị nổi bật trên trang Library.
     */
    @Column(name = "is_popular")
    private Boolean popular = false;

    /** Mô tả ngắn về danh mục (tùy chọn). */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Thời điểm tạo bản ghi, được database tự động gán.
     * Không cho phép insert/update từ phía ứng dụng.
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
