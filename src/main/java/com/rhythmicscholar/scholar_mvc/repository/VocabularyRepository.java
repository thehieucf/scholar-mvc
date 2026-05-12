package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    List<Vocabulary> findByCategoryId(Long categoryId);
    long countByCategoryId(Long categoryId);
    List<Vocabulary> findByKoreanWordContainingOrEnglishMeaningContaining(String korean, String english);
    @Query(value = "SELECT category_id, COUNT(*) as count FROM vocabularies GROUP BY category_id", nativeQuery = true)
    List<Object[]> countAllWordsGroupedByCategory();

    List<Vocabulary> findTop10ByOrderById();
    List<Vocabulary> findByCreatedAtAfter(LocalDateTime dateTime);
}
