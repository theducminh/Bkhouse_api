package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.RealEstatePost;
import com.api.bkhouse.entity.response.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RealEstatePostRepository extends JpaRepository<RealEstatePost, UUID> {

    // 1. Dùng toán tử ->> để lấy expired_at từ chuỗi JSON metadata và ép kiểu (::timestamptz) để so sánh thời gian
    @Transactional
    @Modifying
    @Query(value = "update real_estate_posts set is_enabled = false, status = 'REJECTED' \n" +
            "where now() > cast(metadata->>'expired_at' as timestamptz) \n" +
            "and is_enabled = true\n" +
            "and status IN ('PENDING', 'APPROVED', 'REJECTED')", nativeQuery = true)
    void disablePostExpire();
    
    @Transactional
    @Modifying
    @Query(value = "update real_estate_posts set is_enabled = false where id = :id", nativeQuery = true)
    void disablePostById(@Param("id") UUID id);

    
    @Transactional
    @Modifying
    @Query(value = "update real_estate_posts set status = :status where id = :id", nativeQuery = true)
    void updateStatus(@Param("status") String status, @Param("id") UUID id);
    
    @Transactional      
    @Modifying
    @Query(value = "update real_estate_posts rep set view_count = (COALESCE(rep.view_count, 0) + 1) where rep.id = :realEstatePostId", nativeQuery = true)
    void updateView(@Param("realEstatePostId") UUID realEstatePostId);
    
    @Transactional
    @Modifying
    @Query(value = "update real_estate_posts rep set contact_count = (COALESCE(rep.contact_count, 0) + 1) where rep.id = :realEstatePostId", nativeQuery = true)
    void updateClickedView(@Param("realEstatePostId") UUID realEstatePostId);

    
    @Query(value = "select * from real_estate_posts x where x.owner_id = :ownerId order by x.created_at desc limit :rows offset :myJump", nativeQuery = true)
    List<RealEstatePost> findByOwnerId(@Param("ownerId") UUID ownerId, @Param("rows") Integer rows, @Param("myJump") Integer myJump);

    @Query(value = "select count(*) from real_estate_posts x where x.owner_id = :ownerId", nativeQuery = true)
    Integer getNoOfRecords(@Param("ownerId") UUID ownerId);

    // 2. Sửa lại hàm JPA Repository chuẩn xác
    Optional<RealEstatePost> findByIdAndEnable(UUID id, Boolean enable);
    boolean existsByIdAndEnable(UUID id, Boolean enable);

    // 3. Giữ nguyên Alias (as enabled, as isSell...) để không làm vỡ Interface Projection
    @Query(value = "select cast(rep.id as varchar) as id, rep.type, rep.title, rep.district_code as districtCode, rep.is_sell as isSell, rep.price\n" +
            "from real_estate_posts rep\n" +
            "where rep.is_enabled = true\n" +
            "and rep.status = 'APPROVED'\n" +
            "and rep.owner_id = :userId", nativeQuery = true)
    List<IRepEnableRequest> enableRequest(@Param("userId") UUID userId);

    @Query(value = "select cast(rep.id as varchar) as id, rep.type, rep.title, rep.is_sell as isSell, rep.price, repa.status,\n" +
            "concat_ws(' ', u.first_name, u.middle_name, u.last_name) as fullName, \n" +
            "u.phone_number as phoneNumber, repa.id as repaId\n" +
            "from real_estate_posts rep inner join real_estate_post_agency repa \n" +
            "on repa.real_estate_post_id = rep.id inner join users u\n" +
            "on repa.agency_id = u.id\n" +
            "where rep.is_enabled = true\n" +
            "and rep.status = 'APPROVED'\n" +
            "and rep.owner_id = :userId", nativeQuery = true)
    List<IRepRequested> repRequested(@Param("userId") UUID userId);

    @Query(value = "select cast(rep.id as varchar) as id, rep.type, rep.title, rep.is_sell as isSell, rep.price, repa.status,\n" +
            "concat_ws(' ', u.first_name, u.middle_name, u.last_name) as fullName, \n" +
            "u.phone_number as phoneNumber, repa.id as repaId\n" +
            "from real_estate_posts rep inner join real_estate_post_agency repa \n" +
            "on repa.real_estate_post_id = rep.id inner join users u\n" +
            "on repa.agency_id = u.id\n" +
            "where rep.is_enabled = true\n" +
            "and rep.status = 'APPROVED'\n" +
            "and repa.agency_id = :agencyId", nativeQuery = true)
    List<IRepRequested> requestedOfAgency(@Param("agencyId") UUID agencyId);

    @Query(value = "select cast(u.id as varchar) as id, u.phone_number as phoneNumber, u.avatar_url as avatarUrl,\n" +
            "concat_ws(' ', u.first_name, u.middle_name, u.last_name) as fullName\n" +
            "from users u inner join real_estate_post_agency repa\n" +
            "on u.id = repa.agency_id\n" +
            "where repa.status = 'DA_XAC_NHAN'\n" +
            "and repa.real_estate_post_id = cast(:id as uuid)", nativeQuery = true)
    Optional<IEnableUserChat> findContact(@Param("id") UUID id);

    @Query(value = "select cast(u.id as varchar) as id, u.phone_number as phoneNumber, u.avatar_url as avatarUrl,\n" +
            "concat_ws(' ', u.first_name, u.middle_name, u.last_name) as fullName\n" +
            "from users u inner join real_estate_posts rep\n" +
            "on u.id = rep.owner_id\n" +
            "where rep.id = cast(:id as uuid)", nativeQuery = true)
    Optional<IEnableUserChat> findOwnerContact(@Param("id") UUID id);

    @Query(value = "select cast(rep.id as varchar) as id, rep.type, rep.is_sell as sell, rep.status, rep.is_enabled as enable, rep.price, rep.area, rep.created_at as createAt, " +
            "rep.title, rep.address_show as addressShow, " +
            "(select cast(id as varchar) from post_media where post_id = rep.id limit 1) as imageUrl, " +
            "concat_ws(' ', u.first_name, u.middle_name, u.last_name) as fullName, u.phone_number as phoneNumber " +
            "from real_estate_posts rep inner join users u on rep.owner_id = u.id " +
            "order by rep.created_at desc", nativeQuery = true)
    List<IRepAdmin> findAllByAdmin();

    @Query(value = "select cast(rep.id as varchar) as id, rep.type, rep.is_sell as sell, rep.status, rep.is_enabled as enable, rep.price, rep.area, rep.created_at as createAt, " +
            "rep.title, rep.address_show as addressShow, " +
            "(select cast(id as varchar) from post_media where post_id = rep.id limit 1) as imageUrl, " +
            "concat_ws(' ', u.first_name, u.middle_name, u.last_name) as fullName, u.phone_number as phoneNumber " +
            "from real_estate_posts rep inner join users u on rep.owner_id = u.id " +
            "order by rep.created_at desc limit :limit offset :offset", nativeQuery = true)
    List<IRepAdmin> findAllByAdminPageable(@Param("limit") Integer limit, @Param("offset") Integer offset);

    // 4. Các câu query tính Toán Ngày Hết Hạn dựa trên metadata
    @Query(value = "select cast(rep.id as varchar) as id\n" + 
            "from real_estate_posts rep left join interested i on rep.id = i.real_estate_post_id\n" +
            "where rep.is_enabled = true\n" +
            "and rep.status = 'APPROVED'\n" +
            "and (rep.metadata is null or rep.metadata->>'expired_at' is null or now() <= cast(rep.metadata->>'expired_at' as timestamptz)) "+
            "and rep.is_sell = :sell " +
            "and rep.type = :type " +
            "group by rep.id, rep.priority\n" +
            "order by count(i.real_estate_post_id) desc, rep.priority desc " + 
            "limit :limit offset :offset", nativeQuery = true)
    List<UUID> getRepIdDetailPage(@Param("sell") Boolean sell, @Param("type") String type, @Param("limit") Integer limit, @Param("offset") Integer offset);

    @Query(value = "select count(*)\n" +
            "from real_estate_posts rep\n" +
            "where rep.is_enabled = true\n" +
            "and rep.status = 'APPROVED'\n" +
            "and rep.is_sell = :sell " +
            "and rep.type = :type " +
            "and now() <= cast(rep.metadata->>'expired_at' as timestamptz)", nativeQuery = true)
    Integer countTotalBySellAndTypeClient(@Param("sell") Boolean sell, @Param("type") String type);

    @Query(value = "select cast(rep.id as varchar) as id, rep.title, rep.price, rep.area, rep.is_sell as sell, rep.address_show as addressShow, rep.created_at as createAt, " +
            "(select cast(id as varchar) from post_media where post_id = rep.id limit 1) as imageUrl\n" + 
            "from real_estate_posts rep left join interested i on i.real_estate_post_id = rep.id\n" +
            "inner join users u on rep.owner_id = u.id\n" +
            "where rep.is_enabled = true\n" +
            "and rep.status = 'APPROVED'\n" +
            "and now() <= cast(rep.metadata->>'expired_at' as timestamptz)\n" +
            "and u.is_enabled = true\n" +
            "group by rep.id, rep.title, rep.price, rep.area, rep.is_sell, rep.address_show, rep.created_at, rep.priority, rep.view_count\n" + 
            "order by count(i.real_estate_post_id) desc, rep.priority desc limit 10", nativeQuery = true)
    List<IRepClient> getLstMostInterested();

    @Query(value = "select cast(rep.id as varchar) as id, rep.title, rep.price, rep.area, rep.is_sell as sell, rep.address_show as addressShow, rep.created_at as createAt, " +
            "(select cast(id as varchar) from post_media where post_id = rep.id limit 1) as imageUrl\n" +
            "from real_estate_posts rep inner join users u on rep.owner_id = u.id\n" +
            "where rep.is_enabled = true\n" +
            "and rep.status = 'APPROVED'\n" +
            "and now() <= cast(rep.metadata->>'expired_at' as timestamptz)\n" +
            "and u.is_enabled = true\n" +
            "order by rep.view_count desc, rep.priority desc limit 10", nativeQuery = true)
    List<IRepClient> getLstMostView();

    @Query(value = "select cast(rep.id as varchar) as id, rep.title, rep.price, rep.area, rep.is_sell as sell, rep.address_show as addressShow, rep.created_at as createAt, " +
            "(select cast(id as varchar) from post_media where post_id = rep.id limit 1) as imageUrl\n" +
            "from real_estate_posts rep inner join users u on rep.owner_id = u.id\n" +
            "where rep.is_enabled = true\n" +
            "and rep.status = 'APPROVED'\n" +
            "and now() <= cast(rep.metadata->>'expired_at' as timestamptz)\n" +
            "and u.is_enabled = true\n" +
            "order by rep.created_at desc limit 10", nativeQuery = true)
    List<IRepClient> getLstNewest();

    @Query(value = "select cast(rep.id as varchar) as id, rep.is_sell as sell, rep.type, rep.status, rep.is_enabled as enable, rep.price, rep.area, rep.created_at as createAt,\n" +
            "concat_ws(' ', u.first_name, u.middle_name, u.last_name) as fullName,\n" +
            "u.phone_number as phoneNumber\n" +
            "from real_estate_posts rep \n" +
            "inner join real_estate_post_price repp on rep.id = repp.real_estate_post_id\n" +
            "inner join users u on rep.owner_id = u.id\n" +
            "group by rep.id, rep.is_sell, rep.type, rep.status, rep.is_enabled, rep.price, rep.area, rep.created_at, u.first_name, u.middle_name, u.last_name, u.phone_number\n" + // 🚨 Đã sửa group by toàn diện
            "having count(repp.real_estate_post_id) >= 2\n" +
            "order by count(repp.real_estate_post_id) desc", nativeQuery = true)
    List<IRepAdmin> getLstMostChangePrice();

    @Query(value = "select cast(rep.id as varchar) as id, rep.title, rep.is_sell as sell, rep.price, rep.description, rep.is_enabled as enable, rep.type, \n" +
            "rep.status, rep.created_at as createAt, rep.area, rep.updated_at as updateAt,\n" +
            "cast((select count(*) from clicked_info_view where real_estate_post_id = rep.id) as integer) as clickedView,\n" +
            "cast((select count(*) from post_view where real_estate_post_id = rep.id) as integer) as view,\n" +
            "cast((select count(*) from post_comment where post_id = rep.id) as integer) as comment,\n" +
            "cast((select count(*) from post_report where post_id = rep.id) as integer) as report,\n" +
            "cast((select count(*) from interested where real_estate_post_id = rep.id) as integer) as interested\n" +
            "from real_estate_posts rep where rep.owner_id = :userId\n" +
            "order by rep.created_at desc", nativeQuery = true)
    List<IRepClientAdministration> getAllRealEstatePost(@Param("userId") UUID userId);

    @Query(value = "select cast(u.id as varchar) as id, u.phone_number as phoneNumber, u.email, u.avatar_url as avatarUrl, i.create_at as createAt, \n" +
            "concat_ws(' ', u.first_name, u.middle_name, u.last_name) as fullName\n" +
            "from interested i inner join users u on u.id = i.user_id\n" +
            "where cast(i.user_id as varchar) != '00000000-0000-0000-0000-000000000000'\n" +
            "and i.real_estate_post_id = cast(:postId as uuid)\n" +
            "order by i.create_at desc", nativeQuery = true)
    List<IInterestedUser> getListInterestedUsers(@Param("postId") UUID postId);
}