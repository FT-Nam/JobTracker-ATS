package com.jobtracker.jobtracker_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JobtrackerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobtrackerAppApplication.class, args);
    }
}
