package com.example.ref.service.admin;

import com.example.ref.dto.UserCategoryDto;
import com.example.ref.entity.UserCategory;
import com.example.ref.repository.UserCategoryRepository;
import com.example.ref.request.admin.category.user.AddUserCategoryRequestDto;
import com.example.ref.request.admin.category.user.GetUserCategoryListRequestDto;
import com.example.ref.request.admin.category.user.ModifyUserCategoryRequestDto;
import com.example.ref.request.admin.category.user.TitleCheckRequestDto;
import com.example.ref.rules.UserCategoryDepth;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCategoryService {

    private final UserCategoryRepository userCategoryRepository;



    public boolean checkUserCategoryTitle(TitleCheckRequestDto titleCheckRequestDto) {
        return userCategoryRepository.existsByTitle(titleCheckRequestDto.getTitle());
    }

    public List<UserCategoryDto> getFirstUserCategoryList() {

        // classification 이 ROLE_GUEST 가 아닌 parentId 가 null 인 1차 카테고리 모두 조회
        List<UserCategory> userCategoryList = userCategoryRepository.findAllByParentIsNullAndClassificationIsNot("ROLE_GUEST");
        return userCategoryList.stream().map(UserCategoryDto::convertToDto).toList();
    }

    public List<UserCategoryDto> getUserCategorySubList(Long id) {

        UserCategory parentUserCategory = userCategoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));
        // 1차 카테고리로 2차 카테고리 조회
        List<UserCategory> userCategoryList = userCategoryRepository.findAllByParent(parentUserCategory);
        return userCategoryList.stream().map(UserCategoryDto::convertToDto).toList();

    }


    public void addUserCategory(AddUserCategoryRequestDto addUserCategoryRequestDto) {


        // 타이틀 중복 체크
        if(userCategoryRepository.existsByTitle(addUserCategoryRequestDto.getTitle())) {
            throw new IllegalArgumentException("중복된 카테고리명이 존재합니다.");
        }

        UserCategory userCategoryParentIdx = userCategoryRepository.findById(
            addUserCategoryRequestDto.getUserCategoryParentIdx()).orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));

        // parent 의 depth 가 1 이하면 추가 불가능
        if(userCategoryParentIdx.getDepth() < UserCategoryDepth.MIDDLE_DEPTH.getDepth()) {
            throw new IllegalArgumentException("1차, 2차 카테고리에는 추가할 수 없습니다.");
        }

        // parentId
        int depth = userCategoryParentIdx.getDepth() + 1;
        long parentId = userCategoryParentIdx.getId();
        UserCategory parent = userCategoryRepository.findById(parentId).orElseThrow( () -> new IllegalArgumentException("상위 카테고리가 없습니다."));

        // 부모의 classification 을 그대로 따라감
        String classification = userCategoryParentIdx.getClassification();
        UserCategory userCategory = UserCategory.builder()
            .depth(depth)
            .parent(parent)
            .title(addUserCategoryRequestDto.getTitle())
            .memo(addUserCategoryRequestDto.getMemo())
            .classification(classification)
            .build();

        userCategoryRepository.save(userCategory);

    }

    public UserCategoryDto getUserCategory(Long id) {

        UserCategory userCategory = userCategoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));
        return UserCategoryDto.convertToDto(userCategory);

    }


    public Page<UserCategoryDto> getUserCategoryList(Pageable pageable, GetUserCategoryListRequestDto getUserCategoryListRequestDto) {

        Page<UserCategory> userCategoryList = userCategoryRepository.findAllByClassificationAndTitleContainingAndCreatedAtBetween(
            getUserCategoryListRequestDto.getKeyword(),
            getUserCategoryListRequestDto.getStartDate() == null ? null : getUserCategoryListRequestDto.getStartDate().atStartOfDay(),
            getUserCategoryListRequestDto.getEndDate() == null ? null : getUserCategoryListRequestDto.getEndDate().atTime(23, 59, 59),
            pageable
        );

        return userCategoryList.map(UserCategoryDto::convertToDto);

    }


    public void modifyUserCategory(ModifyUserCategoryRequestDto modifyUserCategoryRequestDto) {


        UserCategory userCategory = userCategoryRepository.findById(modifyUserCategoryRequestDto.getUserCategoryIdx()).orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));

        // depth 2 이상만 수정 가능
        if(userCategory.getDepth() <= UserCategoryDepth.MIDDLE_DEPTH.getDepth()) {
            throw new IllegalArgumentException("1차, 2차 카테고리는 수정할 수 없습니다.");
        }

        // 타이틀 중복 가능성 우려, 기존 카테고리와 받은 카테 고리가 다르면 중복 체크
        if(!userCategory.getTitle().equals(modifyUserCategoryRequestDto.getTitle())) {
            if(userCategoryRepository.existsByTitle(modifyUserCategoryRequestDto.getTitle())) {
                throw new IllegalArgumentException("중복된 카테고리명이 존재합니다.");
            } else { // 중복이 없고 기존 카테고리 입력과 다르다면 타이틀 변경
                userCategory.setTitle(modifyUserCategoryRequestDto.getTitle());
            }
        }

        UserCategory parentUserCategory = userCategoryRepository.findById(
            modifyUserCategoryRequestDto.getUserCategoryParentIdx()).orElseGet(() -> null);

        userCategory.setParent(parentUserCategory);
        userCategory.modifyUserCategory(modifyUserCategoryRequestDto);

    }

    public void deleteUserCategory(Long id) {

        UserCategory userCategory = userCategoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));

        // depth 2 이상만 삭제 가능
        if(userCategory.getDepth() <= UserCategoryDepth.MIDDLE_DEPTH.getDepth()) {
            throw new IllegalArgumentException("1차, 2차 카테고리는 삭제할 수 없습니다.");
        }

        // 하위 카테고리가 존재하는지 확인
        List<UserCategory> childUserCategoryList = userCategoryRepository.findAllByParent(userCategory);
        if(!childUserCategoryList.isEmpty()) {
            throw new IllegalArgumentException("삭제 하려는 하위 카테고리가 존재합니다.");
        }

        // 등록된 어드민 있으면 삭제 불가능하도록 처리
        if(!userCategory.getAdminList().isEmpty()) {
            throw new IllegalArgumentException("어드민이 등록된 카테고리입니다.");
        }

        // 등록된 유저 있으면 삭제 불가능하도록 처리
        if(!userCategory.getUserList().isEmpty()) {
            throw new IllegalArgumentException("유저가 등록된 카테고리입니다.");
        }

        userCategoryRepository.deleteById(id);

    }


}
