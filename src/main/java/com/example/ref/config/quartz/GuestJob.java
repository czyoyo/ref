package com.example.ref.config.quartz;

import com.example.ref.entity.Admin;
import com.example.ref.entity.User;
import com.example.ref.repository.AdminImageRepository;
import com.example.ref.repository.AdminProfileImageRepository;
import com.example.ref.repository.AdminRepository;
import com.example.ref.repository.UserProfileImageRepository;
import com.example.ref.repository.UserRepository;
import com.example.ref.rules.GuestType;
import com.example.ref.util.S3FileUtils;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@Slf4j
public class GuestJob implements Job {

    private final String GUEST_TRIGGER = "guestTrigger";
    private final UserRepository userRepository;
    private final S3FileUtils s3FileUtils;
    private final UserProfileImageRepository userProfileImageRepository;
    private final AdminRepository adminRepository;
    private final AdminProfileImageRepository adminProfileImageRepository;
    private final AdminImageRepository adminImageRepository;
    private final EntityManager entityManager;
    @Value("${path.gov-ncp-location}")
    private String govNcpFilePath;


    public GuestJob(
        UserRepository userRepository,
        S3FileUtils s3FileUtils,
        UserProfileImageRepository userProfileImageRepository,
        AdminRepository adminRepository,
        AdminProfileImageRepository adminProfileImageRepository,
        AdminImageRepository adminImageRepository,
        EntityManager entityManager
    ) {
        this.userRepository = userRepository;
        this.s3FileUtils = s3FileUtils;
        this.userProfileImageRepository = userProfileImageRepository;
        this.adminRepository = adminRepository;
        this.adminProfileImageRepository = adminProfileImageRepository;
        this.adminImageRepository = adminImageRepository;
        this.entityManager = entityManager;
    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext){
        log.info("GuestJob.execute()");

        if(jobExecutionContext.getTrigger().getKey().getName().equals(GUEST_TRIGGER)) {
            log.info("GuestJob.execute() - guestTrigger");

            List<User> userList = userRepository.findAllByRoleAndCreatedAtBefore(GuestType.USER_GUEST.getType(), LocalDateTime.now().minusHours(48));

            // 유저 이미지 S3, DB 삭제
            for(User user : userList) {
                if(user.getUserProfileImage() != null) {
                    s3FileUtils.deleteFile(user.getUserProfileImage().getFilePath());
                    userProfileImageRepository.delete(user.getUserProfileImage());
                    entityManager.flush();
                }
            }

            // 유저 삭제
            userRepository.deleteAll(userList);


            // 어드민 유저 조회
            List<Admin> adminList = adminRepository.findAllByRoleAndCreatedAtBefore(GuestType.ADMIN_GUEST.getType(), LocalDateTime.now().minusHours(48));

            for (Admin admin : adminList) {
                // 어드민 이미지 S3, DB 삭제
                if(admin.getAdminProfileImage() != null) {
                    s3FileUtils.deleteFile(admin.getAdminProfileImage().getFilePath());
                    adminProfileImageRepository.delete(admin.getAdminProfileImage());
                    entityManager.flush();
                }
                if(!admin.getAdminImageList().isEmpty()) {
                    for(int i = 0; i < admin.getAdminImageList().size(); i++) {
                        s3FileUtils.deleteFile(admin.getAdminImageList().get(i).getFilePath());
                        adminImageRepository.delete(admin.getAdminImageList().get(i));
                        entityManager.flush();
                    }
                }
            }
            // 어드민 이미지 S3, DB 삭제
            adminRepository.deleteAll(adminList);

        }

    }
}
