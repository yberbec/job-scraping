package org.job.scraping.controller;

import org.job.scraping.exception.InvalidJobRequestException;
import org.job.scraping.model.Job;
import org.job.scraping.service.ExcelExportService;
import org.job.scraping.service.GoogleJobsRestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobControllerTest {

    private ExcelExportService excelMock;
    private GoogleJobsRestClient googleJobsRestClientMock;
    private JobController jobController;

    @BeforeEach
    void setup() {
        excelMock = mock(ExcelExportService.class);
        googleJobsRestClientMock = mock(GoogleJobsRestClient.class);
        jobController = new JobController(excelMock, googleJobsRestClientMock);
    }

    @Test
    void getGoogleJobs_validRequest_returnsJobs() throws IOException, URISyntaxException {

        String jobTitle = "developer";
        String country = "canada";
        int pages = 2;
        String postedAt = "days";

        Set<Job> mockJobs = new HashSet<>();
        mockJobs.add(new Job());
        when(googleJobsRestClientMock.getGoogleJobListing(jobTitle, country, pages, postedAt))
                .thenReturn(mockJobs);


        Set<Job> result = jobController.getGoogleJobs(jobTitle, country, pages, postedAt);


        assertNotNull(result);
        assertEquals(1, result.size());
        verify(googleJobsRestClientMock, times(1))
                .getGoogleJobListing(jobTitle, country, pages, postedAt);
        verify(excelMock, times(1)).saveJobsToFile(mockJobs);
    }

    @Test
    void getGoogleJobs_invalidPages_throwsException() {

        String jobTitle = "developer";
        String country = "ca";
        int pages = 0;
        String postedAt = "days";


        InvalidJobRequestException ex = assertThrows(InvalidJobRequestException.class,
                () -> jobController.getGoogleJobs(jobTitle, country, pages, postedAt));

        assertEquals("Pages must be greater than 0.", ex.getMessage());


        verifyNoInteractions(googleJobsRestClientMock);
        verifyNoInteractions(excelMock);
    }
}