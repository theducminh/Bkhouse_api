package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.api.bkhouse.entity.SpecialAccount;
import com.api.bkhouse.entity.response.IAgencyRep;
import com.api.bkhouse.entity.response.IDistrict;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpecialAccountRepository extends JpaRepository<SpecialAccount, UUID> {
    Optional<SpecialAccount> findByUserId(UUID userId);
   
    @Transactional
    @Modifying
    void deleteByUserId(UUID userId);

    // Đã chuyển sang INNER JOIN chuẩn chỉ
    @Query(value = "SELECT d.* FROM districts d " +
                   "INNER JOIN agency_district ad ON d.code = ad.district_code " +
                   "WHERE ad.user_id = :userId", 
           nativeQuery = true)
    List<IDistrict> findAllDistrictsAgency(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query(value = "delete from agency_district where user_id=:userId", nativeQuery = true)
    void agencyDistrictDeleteByUserId(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query(value = "delete from user_role where user_id=:userId and id=4", nativeQuery = true)
    void userRoleDeleteByUserId(@Param("userId") UUID userId);

    @Query(value = "select district_code from agency_district where user_id = :userId", nativeQuery = true)
    List<String> getAllDistrictCodeOfAgency(@Param("userId") UUID userId);


    //Lấy danh sách môi giới theo quận của bài đăng bất động sản
    @Query(value = "SELECT DISTINCT CONCAT_WS(' ', u.first_name, u.middle_name, u.last_name) AS fullName, " +
                   "u.phone_number AS phoneNumber, CAST(u.id AS varchar) AS id " +
                   "FROM agency_district ad " +
                   "INNER JOIN special_account sa ON ad.user_id = sa.user_id " +
                   "INNER JOIN users u ON sa.user_id = u.id " +
                   "INNER JOIN real_estate_posts rep ON rep.district_code = ad.district_code " +
                   "WHERE ad.is_enabled = true " + // Giả sử cột enable của agency_district đã đổi thành is_enabled
                   "AND sa.is_agency = true " +
                   "AND rep.id = :repId " +
                   "AND u.id NOT IN (SELECT CAST(agency_id AS uuid) FROM real_estate_post_agency WHERE real_estate_post_id = :repId)", 
           nativeQuery = true)
    List<IAgencyRep> listAgencyByRepDistrict(@Param("repId") UUID repId);
}
