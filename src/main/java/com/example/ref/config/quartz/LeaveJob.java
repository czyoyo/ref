package com.example.ref.config.quartz;

import com.example.ref.entity.Admin;
import com.example.ref.entity.User;
import com.example.ref.entity.UserCategory;
import com.example.ref.repository.AdminImageRepository;
import com.example.ref.repository.AdminProfileImageRepository;
import com.example.ref.repository.AdminRepository;
import com.example.ref.repository.UserCategoryRepository;
import com.example.ref.repository.UserProfileImageRepository;
import com.example.ref.repository.UserRepository;
import com.example.ref.rules.WithdrawalType;
import com.example.ref.util.S3FileUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@Slf4j
public class LeaveJob implements Job {

    private static final String LEAVE_TRIGGER = "leaveTrigger";
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final S3FileUtils s3FileUtils;
    private final UserProfileImageRepository userProfileImageRepository;
    private final AdminProfileImageRepository adminProfileImageRepository;
    private final AdminImageRepository adminImageRepository;
    @Value("${path.gov-ncp-location}")
    private String govNcpFilePath;

    public LeaveJob(UserRepository userRepository, AdminRepository adminRepository, UserCategoryRepository userCategoryRepository,
        S3FileUtils s3FileUtils,
        UserProfileImageRepository userProfileImageRepository,
        AdminProfileImageRepository adminProfileImageRepository,
        AdminImageRepository adminImageRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.userCategoryRepository = userCategoryRepository;
        this.s3FileUtils = s3FileUtils;
        this.userProfileImageRepository = userProfileImageRepository;
        this.adminProfileImageRepository = adminProfileImageRepository;
        this.adminImageRepository = adminImageRepository;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("LeaveJob.execute()");
        if(jobExecutionContext.getTrigger().getKey().getName().equals(LEAVE_TRIGGER)) {
            // 유저 정보 삭제
            UserCategory userCategory = userCategoryRepository.findByClassification(WithdrawalType.WITHDRAWAL_TYPE.getType());
            try{
                List<User> userList = userRepository.findAllByLeaveDateIsNotNull();
                for(User user : userList) {
                    user.withdrawUser(userCategory);
                    // 이미지 삭제, 레포지 토리 삭제
                    if(user.getUserProfileImage() != null) {
                        s3FileUtils.deleteFile(user.getUserProfileImage().getFilePath());
                        userProfileImageRepository.delete(user.getUserProfileImage());
                    }
                }
            } catch(Exception e) {
                log.error("userRepository.deleteAll(userRepository.findAllByLeaveDateIsNotNull()) error", e);
            }
            try {
                // 관리자 유저 정보 삭제
                List<Admin> adminList = adminRepository.findAllByLeaveDateIsNotNull();
                for(Admin admin : adminList) {
                    admin.withdrawAdmin(userCategory);

                    if(admin.getAdminProfileImage() != null) {
                        s3FileUtils.deleteFile(admin.getAdminProfileImage().getFilePath());
                        adminProfileImageRepository.delete(admin.getAdminProfileImage());
                    }
                    if(!admin.getAdminImageList().isEmpty()) {
                        for(int i = 0; i < admin.getAdminImageList().size(); i++) {
                            s3FileUtils.deleteFile(admin.getAdminImageList().get(i).getFilePath());
                            adminImageRepository.delete(admin.getAdminImageList().get(i));
                        }
                    }

                }
            } catch (Exception e) {
                log.error("userAdminRepository.deleteAll(userAdminRepository.findAllByLeaveDateIsNotNull()) error", e);
            }
        }
    }
}
