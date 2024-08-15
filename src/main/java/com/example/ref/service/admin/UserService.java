package com.example.ref.service.admin;

import com.example.ref.config.redis.RedisPublisher;
import com.example.ref.dto.AdminDto;
import com.example.ref.dto.FileDto;
import com.example.ref.dto.RefreshSocketDto;
import com.example.ref.dto.UserCategoryDto;
import com.example.ref.dto.UserDto;
import com.example.ref.entity.Admin;
import com.example.ref.entity.User;
import com.example.ref.entity.UserCategory;
import com.example.ref.entity.UserProfileImage;
import com.example.ref.repository.AdminRepository;
import com.example.ref.repository.UserCategoryRepository;
import com.example.ref.repository.UserProfileImageRepository;
import com.example.ref.repository.UserRepository;
import com.example.ref.request.admin.user.AddUserRequestDto;
import com.example.ref.request.admin.user.GetUserRequestDto;
import com.example.ref.request.admin.user.ModifyUserRequestDto;
import com.example.ref.rules.FileType;
import com.example.ref.rules.RefreshSocketType;
import com.example.ref.rules.SessionType;
import com.example.ref.rules.UserCategoryDepth;
import com.example.ref.util.RedisUtils;
import com.example.ref.util.S3FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisPublisher redisPublisher;
    private final ChannelTopic levelUpChannelTopic;
    private final ObjectMapper objectMapper;
    private final RedisUtils redisUtils;
    private final S3FileUtils s3FileUtils;
    private final UserProfileImageRepository userProfileImageRepository;
    @Value("${path.gov-ncp-location}")
    private String govNcpFilePath;
    private final EntityManager entityManager;


    public void deleteProfileImage(Long id) {
            User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 계정입니다."));

            UserProfileImage userProfileImage = userProfileImageRepository.findByUser(user);

            if(userProfileImage != null) {
                s3FileUtils.deleteFile(userProfileImage.getFilePath());
                userProfileImageRepository.delete(userProfileImage);
            }
    }


    public boolean checkUserId(String userId) {

        List<User> userList = userRepository.findAllByUserId(userId);
        return !userList.isEmpty();
    }

    public boolean checkUserNickname(String nickname) {

        List<User> userLIst = userRepository.findAllByNickname(nickname);
        return !userLIst.isEmpty();
    }

    public void resetPassword(Long id) {

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 계정입니다."));

        String encodedPassword = passwordEncoder.encode(user.getUserId());
        user.setPassword(encodedPassword);
    }


    public void addUser(AddUserRequestDto addUserRequestDto) {

        // 1. 유저 계정 이미 존재하는지 확인
        List<User> userList = userRepository.findAllByUserId(
            addUserRequestDto.getUserId());

        if (!userList.isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 유저 계정입니다.");
        }

        // 2. 어드민 계정이 존재하는지 확인
        Admin admin = adminRepository.findById(
                addUserRequestDto.getAdminIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 어드민 계정입니다."));

        // 3. 닉네임 중복 확인
        List<User> userListByNicknameList = userRepository.findAllByNickname(
            addUserRequestDto.getNickname());

        if (!userListByNicknameList.isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        UserCategory userCategory = userCategoryRepository.findById(addUserRequestDto.getUserCategoryIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 4. 최상위 카테고리는 유저 계정에 추가할 수 없다.
        if(userCategory.getParent() == null ) {
            throw new IllegalArgumentException("최상위 카테고리는 유저 계정에 추가할 수 없습니다.");
        }
        // depth 가 1이하면 최상위 카테고리이므로 유저 계정에 추가할 수 없다.
        if(userCategory.getDepth() <= UserCategoryDepth.MIDDLE_DEPTH.getDepth()) {
            throw new IllegalArgumentException("최상위 카테고리는 유저 계정에 추가할 수 없습니다.");
        }


        // 5. Password Encoding (id 를 password 로 사용)
        String encodedPassword = passwordEncoder.encode(addUserRequestDto.getUserId());

        // 6. Front 에서 받은 정보로 유저 계정 생성
        User user = User.builder()
            .userId(addUserRequestDto.getUserId())
            .password(encodedPassword)
            .nickname(addUserRequestDto.getNickname())
            .memo(addUserRequestDto.getMemo())
            .admin(admin)
            .approvedLevel(1)
            .adminChangeLevel(1)
            .build();

        user.setUserCategory(userCategory);
        userRepository.save(user);

        if(addUserRequestDto.getProfileImage() != null) {
            try {
                MultipartFile profileImage = addUserRequestDto.getProfileImage();
                uploadProfileImage(user, profileImage);
            } catch (IOException e) {
                throw new IllegalArgumentException("프로필 이미지 업로드 중 오류가 발생했습니다.");
            }
        }

    }


    public Page<UserDto> getUserList(Pageable pageable, GetUserRequestDto getUserRequestDto) {

        /*회원 정보*/
        Page<User> userList = userRepository.finaAllByNicknameContainingAndCreatedAtBetween(
            pageable,
            getUserRequestDto.getKeyword(),
            getUserRequestDto.getStartDate() != null ? getUserRequestDto.getStartDate().atStartOfDay() : null,
            getUserRequestDto.getEndDate() != null ? getUserRequestDto.getEndDate().atTime(23, 59, 59) : null,
            getUserRequestDto.getUserCategoryIdx() != null ? getUserRequestDto.getUserCategoryIdx() : null,
            getUserRequestDto.getUserCategoryParentIdx() != null ? getUserRequestDto.getUserCategoryParentIdx() : null
        );

        return userList.map(
            user -> {
                return UserDto.builder()
                    .id(user.getId())
                    .userId(user.getUserId())
                    .nickname(user.getNickname())
                    .memo(user.getMemo())
                    .approvedLevel(user.getApprovedLevel())
                    .typeInspection(user.getTypeInspection())
                    .adminChangeLevel(user.getAdminChangeLevel())
                    .userCategory(UserCategoryDto.builder()
                        .id(user.getUserCategory().getId())
                        .title(user.getUserCategory().getTitle())
                        .build())
                    .createdAt(user.getCreatedAt())
                    .admin(
                        AdminDto.builder()
                            .id(user.getId())
                            .nickname(user.getAdmin().getNickname())
                            .build()
                    )
                    .build();
            }
        );

    }

    public UserDto getUser(Long id) {

        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 계정입니다."));

        return UserDto.convertToDto(user, true);
    }



    public void modifyUser(ModifyUserRequestDto modifyUserRequestDto)
        throws JsonProcessingException {

        // 유저 뽑아오기
        User user = userRepository.findById(modifyUserRequestDto.getUserIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 계정입니다."));

        // 어드민 뽑아오기
        Admin admin = adminRepository.findById(modifyUserRequestDto.getAdminIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 어드민 계정입니다."));

        // 카테고리 뽑아오기
        UserCategory userCategory = userCategoryRepository.findById(modifyUserRequestDto.getUserCategoryIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 최상위 카테고리는 유저 계정에 추가할 수 없다.
        if(userCategory.getParent() == null ) {
            throw new IllegalArgumentException("최상위 카테고리는 유저 계정에 추가할 수 없습니다.");
        }
        // depth 가 1이하면 최상위 카테고리이므로 유저 계정에 추가할 수 없다.
        if(userCategory.getDepth() <= UserCategoryDepth.MIDDLE_DEPTH.getDepth()) {
            throw new IllegalArgumentException("최상위 카테고리는 유저 계정에 추가할 수 없습니다.");
        }

        // 닉네임 중복 확인 -> 닉네임이 변경되었을 때만 확인
        if(StringUtils.hasText(modifyUserRequestDto.getNickname())) {
            Optional<User> checkNicknameEntity = userRepository.findOptionalByNickname(
                modifyUserRequestDto.getNickname());
            // 닉네임이 존재하고, 기존 닉네임과 다를 때
            if (checkNicknameEntity.isPresent() && !checkNicknameEntity.get().getNickname().equals(user.getNickname())) {
                throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
            }
        }

        // 닉네임, 승인 레벨, 메모 수정
        user.modifyUser(modifyUserRequestDto);

        // 유저 카테고리(권한) 삭제 (추후 여러 권한 가질 가능성 고려해 deleteAllBy 사용)
        user.setUserCategory(userCategory);

        // 어드민 수정
        user.setUserAdmin(admin);


        if(modifyUserRequestDto.getProfileImage() != null) {
            try {
                // 기존 프로필 이미지 삭제
                UserProfileImage userProfileImage = userProfileImageRepository.findByUser(user);
                if(userProfileImage != null) {
                    s3FileUtils.deleteFile(userProfileImage.getFilePath());
                    userProfileImageRepository.delete(userProfileImage);
                    entityManager.flush();
                }
                MultipartFile profileImage = modifyUserRequestDto.getProfileImage();
                uploadProfileImage(user, profileImage);
            } catch (IOException e) {
                throw new IllegalArgumentException("프로필 이미지 업로드 중 오류가 발생했습니다.");
            }
        }

        // 활동레벨 변경 시 이전 레벨 저장
        if (modifyUserRequestDto.getAdminChangeLevel() != null && (modifyUserRequestDto.getAdminChangeLevel() != user.getApprovedLevel())) {

            // 활동 레벨 변경 시 이전 레벨로 저장 불가
            if (modifyUserRequestDto.getAdminChangeLevel() < user.getApprovedLevel()) {
                throw new IllegalArgumentException("기존 활동 레벨 혹은 더 아래 레벨로 변경할 수 없습니다.");
            }

            // 활동 레벨 변경 시 변경될 레벨이 이전 레벨 보다 크면 이전 레벨 저장
//            user.setBeforeLevel(user.getApprovedLevel());
//            user.setApprovedLevel(modifyUserRequest.getApprovedLevel());
            user.setAdminChangeLevel(modifyUserRequestDto.getAdminChangeLevel());
            // bool type 업데이트 추가 해야 함
//            user.setLevelUpAlarm(true);
            // socket 도 쏴줘야 함

            String userId = user.getUserId();
            String sessionValue = redisUtils.getValuesWithPrefix(userId, SessionType.SESSION_ID.getType());

            if(sessionValue != null) {
                RefreshSocketDto refreshSocketDto = RefreshSocketDto.builder()
                    .session(sessionValue)
                    .type(RefreshSocketType.USER_TYPE.getType())
                    .action(RefreshSocketType.UPDATE_ACTION.getType())
                    .message("The youth information has been updated.")
                    .dateTime(LocalDateTime.now())
                    .build();

                String json = objectMapper.writeValueAsString(refreshSocketDto);
                redisPublisher.publish(levelUpChannelTopic, json);
            }
        }

    }

    public void deleteUser(Long id) {

        // 유저 뽑아오기
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 계정입니다."));

        user.setLeaveDate(LocalDateTime.now());
    }


    private void uploadProfileImage(User user, MultipartFile profileImage)
        throws IOException {
        FileDto profileImageDto = s3FileUtils.uploadFile(govNcpFilePath, profileImage, 5L, FileType.USER_PROFILE_IMAGE);
        if(profileImageDto != null) {
            UserProfileImage userImage =
                UserProfileImage.builder()
                    .fileExtension(profileImageDto.getFileExtension())
                    .originFileName(profileImageDto.getOriginFileName())
                    .savedFileName(profileImageDto.getSavedFileName())
                    .filePath(profileImageDto.getFilePath())
                    .user(user)
                    .url(profileImageDto.getUrl())
                    .build();
            userProfileImageRepository.save(userImage);
        }
    }





}
