package com.rhythmicscholar.scholar_mvc.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "name_kr", nullable = false)
    private String nameKr;

    @Column(name = "icon_name", nullable = false)
    private String iconName;

    @Column(name = "color_theme", nullable = false)
    private String colorTheme;

    @Column(name = "is_popular")
    private Boolean isPopular = false;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
