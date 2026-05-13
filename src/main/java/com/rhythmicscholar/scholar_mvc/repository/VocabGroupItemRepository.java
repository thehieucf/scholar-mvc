package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.VocabGroupItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho VocabGroupItem – từ vựng bên trong một nhóm cá nhân.
 */
@Repository
public interface VocabGroupItemRepository extends JpaRepository<VocabGroupItem, Long> {

    /**
     * Lấy tất cả từ trong một nhóm, sắp xếp mới nhất trước.
     */
    List<VocabGroupItem> findByGroupIdOrderByAddedAtDesc(Long groupId);

    /**
     * Đếm số từ trong một nhóm.
     */
    long countByGroupId(Long groupId);

    /**
     * Xóa tất cả từ thuộc một nhóm (khi xóa nhóm).
     */
    void deleteByGroupId(Long groupId);
}
