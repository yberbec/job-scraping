package org.job.scraping.controller;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.job.scraping.model.Job;
import org.job.scraping.service.*;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RestController
@RequestMapping("/api/jobs")
@Slf4j
public class JobController {

    private final ExcelExportService excel;
    private final GoogleJobsRestClient googleJobsRestClient;

    public JobController(ExcelExportService excel,
                         GoogleJobsRestClient googleJobsRestClient) {
        this.excel = excel;
        this.googleJobsRestClient = googleJobsRestClient;
    }

    @GetMapping("/google-jobs")
    public List<Job> getGoogleJobs(@RequestParam("jobTitle") String jobTitle, @RequestParam("country") String country,
                                   @RequestParam("pages") int pages) throws IOException {
        List<Job> all = googleJobsRestClient.getGoogleJobListing(jobTitle, country, pages);
        excel.saveJobsToFile(all);
        return all;
    }

}
