package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import com.api.bkhouse.entity.RealEstatePost;
import com.api.bkhouse.entity.response.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface RealEstatePostRepository extends JpaRepository<RealEstatePost, UUID> {
    @Modifying
    @Query(value = "update real_estate_post set enable=0, status = 'DA_HET_HAN' \n" +
            "where datediff(now(), create_at) > period \n" +
            "and enable = 1\n" +
            "and (status = 'CHO_KIEM_DUYET' or status = 'DA_KIEM_DUYET' or status = 'BI_TU_CHOI')", nativeQuery = true)
    void disablePostExpire();

    @Modifying
    @Query(value = "update real_estate_post set enable=0 where id = :id", nativeQuery = true)
    void disablePostById(UUID id);

    @Query(value = "select * from real_estate_post x where x.owner_id = :ownerId order by x.create_at desc limit :rows offset :myJump", nativeQuery = true)
    List<RealEstatePost> findByOwnerId(UUID ownerId, Integer rows, Integer myJump);

    @Query(value = "select count(*) from real_estate_post x where x.owner_id = :ownerId", nativeQuery = true)
    Integer getNoOfRecords(UUID ownerId);

    @Modifying
    @Query(value = "update real_estate_post set status = :status where id = :id ;", nativeQuery = true)
    void updateStatus(@PathVariable("status") String status, @PathVariable("id") UUID id);

    @Modifying
    @Query(value = "update real_estate_post rep set view = (rep.view + 1) where rep.id = :realEstatePostId", nativeQuery = true)
    void updateView(@Param("realEstatePostId") UUID realEstatePostId);

    @Modifying
    @Query(value = "update real_estate_post rep set clicked_view = (rep.clicked_view + 1) where rep.id = :realEstatePostId", nativeQuery = true)
    void updateClickedView(@Param("realEstatePostId") UUID realEstatePostId);

    Optional<RealEstatePost> findByIdAndEnable(UUID id, boolean enable);
    boolean existsByIdAndEnable(UUID id, boolean enable);

    @Query(value = "select rep.id, rep.type, rep.title, rep.district_code as districtCode, rep.is_sell as isSell, rep.price\n" +
            "from real_estate_post rep\n" +
            "where rep.enable = 1\n" +
            "and rep.status = 'DA_KIEM_DUYET'\n" +
            "and rep.owner_id = :userId ;", nativeQuery = true)
    List<IRepEnableRequest> enableRequest(@Param("userId") UUID userId);

    @Query(value = "select rep.id, rep.type, rep.title, rep.is_sell as isSell, rep.price, repa.status,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName, \n" +
            "u.phone_number as phoneNumber, repa.id as repaId\n" +
            "from real_estate_post rep inner join real_estate_post_agency repa \n" +
            "on repa.real_estate_post_id = rep.id inner join user u\n" +
            "on repa.agency_id = u.id\n" +
            "and rep.enable = 1\n" +
            "and rep.status = 'DA_KIEM_DUYET'\n" +
            "and rep.owner_id = :userId", nativeQuery = true)
    List<IRepRequested> repRequested(@Param("userId") UUID userId);

    @Query(value = "select rep.id, rep.type, rep.title, rep.is_sell as isSell, rep.price, repa.status,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName, \n" +
            "u.phone_number as phoneNumber, repa.id as repaId\n" +
            "from real_estate_post rep inner join real_estate_post_agency repa \n" +
            "on repa.real_estate_post_id = rep.id inner join user u\n" +
            "on rep.owner_id = u.id\n" +
            "and rep.enable = 1\n" +
            "and rep.status = 'DA_KIEM_DUYET'\n" +
            "and repa.agency_id = :agencyId", nativeQuery = true)
    List<IRepRequested> requestedOfAgency(@Param("agencyId") UUID agencyId);

    @Query(value = "select u.id, u.phone_number as phoneNumber, u.avatar_url as avatarUrl,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from user u inner join real_estate_post_agency repa\n" +
            "on u.id = repa.agency_id\n" +
            "where repa.status = 'DA_XAC_NHAN'\n" +
            "and repa.real_estate_post_id = :id", nativeQuery = true)
    Optional<IEnableUserChat> findContact(UUID id);

    @Query(value = "select u.id, u.phone_number as phoneNumber, u.avatar_url as avatarUrl,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from user u inner join real_estate_post rep\n" +
            "on u.id = rep.owner_id\n" +
            "where rep.id = :id", nativeQuery = true)
    Optional<IEnableUserChat> findOwnerContact(UUID id);

    @Query(value = "select rep.id, rep.type, rep.is_sell as sell, rep.status, rep.enable, rep.price, rep.area, rep.create_at as createAt, " +
            "rep.title, rep.address_show as addressShow, " +
            "(select id from post_media where post_id = rep.id limit 1) as imageUrl, " +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName, u.phone_number as phoneNumber " +
            "from real_estate_post rep inner join user u on rep.owner_id = u.id " +
            "order by rep.create_at desc", nativeQuery = true)
    List<IRepAdmin> findAllByAdmin();

    @Query(value = "select rep.id, rep.type, rep.is_sell as sell, rep.status, rep.enable, rep.price, rep.area, rep.create_at as createAt, " +
            "rep.title, rep.address_show as addressShow, " +
            "(select id from post_media where post_id = rep.id limit 1) as imageUrl, " +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName, u.phone_number as phoneNumber " +
            "from real_estate_post rep inner join user u on rep.owner_id = u.id " +
            "order by rep.create_at desc limit :limit offset :offset", nativeQuery = true)
    List<IRepAdmin> findAllByAdminPageable(Integer limit, Integer offset);

    @Query(value = "select rep.id\n" +
            "from real_estate_post rep left join interested i on rep.id = i.real_estate_post_id\n" +
            "where rep.enable = 1\n" +
            "and datediff(now(), rep.create_at) <= rep.period\n" +
            "and rep.is_sell = :sell " +
            "and rep.type = :type " +
            "group by rep.id\n" +
            "order by rep.priority, count(i.real_estate_post_id) desc " +
            "limit :limit offset :offset" ,nativeQuery = true)
    List<UUID> getRepIdDetailPage(Byte sell, String type, Integer limit, Integer offset);

    @Query(value = "select count(*)\n" +
            "from real_estate_post rep\n" +
            "where rep.enable = 1\n" +
            "and rep.status = 'DA_KIEM_DUYET'\n" +
            "and rep.is_sell = :sell " +
            "and rep.type = :type " +
            "and datediff(now(), rep.create_at) <= rep.period", nativeQuery = true)
    Integer countTotalBySellAndTypeClient(Byte sell, String type);

    @Query(value = "select rep.id, rep.title, rep.price, rep.area, rep.is_sell as sell, rep.address_show as addressShow, rep.create_at as createAt, " +
            "(select id from post_media where post_id = rep.id limit 1) as imageUrl\n" +
            "from real_estate_post rep left join interested i on i.real_estate_post_id = rep.id\n" +
            "inner join user u on rep.owner_id = u.id\n" +
            "where rep.enable = 1\n" +
            "and rep.status = 'DA_KIEM_DUYET'\n" +
            "and datediff(now(), rep.create_at) <= period\n" +
            "and u.enable = 1\n" +
            "group by rep.id\n" +
            "order by count(i.real_estate_post_id) and rep.priority desc limit 10;", nativeQuery = true)
    List<IRepClient> getLstMostInterested();

    @Query(value = "select rep.id, rep.title, rep.price, rep.area, rep.is_sell as sell, rep.address_show as addressShow, rep.create_at as createAt, " +
            "(select id from post_media where post_id = rep.id limit 1) as imageUrl\n" +
            "from real_estate_post rep inner join user u on rep.owner_id = u.id\n" +
            "where rep.enable = 1\n" +
            "and rep.status = 'DA_KIEM_DUYET'\n" +
            "and datediff(now(), rep.create_at) <= period\n" +
            "and u.enable = 1\n" +
            "order by rep.view and rep.priority desc limit 10;", nativeQuery = true)
    List<IRepClient> getLstMostView();

    @Query(value = "select rep.id, rep.title, rep.price, rep.area, rep.is_sell as sell, rep.address_show as addressShow, rep.create_at as createAt, " +
            "(select id from post_media where post_id = rep.id limit 1) as imageUrl\n" +
            "from real_estate_post rep inner join user u on rep.owner_id = u.id\n" +
            "where rep.enable = 1\n" +
            "and rep.status = 'DA_KIEM_DUYET'\n" +
            "and datediff(now(), rep.create_at) <= period\n" +
            "and u.enable = 1\n" +
            "order by rep.create_at desc limit 10;", nativeQuery = true)
    List<IRepClient> getLstNewest();

    @Query(value = "select rep.id, rep.is_sell as sell, rep.type, rep.status, rep.enable, rep.price, rep.area, rep.create_at as createAt,\n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName,\n" +
            "u.phone_number as phoneNumber\n" +
            "from real_estate_post rep \n" +
            "inner join real_estate_post_price repp on rep.id = repp.real_estate_post_id\n" +
            "inner join user u on rep.owner_id = u.id\n" +
            "group by rep.id, rep.id, rep.type, rep.status, rep.enable, rep.price, rep.area, createAt\n" +
            "having count(repp.real_estate_post_id) >= 2\n" +
            "order by count(repp.real_estate_post_id) desc;", nativeQuery = true)
    List<IRepAdmin> getLstMostChangePrice();

    @Query(value = "select rep.id, rep.title, rep.is_sell as sell, rep.price, rep.description, rep.enable, rep.type, \n" +
            "rep.status, rep.create_at as createAt, rep.area, rep.update_at as updateAt,\n" +
            "(select count(*) from clicked_info_view where real_estate_post_id = rep.id) as clickedView,\n" +
            "(select count(*) from post_view where real_estate_post_id = rep.id) as view,\n" +
            "(select count(*) from post_comment where post_id = rep.id) as comment,\n" +
            "(select count(*) from post_report where post_id = rep.id) as report,\n" +
            "(select count(*) from interested where real_estate_post_id = rep.id) as interested\n" +
            "from real_estate_post rep where rep.owner_id = :userId\n" +
            "order by createAt desc;", nativeQuery = true)
    List<IRepClientAdministration> getAllRealEstatePost(UUID userId);

    @Query(value = "select u.id, u.phone_number as phoneNumber, u.email, u.avatar_url as avatarUrl, i.create_at as createAt, \n" +
            "concat(u.first_name, ' ', u.middle_name, ' ', u.last_name) as fullName\n" +
            "from interested i inner join user u on u.id = i.user_id\n" +
            "where i.user_id != 'anonymous'\n" +
            "and i.real_estate_post_id = :postId\n" +
            "order by createAt desc", nativeQuery = true)
    List<IInterestedUser> getListInterestedUsers(UUID postId);
}
