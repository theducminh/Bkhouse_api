package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api.bkhouse.entity.PostReport;
import com.api.bkhouse.entity.response.IReportData;

import java.util.List;
import java.util.UUID;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    long countByPostId(UUID postId);

    @Query(value = "select rep.id as postId, rep.title, 'REAL_ESTATE_POST' as postType, \n" +
            "    concat(u.first_name,' ',u.middle_name,' ',u.last_name) as fullName,\n" +
            "    u.phone_number as phoneNumber, rep.create_at as createAt, count(pr.post_id) as count\n" +
            "from real_estate_post rep, post_report pr, user u\n" +
            "where rep.id = pr.post_id\n" +
            "and rep.enable = 1\n" +
            "and rep.owner_id = u.id \n" +
            "group by postId, postType, fullName, phoneNumber \n" +
            "union\n" +
            "select fp.id as postId, fp.content, 'FORUM_POST' as postType, \n" +
            "    concat(u.first_name,' ',u.middle_name,' ',u.last_name) as fullName,\n" +
            "    u.phone_number as phoneNumber, fp.create_at as createAt, count(pr.post_id) as count\n" +
            "from forum_post fp, post_report pr, user u\n" +
            "where fp.id = pr.post_id\n" +
            "and fp.create_by = u.id \n" +
            "and fp.enable = 1 \n" +
            "group by postId, postType, fullName, phoneNumber \n" +
            "order by count desc;", nativeQuery = true)
    List<IReportData> getListReportData();

    List<PostReport> findByPostIdOrderByCreateAtDesc(UUID postId);
}
