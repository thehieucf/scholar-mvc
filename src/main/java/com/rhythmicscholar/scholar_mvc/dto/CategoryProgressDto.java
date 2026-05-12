package com.rhythmicscholar.scholar_mvc.dto;

/**
 * DTO chứa thông tin tiến trình học tập của người dùng theo từng danh mục (category).
 * Được dùng để truyền dữ liệu tổng hợp từ controller ra template.
 */
public class CategoryProgressDto {

    private Long categoryId;
    private String nameEn;
    private String nameKr;
    private String iconName;
    private String colorTheme;
    private boolean isPopular;

    /** Tổng số từ trong category */
    private long totalWords;

    /** Số từ người dùng đã học ít nhất 1 lần */
    private long studiedWords;

    /** Phần trăm hoàn thành (0-100) */
    private int completionPercentage;

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
        this.completionPercentage = totalWords > 0
                ? (int) Math.round((studiedWords * 100.0) / totalWords)
                : 0;
    }

    public Long getCategoryId() { return categoryId; }
    public String getNameEn() { return nameEn; }
    public String getNameKr() { return nameKr; }
    public String getIconName() { return iconName; }
    public String getColorTheme() { return colorTheme; }
    public boolean isPopular() { return isPopular; }
    public long getTotalWords() { return totalWords; }
    public long getStudiedWords() { return studiedWords; }
    public int getCompletionPercentage() { return completionPercentage; }
}
