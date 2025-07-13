package org.job.scraping.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Request;
import org.job.scraping.api.GoogleJobsApi;
import org.job.scraping.exception.ExternalApiException;
import org.job.scraping.model.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GoogleJobsRestClientTest {

    private GoogleJobsApi googleJobsApiMock;
    private GoogleJobsRestClient client;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() throws IllegalAccessException, NoSuchFieldException {
        googleJobsApiMock = mock(GoogleJobsApi.class);
        client = new GoogleJobsRestClient();
        Field field = GoogleJobsRestClient.class.getDeclaredField("googleJobsApi");
        field.setAccessible(true);
        field.set(client, googleJobsApiMock);

        Field keyField = GoogleJobsRestClient.class.getDeclaredField("apiKey");
        keyField.setAccessible(true);
        keyField.set(client, "fake-api-key");
        mapper = new ObjectMapper();
    }

    @Test
    void processJobResponse_successfulResponse_returnsNextPageTokenAndAddsJobs() throws IOException {
        String jsonResponse = """
                {
                  "jobs_results": [
                    {"title":"Java Developer","detectedExtensions":{"postedAt":"days"}},
                    {"title":"Backend Engineer","detectedExtensions":{"postedAt":"hours"}}
                  ],
                  "serpapi_pagination": {
                    "next_page_token": "token123"
                  }
                }
                """;

        ResponseEntity<String> response = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        Set<Job> jobs = new HashSet<>();

        String nextPageToken = client.processJobResponse(response, mapper, jobs);

        assertEquals("token123", nextPageToken);
        assertEquals(1, jobs.size());
        assertTrue(jobs.stream().anyMatch(job -> "Java Developer".equals(job.getTitle())));
    }

    @Test
    void processJobResponse_noNextPageToken_returnsNull() throws IOException {
        String jsonResponse = """
                {
                  "jobs_results": [],
                  "serpapi_pagination": {}
                }
                """;

        ResponseEntity<String> response = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        Set<Job> jobs = new HashSet<>();

        String nextPageToken = client.processJobResponse(response, mapper, jobs);

        assertNull(nextPageToken);
        assertTrue(jobs.isEmpty());
    }

    @Test
    void processJobResponse_non2xxStatus_throwsIOException() {
        ResponseEntity<String> response = new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        Set<Job> jobs = new HashSet<>();

        IOException exception = assertThrows(IOException.class,
                () -> client.processJobResponse(response, mapper, jobs));

        assertTrue(exception.getMessage().contains("Failed to fetch Google Job Listing"));
    }

    @Test
    void getGoogleJobListing_success_callsApiMultiplePagesAndFiltersByPostedAt() throws IOException, URISyntaxException {

        String page1 = """
                {
                  "jobs_results": [
                    {"title":"Job1","detectedExtensions":{"postedAt":"days"}},
                    {"title":"Job2","detectedExtensions":{"postedAt":"hours"}}
                  ],
                  "serpapi_pagination": {
                    "next_page_token": "token1"
                  }
                }
                """;

        String page2 = """
                {
                  "jobs_results": [
                    {"title":"Job3","detectedExtensions":{"postedAt":"days"}}
                  ],
                  "serpapi_pagination": {}
                }
                """;

        when(googleJobsApiMock.getJobs(anyString(), anyString(), anyString(), ArgumentMatchers.isNull()))
                .thenReturn(new ResponseEntity<>(page1, HttpStatus.OK));

        when(googleJobsApiMock.getJobs(anyString(), anyString(), anyString(), eq("token1")))
                .thenReturn(new ResponseEntity<>(page2, HttpStatus.OK));

        Set<Job> results = client.getGoogleJobListing("java", "ca", 2, "days");

        assertEquals(0, results.size());
        assertTrue(results.stream().allMatch(j -> j.getDetectedExtensions().getPostedAt().contains("days")));

        verify(googleJobsApiMock, times(1)).getJobs(anyString(), anyString(), anyString(), isNull());
        verify(googleJobsApiMock, times(1)).getJobs(anyString(), anyString(), anyString(), eq("token1"));
    }

    @Test
    void getGoogleJobListing_apiTooManyRequests_throwsExternalApiException() throws URISyntaxException {
        Request request = Request.create(Request.HttpMethod.GET, "/test", Map.of(), null, Charset.defaultCharset(), null);
        when(googleJobsApiMock.getJobs(anyString(), anyString(), anyString(), any()))
                .thenThrow(new FeignException.TooManyRequests("Too many requests", request , null, null));

        ExternalApiException ex = assertThrows(ExternalApiException.class,
                () -> client.getGoogleJobListing("java", "ca", 1, "days"));

        assertTrue(ex.getMessage().contains("SerpApi quota exceeded"));
    }

    @Test
    void getGoogleJobListing_apiOtherFeignException_throwsExternalApiException() throws URISyntaxException {
        Request request = Request.create(Request.HttpMethod.GET, "/test", Map.of(), null, Charset.defaultCharset(), null);
        when(googleJobsApiMock.getJobs(anyString(), anyString(), anyString(), any()))
                .thenThrow(new FeignException.BadRequest("Bad request", request, null, null));

        ExternalApiException ex = assertThrows(ExternalApiException.class,
                () -> client.getGoogleJobListing("java", "ca", 1, "days"));

        assertTrue(ex.getMessage().contains("SerpApi error"));
    }
}