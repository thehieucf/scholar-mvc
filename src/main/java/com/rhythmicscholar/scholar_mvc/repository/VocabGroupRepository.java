package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.VocabGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho VocabGroup – nhóm từ vựng cá nhân của user.
 */
@Repository
public interface VocabGroupRepository extends JpaRepository<VocabGroup, Long> {

    /**
     * Lấy tất cả nhóm từ vựng của một user theo userId, sắp xếp mới nhất trước.
     */
    List<VocabGroup> findByUserIdOrderByCreatedAtDesc(Long userId);
}
