package com.jobtracker.jobtracker_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableScheduling
public class JobtrackerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobtrackerAppApplication.class, args);
    }
}
