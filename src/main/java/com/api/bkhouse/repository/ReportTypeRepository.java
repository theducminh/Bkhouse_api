package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.ReportType;

import java.util.List;

@Repository
public interface ReportTypeRepository extends JpaRepository<ReportType, Integer> {
    
    // JPA tự động map chuẩn với trường isForum trong Entity
    List<ReportType> findByIsForum(boolean isForum);

    // LƯU Ý: Nhờ ON DELETE CASCADE ở DB, hàm này không còn bắt buộc phải gọi trước khi xóa ReportType,
    // nhưng vẫn giữ lại để tương thích với code cũ ở Service của bác.
    @Modifying
    @Query(value = "DELETE FROM post_report_type WHERE report_type_id = :reportTypeId", nativeQuery = true)
    void deletePostReportTypeByReportTypeId(@Param("reportTypeId") Integer reportTypeId);

    // Viết hoa từ khóa SQL cho chuẩn Convention
    @Query(value = "SELECT COUNT(*) FROM post_report_type WHERE report_type_id = :reportTypeId", nativeQuery = true)
    Integer countByRTId(@Param("reportTypeId") Integer reportTypeId);
}