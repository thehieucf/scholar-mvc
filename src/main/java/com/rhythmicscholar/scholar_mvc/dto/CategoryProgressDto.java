package com.rhythmicscholar.scholar_mvc.dto;

/**
 * DTO (Data Transfer Object) chứa thông tin tiến trình học tập của người dùng
 * theo từng danh mục từ vựng (category).
 *
 * Được dùng để truyền dữ liệu tổng hợp từ controller ra template Thymeleaf,
 * tránh việc gọi nhiều query riêng lẻ trong view.
 */
public class CategoryProgressDto {

    /** ID của danh mục từ vựng. */
    private Long categoryId;

    /** Tên danh mục bằng tiếng Anh (ví dụ: "Food", "Travel"). */
    private String nameEn;

    /** Tên danh mục bằng tiếng Hàn (ví dụ: "음식", "여행"). */
    private String nameKr;

    /** Tên icon Material Symbols đại diện cho danh mục (ví dụ: "restaurant"). */
    private String iconName;

    /** Màu chủ đề của danh mục (ví dụ: "emerald", "blue"). */
    private String colorTheme;

    /** Đánh dấu danh mục có phải là danh mục phổ biến hay không. */
    private boolean isPopular;

    /** Tổng số từ vựng trong danh mục này. */
    private long totalWords;

    /** Số từ người dùng đã học ít nhất 1 lần (có bản ghi UserWordProgress). */
    private long studiedWords;

    /** Phần trăm hoàn thành (0–100), tính từ studiedWords / totalWords. */
    private int completionPercentage;

    /**
     * Constructor khởi tạo DTO và tự động tính completionPercentage.
     *
     * @param categoryId  ID danh mục
     * @param nameEn      Tên tiếng Anh
     * @param nameKr      Tên tiếng Hàn
     * @param iconName    Tên icon
     * @param colorTheme  Màu chủ đề
     * @param isPopular   Có phổ biến không
     * @param totalWords  Tổng số từ trong danh mục
     * @param studiedWords Số từ đã học
     */
    public CategoryProgressDto(Long categoryId, String nameEn, String nameKr,
                               String iconName, String colorTheme, boolean isPopular,
                               long totalWords, long studiedWords) {
        this.categoryId = categoryId;
        this.nameEn = nameEn;
        this.nameKr = nameKr;
        this.iconName = iconName;
        this.colorTheme = colorTheme;
        this.isPopular = isPopular;
        this.totalWords = totalWords;
        this.studiedWords = studiedWords;
        // Tính phần trăm hoàn thành, tránh chia cho 0 khi danh mục chưa có từ nào
        this.completionPercentage = totalWords > 0
                ? (int) Math.round((studiedWords * 100.0) / totalWords)
                : 0;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    /** @return ID của danh mục */
    public Long getCategoryId() { return categoryId; }

    /** @return Tên danh mục tiếng Anh */
    public String getNameEn() { return nameEn; }

    /** @return Tên danh mục tiếng Hàn */
    public String getNameKr() { return nameKr; }

    /** @return Tên icon Material Symbols */
    public String getIconName() { return iconName; }

    /** @return Màu chủ đề */
    public String getColorTheme() { return colorTheme; }

    /** @return true nếu là danh mục phổ biến */
    public boolean isPopular() { return isPopular; }

    /** @return Tổng số từ trong danh mục */
    public long getTotalWords() { return totalWords; }

    /** @return Số từ đã học */
    public long getStudiedWords() { return studiedWords; }

    /** @return Phần trăm hoàn thành (0–100) */
    public int getCompletionPercentage() { return completionPercentage; }
}
