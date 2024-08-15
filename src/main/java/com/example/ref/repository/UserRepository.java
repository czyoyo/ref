package com.example.ref.repository;

import com.example.ref.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {

    List<User> findAllByUserId(String userId);
    List<User> findAllByNickname(String nickname);

    // 자기 권한으로  유저 모두 조회
    @Query("select u from User u join fetch u.userCategory where u.userCategory.title = :role order by u.id desc")
    List<User> findAllByRole(@Param("role") String role);

    @Query("select u from User u join fetch u.userCategory where u.userCategory.title = :role and u.createdAt < :before48Hours order by u.id desc")
    List<User> findAllByRoleAndCreatedAtBefore(@Param("role") String role, @Param("before48Hours") LocalDateTime before48Hours);

    // 위 조건에서 leaveDate 가 null 이 아닌 유저만 조회
    @Query("select u from User u where u.leaveDate is null order by u.id desc")
    List<User> findAllByLeaveDateIsNull();

    // User 의 idx 를 리스트로 가져와서 조회한다.
    @Query("select u from User u where u.id in :ids order by u.id desc")
    List<User> findAllByIds(@Param("ids") List<Long> ids);

    // leveDate가 null이 아닌 유저 + 3개월 이상 지난 유저 현재보다 미래는 - 값이 나오고 과거는 + 값이 나온다.
    // 90일이 지난 유저만 조회
    @Query("select u from User u where u.leaveDate is not null and u.password is not null and function('DATEDIFF', current_date, u.leaveDate) > 90 order by u.id desc")
    List<User> findAllByLeaveDateIsNotNull();

    Optional<User> findOptionalByNickname(String nickname);
    User findByUserId(String userId);
    Optional<User> findOptionalByUserId(String userId);


}
