package com.example.ref.repository;

import com.example.ref.entity.UserCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long>, UserCategoryCustomRepository {

    UserCategory findByClassification(String type);

    List<UserCategory> findAllByParent(UserCategory parentCategory);

    boolean existsByTitle(String title);

    List<UserCategory> findAllByParentIsNullAndClassificationIsNot(String classification);


    // IDX 여러개로 조회

    @Query("select uc from UserCategory uc where uc.id in :idx")
    List<UserCategory> findAllByIdxIn(@Param("idx") List<Long> idx);


}
