package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    /** Lấy tất cả badge theo thứ tự hiển thị. */
    List<Badge> findAllByOrderByDisplayOrderAsc();

    /** Lấy badge theo loại điều kiện. */
    List<Badge> findByConditionType(String conditionType);
}
