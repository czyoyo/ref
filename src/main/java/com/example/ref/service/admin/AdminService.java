package com.example.ref.service.admin;

import com.amazonaws.services.s3.AmazonS3Client;
import com.example.ref.annotation.NoAuthCheck;
import com.example.ref.config.jwt.JwtTokenProvider;
import com.example.ref.dto.AdminDto;
import com.example.ref.dto.FileDto;
import com.example.ref.entity.Admin;
import com.example.ref.entity.AdminImage;
import com.example.ref.entity.AdminProfileImage;
import com.example.ref.entity.UserCategory;
import com.example.ref.repository.AdminImageRepository;
import com.example.ref.repository.AdminProfileImageRepository;
import com.example.ref.repository.AdminRepository;
import com.example.ref.repository.UserCategoryRepository;
import com.example.ref.request.admin.admin.AddAdminRequestDto;
import com.example.ref.request.admin.admin.ChangeMyPasswordRequestDto;
import com.example.ref.request.admin.admin.GetAdminRequestDto;
import com.example.ref.request.admin.admin.LoginAdminRequestDto;
import com.example.ref.request.admin.admin.ModifyAdminRequestDto;
import com.example.ref.request.admin.admin.ModifyMyInfoRequestDto;
import com.example.ref.response.CommonResponse;
import com.example.ref.response.JwtResponse;
import com.example.ref.rules.FileType;
import com.example.ref.rules.RedisType;
import com.example.ref.rules.ResponseCode;
import com.example.ref.rules.UserCategoryDepth;
import com.example.ref.util.AuthUtils;
import com.example.ref.util.RedisUtils;
import com.example.ref.util.S3FileUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminImageRepository adminImageRepository;
    private final AdminProfileImageRepository adminProfileImageRepository;
    private final JwtTokenProvider tokenProvider;
    @Value("${jwt.header}")
    private String AUTHORIZATION_HEADER;
    @Value("${jwt.roles}")
    private String ROLES_HEADER;
    @Value("${spring.servlet.multipart.location}")
    private String filePath;
    @Value("${path.gov-ncp-location}")
    private String govNcpFilePath;
    @Value("${jwt.refresh-token-header}")
    private String REFRESH_TOKEN_HEADER;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3Client amazonS3Client;

    private final S3FileUtils s3FileUtils;

    private final EntityManager entityManager;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisUtils redisUtils;

    public void changeMyPassword(ChangeMyPasswordRequestDto request) {
        String userId = AuthUtils.getUserId();
        Admin admin = adminRepository.findOptionalByAdminId(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정 입니다."));

        // 기존 비밀 번호 확인
        if(!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀 번호 인코딩
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());

        admin.setPassword(encodedPassword);
    }


    public AdminDto getMyInfo() {
        String userId = AuthUtils.getUserId();
        Admin admin = adminRepository.findOptionalByAdminId(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정 입니다."));

        return AdminDto.convertToDto(admin, true, true);
    }

    public void modifyMyInfo(ModifyMyInfoRequestDto request) throws IOException {
        String userId = AuthUtils.getUserId();
        Admin admin = adminRepository.findOptionalByAdminId(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정 입니다."));

        // 닉네임 중복 확인, 닉네임 왔을때만 확인
        if(StringUtils.hasText(request.getNickname())) {
            Optional<Admin> checkNicknameEntity = adminRepository.findOptionalByNickname(
                request.getNickname());

            if(checkNicknameEntity.isPresent() && !admin.getNickname().equals(request.getNickname())){
                throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
            }
        }

        admin.modifyMyInfo(request);

        if(request.getIntroImage01() != null && !request.getIntroImage01().isEmpty()) {
            AdminImage adminImage = adminImageRepository.findByAdminAndType(
                admin, FileType.INTRO_IMAGE_01.getPath());
            if(adminImage != null) {
                s3FileUtils.deleteFile(adminImage.getFilePath());
                adminImageRepository.deleteByAdminAndType(admin, FileType.INTRO_IMAGE_01.getPath());
                entityManager.flush();
            }
            MultipartFile introImage = request.getIntroImage01();
            uploadIntroImages(admin, introImage, FileType.INTRO_IMAGE_01);
        }

        // introImage02 있으면 기존 이미지 삭제하고 저장
        if(request.getIntroImage02() != null && !request.getIntroImage02().isEmpty()) {
            AdminImage adminImage = adminImageRepository.findByAdminAndType(
                admin, FileType.INTRO_IMAGE_02.getPath());
            if(adminImage != null) {
                s3FileUtils.deleteFile(adminImage.getFilePath());
                adminImageRepository.deleteByAdminAndType(admin, FileType.INTRO_IMAGE_02.getPath());
                entityManager.flush();
            }
            MultipartFile introImage = request.getIntroImage02();
            uploadIntroImages(admin, introImage, FileType.INTRO_IMAGE_02);
        }

        // 프로필 이미지 저장 -> 프로필 이미지는 하나일수 밖에 없으므로 기존에 존재하면 삭제 후 저장
        if(request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {

            // 기존 프로필 이미지 존재 시 삭제
            AdminProfileImage adminProfileImage = adminProfileImageRepository.findByAdmin(admin);

            if(adminProfileImage != null) {
                s3FileUtils.deleteFile(adminProfileImage.getFilePath());
                adminProfileImageRepository.deleteByAdmin(adminProfileImage.getAdmin());
                entityManager.flush();
            }
            MultipartFile profileImage = request.getProfileImage();
            uploadProfileImage(admin, profileImage);
        }

    }



    public boolean checkAdminId(String adminId) {
        List<Admin> adminList = adminRepository.findAllByAdminId(adminId);
        return !adminList.isEmpty();
    }

    public boolean checkAdminNickname(String nickname) {
        List<Admin> adminList = adminRepository.findAllByNickname(nickname);
        return !adminList.isEmpty();
    }

    public void deleteAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정 입니다."));

        // depth 1 이하이면 수퍼 계정이므로 삭제가 불가능 하다.
        if(admin.getUserCategory().getDepth() <= UserCategoryDepth.MIDDLE_DEPTH.getDepth()) {
            throw new IllegalArgumentException("최상위 카테고리는 삭제할 수 없습니다.");
        }

        admin.setLeaveDate(LocalDateTime.now());
    }

    @NoAuthCheck
    public void resetPassword(Long id) {
        Admin admin = adminRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정 입니다."));
        String encodedPassword = passwordEncoder.encode(admin.getAdminId());
        admin.setPassword(encodedPassword);
    }

    public void deleteIntroImage(Long id) {
        AdminImage adminImage = adminImageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지 입니다."));
        s3FileUtils.deleteFile(adminImage.getFilePath());
        adminImageRepository.delete(adminImage);
    }

    public void deleteProfileImage(Long id) {
        AdminProfileImage adminProfileImage = adminProfileImageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지 입니다."));
        s3FileUtils.deleteFile(adminProfileImage.getFilePath());
        adminProfileImageRepository.delete(adminProfileImage);
    }

    public void modifyAdmin(ModifyAdminRequestDto modifyAdminRequestDto) throws IOException {
        Admin admin = adminRepository.findById(
                modifyAdminRequestDto.getAdminIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정 입니다."));

        // depth 1 이하이면 수퍼 계정이므로 수정이 불가능 하다.
        if(admin.getUserCategory().getDepth() <= UserCategoryDepth.MIDDLE_DEPTH.getDepth()) {
            throw new IllegalArgumentException("최상위 카테고리는 수정할 수 없습니다.");
        }

        // 닉네임 중복 확인, 닉네임 왔을때만 확인
        if(StringUtils.hasText(modifyAdminRequestDto.getNickname())) {
            Optional<Admin> checkNicknameEntity = adminRepository.findOptionalByNickname(
                modifyAdminRequestDto.getNickname());

            if(checkNicknameEntity.isPresent() && !admin.getNickname().equals(modifyAdminRequestDto.getNickname())){
                throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
            }

        }

        // 카테고리 불러오기
        UserCategory userCategory = userCategoryRepository.findById(modifyAdminRequestDto.getCategoryIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 4. 최상위 카테고리는 유저 계정에 추가할 수 없다.
        if(userCategory.getParent() == null ) {
            throw new IllegalArgumentException("최상위 카테고리는 유저 계정에 추가할 수 없습니다.");
        }
        // depth 가 1이하면 최상위 카테고리이므로 유저 계정에 추가할 수 없다.
        if(userCategory.getDepth() <= UserCategoryDepth.MIDDLE_DEPTH.getDepth()) {
            throw new IllegalArgumentException("최상위 카테고리는 유저 계정에 추가할 수 없습니다.");
        }

        // 비밀번호 변경 시
        if(StringUtils.hasText(modifyAdminRequestDto.getPassword())) {
            String encodedPassword = passwordEncoder.encode(modifyAdminRequestDto.getPassword());
            admin.setPassword(encodedPassword);
        }

        // 일반 정보 변경
        admin.setUserCategory(userCategory);
        admin.modifyAdmin(modifyAdminRequestDto);

        // 기존 소개 이미지 존재 시 삭제 하려 했으나 여러개라 삭제는 비동기로 처리 -> 비동기 이미지 삭제 API 만들어야 함 왜냐하면 이미지 추가하는 상황도 있을것이기 때문
        // 소개 이미지 저장
//        List<MultipartFile> introImage = modifyAdminRequest.getIntroImageList();
//        uploadIntroImages(admin, introImage);

        // introImage01 있으면 기존 이미지 삭제하고 저장
        if(modifyAdminRequestDto.getIntroImage01() != null && !modifyAdminRequestDto.getIntroImage01().isEmpty()) {
            AdminImage adminImage = adminImageRepository.findByAdminAndType(
                admin, FileType.INTRO_IMAGE_01.getPath());
            if(adminImage != null) {
                s3FileUtils.deleteFile(adminImage.getFilePath());
                adminImageRepository.deleteByAdminAndType(admin, FileType.INTRO_IMAGE_01.getPath());
                entityManager.flush();
            }
            MultipartFile introImage = modifyAdminRequestDto.getIntroImage01();
            uploadIntroImages(admin, introImage, FileType.INTRO_IMAGE_01);
        }

        // introImage02 있으면 기존 이미지 삭제하고 저장
        if(modifyAdminRequestDto.getIntroImage02() != null && !modifyAdminRequestDto.getIntroImage02().isEmpty()) {
            AdminImage adminImage = adminImageRepository.findByAdminAndType(
                admin, FileType.INTRO_IMAGE_02.getPath());
            if(adminImage != null) {
                s3FileUtils.deleteFile(adminImage.getFilePath());
                adminImageRepository.deleteByAdminAndType(admin, FileType.INTRO_IMAGE_02.getPath());
                entityManager.flush();
            }
            MultipartFile introImage = modifyAdminRequestDto.getIntroImage02();
            uploadIntroImages(admin, introImage, FileType.INTRO_IMAGE_02);
        }




        // 프로필 이미지 저장 -> 프로필 이미지는 하나일수 밖에 없으므로 기존에 존재하면 삭제 후 저장
        if(modifyAdminRequestDto.getProfileImage() != null && modifyAdminRequestDto.getProfileImage() != null) {

            // 기존 프로필 이미지 존재 시 삭제
            AdminProfileImage adminProfileImage = adminProfileImageRepository.findByAdmin(admin);

            if(adminProfileImage != null) {
                s3FileUtils.deleteFile(adminProfileImage.getFilePath());
                adminProfileImageRepository.deleteByAdmin(adminProfileImage.getAdmin());
                entityManager.flush();
            }
            MultipartFile profileImage = modifyAdminRequestDto.getProfileImage();
            uploadProfileImage(admin, profileImage);
        }


    }

    public Page<AdminDto> getAdminList(Pageable pageable, @ModelAttribute GetAdminRequestDto adminRequest) {
        Page<Admin> admins = adminRepository.finaAllByNicknameContainingAndCreatedAtBetween(
            pageable,
            adminRequest.getKeyword(),
            adminRequest.getStartDate() != null ? adminRequest.getStartDate().atStartOfDay() : null,
            adminRequest.getEndDate() != null ? adminRequest.getEndDate().atStartOfDay() : null,
            adminRequest.getUserCategoryIdx(),
            adminRequest.getUserCategoryParentIdx()
        );
        return admins.map(admin -> AdminDto.convertToDto(admin, true, false));
    }

    public AdminDto getAdmin(Long id) {
        Admin admin = adminRepository.findByIdAndAdminImages(id);
        if(admin == null) {
            throw new IllegalArgumentException("유저 정보가 없습니다.");
        }

        return AdminDto.convertToDto(admin, true, true);
    }

    @NoAuthCheck
    public ResponseEntity<CommonResponse<JwtResponse>> login(
        LoginAdminRequestDto loginAdminRequestDto) {

        // 1. 어드민 계정이 존재하는지 확인
        Admin admin = adminRepository.findOptionalByAdminId(
                loginAdminRequestDto.getAdminId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디 입니다."));

        // 2. 어드민 계정 비밀번호 확인
        if(!passwordEncoder.matches(loginAdminRequestDto.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("잘못된 아이디 또는 비밀번호 입니다.");
        }

        List<String> roles = new ArrayList<>();
        roles.add(admin.getUserCategory().getClassification());

        List<GrantedAuthority> authorities = new ArrayList<>();
        for(String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginAdminRequestDto.getAdminId(), loginAdminRequestDto.getPassword(), authorities);

        String accessToken = tokenProvider.createAccessToken(authenticationToken);
        String refreshToken = tokenProvider.createRefreshToken(authenticationToken);

        JwtResponse jwtResponse = JwtResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken) // DB 에 저장 하지만 Redis 에 저장 된걸 사용
            .roles(String.join(",", roles))
//            .roles(authority.getAuthority())
            .build();


        CommonResponse<JwtResponse> commonResponse = CommonResponse.<JwtResponse>builder()
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .data(jwtResponse)
            .build();


        admin.setRefreshToken(refreshToken);

        return ResponseEntity.ok()
            .header(AUTHORIZATION_HEADER, "Bearer " + accessToken)
            .header(REFRESH_TOKEN_HEADER, refreshToken)
            .header(ROLES_HEADER, String.join(",", roles))
//            .header(ROLES_HEADER, authority.getAuthority())
            .body(commonResponse);
    }

    public CommonResponse<Object> addAdmin(AddAdminRequestDto addAdminRequestDto)
        throws IOException {
        // 1. 어드민 계정 이미 존재하는지 확인
        List<Admin> adminList = adminRepository.findAllByAdminId(
            addAdminRequestDto.getAdminId());
        if(!adminList.isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 어드민 계정입니다.");
        }

        // 2. 닉네임 중복 확인
        List<Admin> adminListByNicknameList = adminRepository.findAllByNickname(
            addAdminRequestDto.getNickname());
        if(!adminListByNicknameList.isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // 3. Password Encoding (id 를 password 로 사용)
        String encodedPassword = passwordEncoder.encode(addAdminRequestDto.getAdminId());

        // 4. Category 불러오기
        UserCategory userCategory = userCategoryRepository.findById(addAdminRequestDto.getCategoryIdx())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));


        // 6. Front 에서 받은 정보로 어드민 계정 생성
        Admin admin = Admin.builder()
            .adminId(addAdminRequestDto.getAdminId())
            .password(encodedPassword)
            .nickname(addAdminRequestDto.getNickname())
            .memo(addAdminRequestDto.getMemo())
            .build();
        admin.setUserCategory(userCategory);
        adminRepository.save(admin);

        // 5. 파일 저장 후 DB 에 저장
        if(addAdminRequestDto.getProfileImage() != null) {
            MultipartFile profileImage = addAdminRequestDto.getProfileImage();
            uploadProfileImage(admin, profileImage);
        }

        if(addAdminRequestDto.getIntroImage01() != null && !addAdminRequestDto.getIntroImage01().isEmpty()) {
            // 소개 이미지 01 저장
            MultipartFile introImage = addAdminRequestDto.getIntroImage01();
            uploadIntroImages(admin, introImage, FileType.INTRO_IMAGE_01);
        }

        if(addAdminRequestDto.getIntroImage02() != null && !addAdminRequestDto.getIntroImage02().isEmpty()) {
            // 소개 이미지 02 저장
            MultipartFile introImage = addAdminRequestDto.getIntroImage02();
            uploadIntroImages(admin, introImage, FileType.INTRO_IMAGE_02);
        }

        return CommonResponse.builder()
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .data(null)
            .build();
    }

    // 프로필 이미지 업로드
    private void uploadIntroImages(Admin admin, List<MultipartFile> introImages) throws IOException {
        if(introImages == null) {
            return;
        }
        for(MultipartFile multipartFile : introImages) {
            FileDto fileDto = s3FileUtils.uploadFile(govNcpFilePath, multipartFile, 5L, FileType.INTRO_IMAGE);
            if(fileDto != null) {
                AdminImage adminImage =
                    AdminImage.builder()
                        .fileExtension(fileDto.getFileExtension())
                        .originFileName(fileDto.getOriginFileName())
                        .savedFileName(fileDto.getSavedFileName())
                        .filePath(fileDto.getFilePath())
                        .admin(admin)
                        .url(fileDto.getUrl())
                        .build();
                adminImageRepository.save(adminImage);
            }
        }
    }

    // 프로필 이미지 단일 업로드
    private void uploadIntroImages(Admin admin, MultipartFile introImage, FileType fileType) throws IOException {
        if(introImage == null) {
            return;
        }
        FileDto introImageDto = s3FileUtils.uploadFile(govNcpFilePath, introImage, 5L, fileType);
        if(introImageDto != null) {
            AdminImage adminImage =
                AdminImage.builder()
                    .fileExtension(introImageDto.getFileExtension())
                    .originFileName(introImageDto.getOriginFileName())
                    .savedFileName(introImageDto.getSavedFileName())
                    .filePath(introImageDto.getFilePath())
                    .admin(admin)
                    .url(introImageDto.getUrl())
                    .type(fileType.getPath())
                    .build();
            adminImageRepository.save(adminImage);
        }
    }

    private void uploadProfileImage(Admin admin, MultipartFile profileImage) throws IOException {
        FileDto profileImageDto = s3FileUtils.uploadFile(govNcpFilePath, profileImage, 5L, FileType.ADMIN_PROFILE_IMAGE);
        if(profileImageDto != null) {
            AdminProfileImage adminImage =
                AdminProfileImage.builder()
                    .fileExtension(profileImageDto.getFileExtension())
                    .originFileName(profileImageDto.getOriginFileName())
                    .savedFileName(profileImageDto.getSavedFileName())
                    .filePath(profileImageDto.getFilePath())
                    .admin(admin)
                    .url(profileImageDto.getUrl())
                    .build();
            adminProfileImageRepository.save(adminImage);
        }
    }


    public ResponseEntity<CommonResponse<JwtResponse>> refresh(HttpServletRequest httpServletRequest) {

        String refreshToken = httpServletRequest.getHeader(REFRESH_TOKEN_HEADER);

        if(refreshToken == null) {
            throw new IllegalArgumentException("JWT Token이 존재하지 않습니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(refreshToken);

        String redisRefreshToken = redisUtils.getValuesWithPrefix(authentication.getName(),
            RedisType.REFRESH_TOKEN.getType());

        // redis 에 저장된 리프레시 토큰이 없으면 만료된 토큰이다.
        if(!StringUtils.hasText(redisRefreshToken)) {
            throw new ExpiredJwtException(null, null, "만료된 리프레시 토큰입니다.");
        }

        // redis 에 저장된 리프레시 토큰과 요청받은 리프레시 토큰이 같은지 확인
        if(!redisRefreshToken.equals(refreshToken)) {
            throw new ExpiredJwtException(null, null, "잘못된 리프레시 토큰 입니다.");
        }

        // refresh Token 검증
        if(!tokenProvider.validateToken(refreshToken)) {
            throw new ExpiredJwtException(null, null, "리프레시 토큰 만료");
        }

        // refresh Token 탈퇴된 회원인지 존재 하는지 체크
        String refreshTokenUserId = tokenProvider.getAuthenticationName(refreshToken);

        // 유저 조회
        Admin admin = adminRepository.findOptionalByAdminId(refreshTokenUserId)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        // 유저 DB에 리프레시 토큰이 있는지 확인
        if(!StringUtils.hasText(admin.getRefreshToken())) {
            throw new ExpiredJwtException(null, null, "리프레시 토큰이 존재하지 않습니다.");
        }

        // 유저의 refreshToken 과 요청받은 refreshToken 이 같은지 확인
        if(!admin.getRefreshToken().equals(refreshToken)) {
            throw new ExpiredJwtException(null, null, "잘못된 리프레시 토큰 입니다.");
        }

        // 토큰 재발급 (리프레시 토큰으로 부터)
        String newAccessToken = tokenProvider.createAccessToken(tokenProvider.getAuthentication(refreshToken));

        // 재발급된 토큰으로 부터 roles 를 가져온다.
        Collection<? extends GrantedAuthority> authorities = tokenProvider.getSimpleGrantedAuthorities(refreshToken);

        JwtResponse jwtResponse = JwtResponse.builder()
            .accessToken(newAccessToken)
            .build();

        CommonResponse<JwtResponse> commonResponse = CommonResponse.<JwtResponse>builder()
            .code(ResponseCode.SUCCESS.getCode())
            .status(ResponseCode.SUCCESS.getStatus())
            .data(jwtResponse)
            .build();

        // 리프레시 토큰은 재발행 하지 말자는 의견(계속 갱신되면 영원히 존재할수도 있다.) 1회성 리프래시 토큰 발급 하면 되지만 일단은 그냥 둠
        return ResponseEntity.ok()
            .header(AUTHORIZATION_HEADER, "Bearer " + newAccessToken)
            .header(ROLES_HEADER, authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
            .header(REFRESH_TOKEN_HEADER, refreshToken)
            .body(commonResponse);
    }



}
