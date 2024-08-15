package com.example.ref.config.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.springframework.context.annotation.Bean;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

@Configuration
@Slf4j
public class QuartzConfig {

    // 회원탈퇴 Job 실행 주기 설정
    @Bean
    public CronTriggerFactoryBean leaveTrigger(JobDetail leaveJobDetail) {
        // 매일 0시 0분 0초
        return createCronTrigger(leaveJobDetail, "0 0 0 * * ? *");
        // 10초마다 테스트
//        return createCronTrigger(leaveJobDetail, "0/10 * * * * ? *"); // 매일 0시 0분 0초
    }

    // 방문 기록 Job 실행 주기 설정
    @Bean
    public CronTriggerFactoryBean visitorTrigger(JobDetail visitorJobDetail) {
//        return createCronTrigger(visitorJobDetail, "0 0 0/1 * * ? *");
        // 3시간 마다 테스트
//        return createCronTrigger(visitorJobDetail, "0 0 0/3 * * ? *");
        // 6시간 마다 테스트
        return createCronTrigger(visitorJobDetail, "0 0 0/6 * * ? *");
        // 10초 마다 테스트
//        return createCronTrigger(visitorJobDetail, "0/10 * * * * ? *");
    }

    @Bean
    public CronTriggerFactoryBean guestTrigger(JobDetail guestJobDetail) {
        // 매일 0시 0분 0초
        return createCronTrigger(guestJobDetail, "0 0 0 * * ? *");
        // 10초마다 테스트
//        return createCronTrigger(guestJobDetail, "0/10 * * * * ? *"); // 매일 0시 0분 0초
    }



    @Bean
    public JobDetailFactoryBean leaveJobDetail() {
        return createJobDetail(LeaveJob.class);
    }
    @Bean
    public JobDetailFactoryBean visitorJobDetail() {
        return createJobDetail(VisitorJob.class);
    }
    @Bean
    public JobDetailFactoryBean guestJobDetail() {
        return createJobDetail(GuestJob.class);
    }



    private JobDetailFactoryBean createJobDetail(Class<? extends Job> jobClass) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        factoryBean.setDurability(true);
        return factoryBean;
    }

    private CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        return factoryBean;
    }

}
