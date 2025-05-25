package org.job.scraping.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.job.scraping.model.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GoogleJobsRestClient {

    private final RestTemplate restTemplate;
    private final String serpApiBaseUrl = "https://serpapi.com";
    String nextPageToken = null;
    @Value("${serpapi.api.key}")
    private String apiKey;
    @Value("${server.search.engine}")
    private String searchEngine;

    public GoogleJobsRestClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<Job> getGoogleJobListing(String jobTitle, String country, int pages) throws IOException {
        List<Job> allJobs = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        String nextPageToken = null;
        int currentPage = 0;
        do {
            String queryUrl = serpApiBaseUrl + searchEngine + "&q=\"" + jobTitle + "\"+\"" + country + "\"" +
                    "&api_key=" + apiKey;

            log.info("queryUrl: " + queryUrl);

            if (nextPageToken != null) {
                queryUrl += "&next_page_token=" + nextPageToken;
            }

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(queryUrl, HttpMethod.GET, httpEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = mapper.readTree(response.getBody());

                System.out.println("Page response:");
                System.out.println(root.toPrettyString());

                JsonNode jobsResults = root.path("jobs_results");
                if (jobsResults.isArray()) {
                    for (JsonNode job : jobsResults) {
                        Job jobObj = mapper.treeToValue(job, Job.class);
                        allJobs.add(jobObj);
                    }
                }

                JsonNode paginationNode = root.path("serpapi_pagination");
                if (paginationNode.has("next_page_token")) {
                    nextPageToken = paginationNode.path("next_page_token").asText(null);
                } else {
                    nextPageToken = null;
                }

            } else {
                throw new IOException("Failed to fetch Google Job Listing. Status code: " + response.getStatusCode());
            }
            currentPage++;
        } while (currentPage < pages);

        return allJobs;
    }

}