package org.job.scraping.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.job.scraping.api.GoogleJobsApi;
import org.job.scraping.exception.ExternalApiException;
import org.job.scraping.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoogleJobsRestClient {

    private static final Logger log = LoggerFactory.getLogger(GoogleJobsRestClient.class);
    private static final String PAGINATION = "serpapi_pagination";
    private static final String NEXT_PAGE_TOKEN = "next_page_token";
    String nextPageToken = null;
    @Value("${serpapi.api.key}")
    private String apiKey;

    private static final String FILTER_TIME = "qdr:d";
    private static final String JOBS_RESULTS = "jobs_results";
    @Autowired
    private GoogleJobsApi googleJobsApi;

    public Set<Job> getGoogleJobListing(String jobTitle, String country, int pages, String postedAt) throws IOException, URISyntaxException {
        Set<Job> allJobs = new HashSet<>();
        ObjectMapper mapper = new ObjectMapper();
        String nextPageToken = null;
        int currentPage = 0;
        String queryUrl = jobTitle + country;
        do {
            try {
                ResponseEntity<String> response = googleJobsApi.getJobs(queryUrl, FILTER_TIME, apiKey, nextPageToken);
                nextPageToken = processJobResponse(response, mapper, allJobs);
                currentPage++;
            } catch (FeignException.TooManyRequests ex) {
                throw new ExternalApiException("SerpApi quota exceeded: " + ex.contentUTF8(), ex);
            } catch (FeignException ex) {
                throw new ExternalApiException("SerpApi error: " + ex.status() + " - " + ex.contentUTF8(), ex);
            }
        } while (currentPage < pages && nextPageToken != null);

        return allJobs.stream().filter(job -> Optional.ofNullable(job.getDetectedExtensions().getPostedAt()).orElse(Strings.EMPTY).contains(postedAt)).collect(Collectors.toSet());
    }

    public String processJobResponse(ResponseEntity<String> response, ObjectMapper mapper, Set<Job> allJobs) throws IOException {
        String nextPageToken = "";
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JsonNode root = mapper.readTree(response.getBody());

            JsonNode jobsResults = root.path(JOBS_RESULTS);
            if (jobsResults.isArray()) {
                for (JsonNode job : jobsResults) {
                    Job jobObj = mapper.treeToValue(job, Job.class);
                    allJobs.add(jobObj);
                }
            }

            JsonNode paginationNode = root.path(PAGINATION);
            if (paginationNode.has(NEXT_PAGE_TOKEN)) {
                nextPageToken = paginationNode.path(NEXT_PAGE_TOKEN).asText(null);
            } else {
                nextPageToken = null;
            }

        } else {
            throw new IOException("Failed to fetch Google Job Listing. Status code: " + response.getStatusCode());
        }

        return nextPageToken;
    }
}