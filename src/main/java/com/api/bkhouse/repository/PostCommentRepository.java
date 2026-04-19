package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.PostComment;
import com.api.bkhouse.entity.response.ICommentCompare;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    long countByPostId(UUID postId);
    List<PostComment> findByPostId(UUID postId);
    Optional<PostComment> findById(UUID id);
    @Query(value = "select count(*) > 0 from post_comment where id = :id", nativeQuery = true)
    boolean existsById(UUID id);
    @Query(value = "delete from post_comment where id = :id", nativeQuery = true)
    void deleteById(UUID id);
    @Query(value = "select rep.owner_id as postOwner, pc.create_by as commentOwner \n" +
            "from real_estate_post rep  inner join post_comment pc on rep.owner_id = pc.create_by\n" +
            "and pc.id = :commentId and rep.enable = 1 \n" +
            "union\n" +
            "select fp.create_by as postOwner, pc.create_by as commentOwner \n" +
            "from forum_post fp  inner join post_comment pc on fp.create_by = pc.create_by\n" +
            "and pc.id = :commentId and fp.enable = 1", nativeQuery = true)
    Optional<ICommentCompare> compareOwner(@Param("commentId") UUID id);
}
