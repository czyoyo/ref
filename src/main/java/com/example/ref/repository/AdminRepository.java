package com.example.ref.repository;

import com.example.ref.entity.Admin;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminRepository extends JpaRepository<Admin, Long>, AdminCustomRepository {

    List<Admin> findAllByAdminId(String adminId);
    List<Admin> findAllByNickname(String nickname);


    @Query("select u from Admin u where u.leaveDate is not null and u.password is not null and function('DATEDIFF', current_date, u.leaveDate) > 90 order by u.id desc")
    List<Admin> findAllByLeaveDateIsNotNull();

    @Query("select t from Admin t where t.leaveDate is null order by t.id desc")
    List<Admin> findAllByLeaveDateIsNull();

    @Query("select t from Admin t where t.id in :ids order by t.id desc")
    List<Admin> findAllByIds(@Param("ids") List<Long> ids);

    Optional<Admin> findOptionalByNickname(String nickname);
    Admin findByAdminId(String adminId);
    Optional<Admin> findOptionalByAdminId(String adminId);

    @Query("select ut from Admin ut left join fetch ut.adminImageList left join fetch ut.adminProfileImage where ut.id = :id")
    Admin findByIdAndAdminImages(@Param("id") Long id);

    @Query("select ut from Admin ut left join fetch ut.adminImageList left join fetch ut.adminProfileImage where ut.userCategory.title = :role and ut.createdAt < :before48Hours order by ut.id desc")
    List<Admin> findAllByRoleAndCreatedAtBefore(@Param("role") String role, @Param("before48Hours") LocalDateTime before48Hours);


}
