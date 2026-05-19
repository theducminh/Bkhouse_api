package com.api.bkhouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.bkhouse.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findByUsernameNot(String username);
    @Modifying
    @Query(value = "insert into user_role(user_id, role_id) values ( :userId , 2)", nativeQuery = true)
    void agencyRegister(@Param("userId") UUID userId);

    @Modifying
    @Query(value = "insert into agency_district(user_id, district_code) values ( :userId, :districtCode );", nativeQuery = true)
    void agencyDistrictInsert(@Param("userId") UUID userId, @Param("districtCode") String districtCode);

    @Query(value = "select r.name\n" +
            "from role r inner join user_role ur on ur.role_id = r.id\n" +
            "where ur.user_id = :userId", nativeQuery = true)
    List<String> findRolesByUserId(UUID userId);

    @Query(value = "SELECT COUNT(*) > 0 FROM user_role ur " +
                   "INNER JOIN roles r ON ur.role_id = r.id " +
                   "WHERE ur.user_id = :userId AND r.name = 'ROLE_ADMIN'", 
           nativeQuery = true)
    boolean isUserAdmin(@Param("userId") UUID userId);
}
