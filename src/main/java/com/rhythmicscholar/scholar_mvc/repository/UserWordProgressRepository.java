package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.UserWordProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserWordProgressRepository extends JpaRepository<UserWordProgress, Long> {
    
    @Query("SELECT COUNT(u) FROM UserWordProgress u WHERE u.user.id = :userId AND u.lastStudiedAt >= :startOfDay")
    long countStudiedToday(@Param("userId") Long userId, @Param("startOfDay") LocalDateTime startOfDay);

    List<UserWordProgress> findByUserIdAndLastStudiedAtGreaterThanEqual(Long userId, LocalDateTime startOfDay);

    /**
     * Đếm số từ vựng mà người dùng đã học ít nhất 1 lần trong một category cụ thể.
     * Từ "đã học" là các từ có lastStudiedAt != null (đã từng được học).
     */
    @Query("SELECT COUNT(DISTINCT u.vocabulary.id) FROM UserWordProgress u " +
           "WHERE u.user.id = :userId AND u.vocabulary.category.id = :categoryId " +
           "AND u.lastStudiedAt IS NOT NULL")
    long countStudiedWordsByCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);
}
