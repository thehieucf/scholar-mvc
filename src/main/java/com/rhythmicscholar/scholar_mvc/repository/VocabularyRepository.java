package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    List<Vocabulary> findByCategoryId(Long categoryId);
    List<Vocabulary> findByKoreanWordContainingOrEnglishMeaningContaining(String korean, String english);
    List<Vocabulary> findByCreatedAtAfter(LocalDateTime dateTime);

    long countByCategoryId(Long categoryId);

    /**
     * Lấy top N category có nhiều từ vựng nhất.
     * Trả về mảng Object[]{categoryNameEn, count}.
     */
    @Query("SELECT v.category.nameEn, COUNT(v) FROM Vocabulary v " +
           "GROUP BY v.category.nameEn ORDER BY COUNT(v) DESC")
    List<Object[]> countGroupByCategory();

    /**
     * Tìm kiếm từ vựng theo từ Hàn hoặc nghĩa tiếng Anh,
     * đồng thời JOIN FETCH category để tránh LazyInitializationException khi render template.
     */
    @Query("SELECT v FROM Vocabulary v JOIN FETCH v.category " +
           "WHERE v.koreanWord LIKE %:korean% OR v.englishMeaning LIKE %:english%")
    List<Vocabulary> searchWithCategory(@Param("korean") String korean, @Param("english") String english);

    /** Tìm kiếm từ vựng có phân trang (JOIN FETCH để load category). */
    @Query(value = "SELECT v FROM Vocabulary v JOIN FETCH v.category " +
                   "WHERE LOWER(v.koreanWord) LIKE LOWER(CONCAT('%', :q, '%')) " +
                   "OR LOWER(v.englishMeaning) LIKE LOWER(CONCAT('%', :q, '%')) " +
                   "OR LOWER(v.romaji) LIKE LOWER(CONCAT('%', :q, '%'))",
           countQuery = "SELECT COUNT(v) FROM Vocabulary v " +
                        "WHERE LOWER(v.koreanWord) LIKE LOWER(CONCAT('%', :q, '%')) " +
                        "OR LOWER(v.englishMeaning) LIKE LOWER(CONCAT('%', :q, '%')) " +
                        "OR LOWER(v.romaji) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Vocabulary> searchPaged(@Param("q") String q, Pageable pageable);
}
