package org;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"org.job"})
@EnableScheduling
@EnableFeignClients(basePackages = "org.job.scraping")
public class JobScrapApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobScrapApplication.class, args);
    }
}
