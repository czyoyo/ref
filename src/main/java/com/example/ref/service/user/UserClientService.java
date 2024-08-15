package com.example.ref.service.user;

import com.example.ref.annotation.NoAuthCheck;
import com.example.ref.config.jwt.JwtTokenProvider;
import com.example.ref.dto.UserDto;
import com.example.ref.entity.User;
import com.example.ref.entity.UserCategory;
import com.example.ref.repository.UserCategoryRepository;
import com.example.ref.repository.UserRepository;
import com.example.ref.request.user.user.LoginClientUserRequestDto;
import com.example.ref.response.CommonResponse;
import com.example.ref.response.JwtResponse;
import com.example.ref.rules.GuestType;
import com.example.ref.rules.ResponseCode;
import com.example.ref.util.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserClientService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    @Value("${jwt.header}")
    private String AUTHORIZATION_HEADER;
    @Value("${jwt.roles}")
    private String ROLES_HEADER;
    @Value("${jwt.refresh-token-header}")
    private String REFRESH_TOKEN_HEADER;
    private final UserCategoryRepository userCategoryRepository;


    @NoAuthCheck
    public ResponseEntity<CommonResponse<JwtResponse>> guestLogin(HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();
        String id = ip + "_" + userAgent;

        String encodedPassword = passwordEncoder.encode(id);

        List<String> roles = List.of(GuestType.GUEST.getType());

        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add((GrantedAuthority) () -> role));

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(id, encodedPassword, authorities);

        String accessToken = tokenProvider.createAccessToken(authenticationToken);

        JwtResponse jwtResponse = JwtResponse.builder()
            .accessToken(accessToken)
            .roles(String.join(",", roles))
            .build();

        CommonResponse<JwtResponse> jwtResponseCommonResponse = CommonResponse.<JwtResponse>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(jwtResponse)
            .build();

        return ResponseEntity.ok()
            .header(AUTHORIZATION_HEADER, "Bearer " + accessToken)
            .header(ROLES_HEADER, String.join(",", roles))
            .body(jwtResponseCommonResponse);
    }



    @NoAuthCheck
    public ResponseEntity<CommonResponse<JwtResponse>> login(
        LoginClientUserRequestDto loginUnityUserRequest) {
        // 1. 유저 계정이 존재하는지 확인
        User user = userRepository.findOptionalByUserId(loginUnityUserRequest.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 계정입니다."));

        // 유저 계정의 회원 탈퇴일이 존재하면 로그인 불가
        if(user.getLeaveDate() != null) {
            throw new IllegalArgumentException("탈퇴한 계정입니다.");
        }

        // 2. 유저 계정 비밀번호 확인
        if(!passwordEncoder.matches(loginUnityUserRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 권한조회,
        UserCategory userCategory = user.getUserCategory();

//        List<String> roles = userUserCategorieList.stream()
//            .map(userUserCategory -> userUserCategory.getUserCategory().getClassification())
//            .toList();

        List<String> roles = new ArrayList<>();
        roles.add(userCategory.getClassification());


        List<GrantedAuthority> authorities = new ArrayList<>();

        for(String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginUnityUserRequest.getUserId(), loginUnityUserRequest.getPassword(), authorities
        );

        String accessToken = tokenProvider.createAccessToken(authenticationToken);
        String refreshToken = tokenProvider.createRefreshToken(authenticationToken);

        JwtResponse jwtResponse = JwtResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .roles(String.join(",", roles))
            .build();

        CommonResponse<JwtResponse> commonResponse = CommonResponse.<JwtResponse>builder()
            .status(ResponseCode.SUCCESS.getStatus())
            .code(ResponseCode.SUCCESS.getCode())
            .data(jwtResponse)
            .build();

        user.setRefreshToken(refreshToken);

        return ResponseEntity.ok()
            .header(AUTHORIZATION_HEADER, "Bearer " + accessToken)
            .header(ROLES_HEADER, authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
            .header(REFRESH_TOKEN_HEADER, refreshToken)
            .body(commonResponse);
    }

    public UserDto getMyInfo() {

        String userId = AuthUtils.getUserId();
        User user = userRepository.findByUserId(userId);



        return UserDto.builder()
            .id(user.getId())
            .userId(user.getUserId())
            .nickname(user.getNickname())
            .memo(user.getMemo())
            .approvedLevel(user.getApprovedLevel())
            .typeInspection(user.getTypeInspection())
            .adminChangeLevel(user.getAdminChangeLevel())
            .build();
    }

    public void levelUp() {
        String userId = AuthUtils.getUserId();

        User user = userRepository.findByUserId(userId);

        // 레벨업 요청
        if(user.getAdminChangeLevel() != null) {
            user.setApprovedLevel(user.getAdminChangeLevel());
            user.setAdminChangeLevel(null);
        }
    }


}
