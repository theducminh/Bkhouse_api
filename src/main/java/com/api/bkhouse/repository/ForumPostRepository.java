package com.api.bkhouse.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.ForumPost;
import com.api.bkhouse.entity.response.IUserForumPost;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, UUID> {
    boolean existsByIdAndEnable(UUID id, boolean enable);
    List<ForumPost> findByCreateByNot(UUID createBy);
    Page<ForumPost> findByCreateByAndEnable(UUID createBy, boolean enable, Pageable pageable);
    Optional<ForumPost> findByIdAndEnable(UUID id, boolean enable);
    @Query(value = "select tmp1.noLikes, tmp2.noComments, tmp3.noReports, \n" +
            "\tfp.id, fp.content, \n" +
            "    concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName,\n" +
            "    u.phone_number as phoneNumber\n" +
            "from\n" +
            "(\n" +
            "\tselect count(fpl.forum_post_id) as noLikes, fp.id\n" +
            "\tfrom forum_post fp left join forum_post_like fpl on fpl.forum_post_id = fp.id\n" +
            "\tgroup by fp.id\n" +
            ") as tmp1,\n" +
            "(\n" +
            "\tselect count(pc.post_id) as noComments, fp.id\n" +
            "\tfrom forum_post fp left join post_comment pc on pc.post_id = fp.id\n" +
            "\tgroup by fp.id\n" +
            ") as tmp2,\n" +
            "(\n" +
            "\tselect count(pr.post_id) as noReports, fp.id\n" +
            "\tfrom forum_post fp left join post_report pr on pr.post_id = fp.id\n" +
            "\tgroup by fp.id\n" +
            ") as tmp3,\n" +
            "forum_post fp, user u\n" +
            "where fp.id = tmp1.id\n" +
            "and fp.id = tmp2.id\n" +
            "and fp.id = tmp3.id\n" +
            "and fp.create_by != 'admin' \n" +
            "and fp.create_by = u.id \n" +
            "and fp.enable = 1\n" +
            "order by fp.create_at;",nativeQuery = true)
    List<IUserForumPost> findAllByUser();

    @Modifying
    @Query(value = "update forum_post set enable = 0 where id = :postId", nativeQuery = true)
    void deletePostById(UUID postId);

    Page<ForumPost> findByEnable(boolean enable, Pageable pageable);
}
