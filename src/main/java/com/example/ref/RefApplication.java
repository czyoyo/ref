package com.example.ref;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy // AOP 사용을 위한 어노테이션
public class RefApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefApplication.class, args);
    }

}
