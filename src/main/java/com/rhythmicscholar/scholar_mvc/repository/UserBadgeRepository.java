package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    /** Lấy tất cả badge của một user, kèm thông tin badge. */
    @Query("SELECT ub FROM UserBadge ub JOIN FETCH ub.badge WHERE ub.user.id = :userId ORDER BY ub.badge.displayOrder ASC")
    List<UserBadge> findByUserIdWithBadge(@Param("userId") Long userId);

    /** Kiểm tra user đã có badge này chưa. */
    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);

    /** Đếm số badge của user. */
    long countByUserId(Long userId);
}
