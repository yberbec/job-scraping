package org.job.scraping.common;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class JobScheduler {

    private final RestTemplate restTemplate = new RestTemplate();

//    @Scheduled(fixedRate = 30000)
    public void triggerJobExport() {
        try {
            URI uri = UriComponentsBuilder
                    .fromHttpUrl("http://localhost:8080/api/jobs")
                    .queryParam("position", "Java+Dev")
                    .queryParam("experience", "3")
                    .queryParam("tech","Spring")
                    .queryParam("country", "Canada")
                    .build()
                    .encode()
                    .toUri();
            restTemplate.getForObject(uri, Void.class);
            System.out.println("Triggered job export at: " + java.time.LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("Error triggering job export: " + e.getMessage());
        }
    }
}
