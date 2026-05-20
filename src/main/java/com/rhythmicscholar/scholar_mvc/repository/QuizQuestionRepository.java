package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.QuizQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByTopic(String topic);
    List<QuizQuestion> findByVocabularyIdIn(List<Long> vocabularyIds);

    /** Tìm kiếm câu hỏi theo topic, koreanText hoặc correctAnswer (có phân trang). */
    @Query(value = "SELECT q FROM QuizQuestion q " +
                   "WHERE LOWER(q.topic) LIKE LOWER(CONCAT('%', :q, '%')) " +
                   "OR LOWER(q.koreanText) LIKE LOWER(CONCAT('%', :q, '%')) " +
                   "OR LOWER(q.correctAnswer) LIKE LOWER(CONCAT('%', :q, '%'))",
           countQuery = "SELECT COUNT(q) FROM QuizQuestion q " +
                        "WHERE LOWER(q.topic) LIKE LOWER(CONCAT('%', :q, '%')) " +
                        "OR LOWER(q.koreanText) LIKE LOWER(CONCAT('%', :q, '%')) " +
                        "OR LOWER(q.correctAnswer) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<QuizQuestion> searchPaged(@Param("q") String q, Pageable pageable);
}
