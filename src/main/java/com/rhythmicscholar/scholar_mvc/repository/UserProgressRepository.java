package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository quản lý các truy vấn liên quan đến thực thể UserProgress.
 * Cung cấp các phương pháp để tìm kiếm thông tin tiến độ tổng thể của người dùng.
 */
@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    
    /**
     * Tìm danh sách tất cả các bản ghi tiến độ của một người dùng cụ thể.
     * @param userId ID của người dùng cần tìm kiếm.
     * @return Danh sách các bản ghi UserProgress.
     */
    @Query("SELECT up FROM UserProgress up JOIN FETCH up.category WHERE up.user.id = :userId")
    List<UserProgress> findByUserId(@Param("userId") Long userId);
}
