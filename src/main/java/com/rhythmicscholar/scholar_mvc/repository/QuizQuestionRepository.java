package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByTopic(String topic);
    List<QuizQuestion> findByVocabularyIdIn(List<Long> vocabularyIds);
    List<QuizQuestion> findTop10ByOrderById();
}
