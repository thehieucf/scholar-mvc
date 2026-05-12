package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.UserWordProgress;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;

@Repository
public interface UserWordProgressRepository extends JpaRepository<UserWordProgress, Long> {
    
    Optional<UserWordProgress> findByUserIdAndVocabularyId(Long userId, Long vocabularyId);

    @Query("SELECT COUNT(u) FROM UserWordProgress u WHERE u.user.id = :userId AND u.lastStudiedAt >= :startOfDay")
    long countStudiedToday(@Param("userId") Long userId, @Param("startOfDay") LocalDateTime startOfDay);

    List<UserWordProgress> findByUserIdAndLastStudiedAtGreaterThanEqual(Long userId, LocalDateTime startOfDay);

    @Query(value = "SELECT v.category_id, COUNT(DISTINCT u.vocabulary_id) FROM user_word_progress u " +
           "JOIN vocabularies v ON u.vocabulary_id = v.id " +
           "WHERE u.user_id = :userId AND u.last_studied_at IS NOT NULL " +
           "GROUP BY v.category_id", nativeQuery = true)
    List<Object[]> countStudiedWordsGroupedByCategory(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"vocabulary", "vocabulary.category"})
    List<UserWordProgress> findTop10ByUserIdOrderByLastStudiedAtDesc(Long userId);
}
