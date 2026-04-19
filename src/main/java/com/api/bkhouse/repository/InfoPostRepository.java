package com.api.bkhouse.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.InfoPost;
import com.api.bkhouse.entity.response.IInfoPost;

import java.util.List;

@Repository
public interface InfoPostRepository extends JpaRepository<InfoPost, Long> {
    List<InfoPost> findByCreateByOrderByCreateAtDesc(String createBy);

    List<InfoPost> findTop5ByInfoTypeIdOrderByCreateAtDesc(Integer infoTypeId);

    List<InfoPost> findByInfoTypeIdOrderByCreateAtDesc(Integer infoTypeId, Pageable pageable);

    long countByInfoTypeId(Integer infoTypeId);

    @Modifying
    @Query(value = "delete from info_post where info_type_id = :infoTypeId", nativeQuery = true)
    void deleteAllInfoPostByInfoTypeId(@Param("infoTypeId") Integer infoTypeId);

    @Query(value = "select ip.id, ip.title, ip.create_at as createAt, ip.image_url as imageUrl,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from info_post ip inner join user u on ip.create_by = u.id\n" +
            "inner join info_type it on ip.info_type_id = it.id \n" +
            "where it.id = 2 or it.parent = 2 \n" +
            "order by ip.view desc limit 3", nativeQuery = true)
    List<IInfoPost> getTop3OrderByView();

    @Query(value = "select ip.id, ip.title, ip.create_at as createAt, ip.image_url as imageUrl,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from info_post ip inner join user u on ip.create_by = u.id\n" +
            "inner join info_type it on ip.info_type_id = it.id \n" +
            "where it.id = 2 or it.parent = 2 \n" +
            "order by ip.create_at desc limit 5", nativeQuery = true)
    List<IInfoPost> getTop5InfoPostNewest();

    @Query(value = "select ip.id, ip.title, ip.create_at as createAt, ip.image_url as imageUrl,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from info_post ip inner join user u on ip.create_by = u.id\n" +
            "where ip.info_type_id = 1 \n" +
            "order by ip.view desc limit 5", nativeQuery = true)
    List<IInfoPost> bestProjects();
}
