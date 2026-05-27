package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.UserWordProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserWordProgressRepository extends JpaRepository<UserWordProgress, Long> {
    
    @Query("SELECT COUNT(DISTINCT u.vocabulary.id) FROM UserWordProgress u WHERE u.user.id = :userId AND u.lastStudiedAt >= :startOfDay")
    long countStudiedToday(@Param("userId") Long userId, @Param("startOfDay") LocalDateTime startOfDay);

    List<UserWordProgress> findByUserIdAndLastStudiedAtGreaterThanEqual(Long userId, LocalDateTime startOfDay);

    Optional<UserWordProgress> findByUserIdAndVocabularyId(Long userId, Long vocabularyId);

    /**
     * Đếm số từ vựng mà người dùng đã học ít nhất 1 lần trong một category cụ thể.
     * Từ "đã học" là các từ có lastStudiedAt != null (đã từng được học).
     */
    @Query("SELECT COUNT(DISTINCT u.vocabulary.id) FROM UserWordProgress u " +
           "WHERE u.user.id = :userId AND u.vocabulary.category.id = :categoryId " +
           "AND u.lastStudiedAt IS NOT NULL")
    long countStudiedWordsByCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    /**
     * Đếm số người dùng phân biệt đã học ít nhất 1 từ trong một ngày cụ thể.
     * Dùng để vẽ biểu đồ "Active Learners per Day" trên dashboard.
     */
    @Query("SELECT COUNT(DISTINCT u.user.id) FROM UserWordProgress u " +
           "WHERE u.lastStudiedAt >= :dayStart AND u.lastStudiedAt < :dayEnd")
    long countDistinctUsersByDay(@Param("dayStart") LocalDateTime dayStart,
                                  @Param("dayEnd") LocalDateTime dayEnd);

    /**
     * Đếm số từ theo từng trạng thái học (NEW, LEARNING, MASTERED) trên toàn hệ thống.
     * Trả về mảng Object[]{learningStatus, count}.
     */
    @Query("SELECT u.learningStatus, COUNT(u) FROM UserWordProgress u GROUP BY u.learningStatus")
    List<Object[]> countByLearningStatus();

    /**
     * Lấy top N từ vựng được nhiều người dùng học nhất (có bản ghi progress).
     * Trả về mảng Object[]{koreanWord, englishMeaning, userCount}.
     */
    @Query("SELECT u.vocabulary.koreanWord, u.vocabulary.englishMeaning, COUNT(DISTINCT u.user.id) " +
           "FROM UserWordProgress u " +
           "GROUP BY u.vocabulary.id, u.vocabulary.koreanWord, u.vocabulary.englishMeaning " +
           "ORDER BY COUNT(DISTINCT u.user.id) DESC")
    List<Object[]> findTopStudiedWords(org.springframework.data.domain.Pageable pageable);

    /**
     * Đếm số từ có trạng thái MASTERED của một user.
     */
    @Query("SELECT COUNT(u) FROM UserWordProgress u WHERE u.user.id = :userId AND u.learningStatus = 'MASTERED'")
    long countMasteredByUserId(@Param("userId") Long userId);

    /**
     * Đếm tổng số từ đã học (bất kỳ trạng thái) của một user.
     */
    @Query("SELECT COUNT(u) FROM UserWordProgress u WHERE u.user.id = :userId AND u.lastStudiedAt IS NOT NULL")
    long countStudiedByUserId(@Param("userId") Long userId);

    /**
     * Lấy danh sách tiến độ học gần đây nhất của một user (có kèm vocabulary + category).
     * Dùng JPQL với Pageable — JOIN FETCH chỉ trên @ManyToOne nên không gây HHH90003004.
     */
    @Query("SELECT u FROM UserWordProgress u JOIN FETCH u.vocabulary v JOIN FETCH v.category " +
           "WHERE u.user.id = :userId AND u.lastStudiedAt IS NOT NULL " +
           "ORDER BY u.lastStudiedAt DESC")
    List<UserWordProgress> findRecentByUserId(@Param("userId") Long userId,
                                              org.springframework.data.domain.Pageable pageable);

    /**
     * Bước 2: fetch đầy đủ entities theo IDs (JOIN FETCH vocabulary + category).
     */
    @Query("SELECT u FROM UserWordProgress u JOIN FETCH u.vocabulary v JOIN FETCH v.category " +
           "WHERE u.id IN :ids ORDER BY u.lastStudiedAt DESC")
    List<UserWordProgress> findByIdsWithVocabulary(@Param("ids") List<Long> ids);

    /**
     * Đếm số từ theo từng trạng thái học của một user cụ thể.
     */
    @Query("SELECT u.learningStatus, COUNT(u) FROM UserWordProgress u " +
           "WHERE u.user.id = :userId GROUP BY u.learningStatus")
    List<Object[]> countByLearningStatusForUser(@Param("userId") Long userId);

    /**
     * Đếm số ngày học phân biệt của một user (dùng để hiển thị tổng ngày đã học).
     */
    @Query(value = "SELECT COUNT(DISTINCT DATE(last_studied_at)) FROM user_word_progress " +
                   "WHERE user_id = :userId AND last_studied_at IS NOT NULL",
           nativeQuery = true)
    long countDistinctStudyDaysByUserId(@Param("userId") Long userId);
}
