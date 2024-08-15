package com.example.ref.config.quartz;

import com.example.ref.entity.Visitor;
import com.example.ref.repository.VisitorRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
@Slf4j
public class VisitorJob implements Job {

    private static final String VISITOR_TRIGGER = "visitorTrigger";

    private final VisitorRepository visitorRepository;

    private final RedisTemplate<String, Object> redisTemplate;


    public VisitorJob(VisitorRepository visitorRepository,
        RedisTemplate<String, Object> redisTemplate) {
        this.visitorRepository = visitorRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("VisitorJob.execute()");
        if(jobExecutionContext.getTrigger().getKey().getName().equals(VISITOR_TRIGGER)) {
            Set<String> keys = redisTemplate.keys("ip:*");
            if(keys == null) {
                return;
            }
            for(String key : keys) {
                String[] split = key.split("\\|");
                String ip = split[0].substring(3);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate visitDate = LocalDate.parse(split[1].substring(6), formatter);
                String userAgent = (String) redisTemplate.opsForValue().get(key);
                if(!visitorRepository.existsByIpAndVisitDate(ip, LocalDate.now())) {
                    visitorRepository.save(Visitor.builder()
                        .ip(ip)
                        .userAgent(userAgent)
                        .visitDate(visitDate)
                        .build());
                }
                redisTemplate.delete(key);
            }

        }
    }
}
