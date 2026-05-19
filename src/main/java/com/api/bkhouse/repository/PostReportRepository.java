package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.PostReport;
import com.api.bkhouse.entity.response.IReportData;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    
    long countByPostId(UUID postId);

    // ĐÃ SỬA: Nối (JOIN) trực tiếp bằng UUID, không cần ép kiểu String nữa cho nhẹ Database
    @Query(value = "select cast(rep.id as varchar) as postId, rep.title as title, 'REAL_ESTATE_POST' as postType, \n" +
            "    concatWS(' ', u.first_name, u.middle_name, u.last_name) as fullName,\n" +
            "    u.phone_number as phoneNumber, rep.created_at as createAt, count(pr.id) as count\n" +
            "from real_estate_posts rep \n" +
            "inner join post_report pr on rep.id = pr.post_id \n" + // <-- Nối mượt mà UUID = UUID
            "inner join users u on rep.owner_id = u.id \n" +
            "where rep.is_enabled = true \n" +
            "group by rep.id, rep.title, u.first_name, u.middle_name, u.last_name, u.phone_number, rep.created_at \n" +
            "union \n" +
            "select cast(fp.id as varchar) as postId, fp.content as title, 'FORUM_POST' as postType, \n" +
            "    concatWS(' ', u.first_name, u.middle_name, u.last_name) as fullName,\n" +
            "    u.phone_number as phoneNumber, fp.create_at as createAt, count(pr.id) as count\n" +
            "from forum_post fp \n" +
            "inner join post_report pr on fp.id = pr.post_id \n" + // <-- Nối mượt mà UUID = UUID
            "inner join users u on CAST(fp.create_by as UUID) = u.id \n" +
            "where fp.is_enabled = true \n" +
            "group by fp.id, fp.content, u.first_name, u.middle_name, u.last_name, u.phone_number, fp.create_at \n" +
            "order by count desc", nativeQuery = true)
    List<IReportData> getListReportData();

    List<PostReport> findByPostIdOrderByCreateAtDesc(UUID postId);
}