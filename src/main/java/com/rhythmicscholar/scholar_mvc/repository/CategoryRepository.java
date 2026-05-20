package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /** Tìm kiếm category theo tên tiếng Anh hoặc tiếng Hàn. */
    @Query("SELECT c FROM Category c " +
           "WHERE LOWER(c.nameEn) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(c.nameKr) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Category> searchByName(@Param("q") String q);
}
