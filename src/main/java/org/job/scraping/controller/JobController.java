package org.job.scraping.controller;

import lombok.extern.slf4j.Slf4j;
import org.job.scraping.model.Job;
import org.job.scraping.service.ExcelExportService;
import org.job.scraping.service.GoogleJobsRestClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Set;

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
    public Set<Job> getGoogleJobs(@RequestParam("jobTitle") String jobTitle, @RequestParam("country") String country,
                                   @RequestParam("pages") int pages,
                                   @RequestParam("posted_at") String postedAt) throws IOException {
        Set<Job> all = googleJobsRestClient.getGoogleJobListing(jobTitle, country, pages, postedAt); // postedAt : write(hours/days/minutes) to get jobs posted hours/days/minutes ago
        excel.saveJobsToFile(all);
        return all;
    }

}
