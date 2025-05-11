package org.job.scraping.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.job.scraping.model.Job;
import org.job.scraping.service.ExcelExportService;
import org.job.scraping.service.IndeedScraperService;
import org.job.scraping.service.JsonExportService;
import org.job.scraping.service.LinkedInScraperService;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final IndeedScraperService indeed;
    private final LinkedInScraperService linkedin;
    private final ExcelExportService excel;
    private final JsonExportService jsonExportService;

    public JobController(IndeedScraperService indeed,
                         LinkedInScraperService linkedin,
                         ExcelExportService excel,
                         JsonExportService json) {
        this.indeed = indeed;
        this.linkedin = linkedin;
        this.excel = excel;
        this.jsonExportService = json;
    }

    @GetMapping
    public ResponseEntity<?> getJobs(
            @RequestParam(value = "experience", required = false) Integer experience,
            @RequestParam("position") String position,
            @RequestParam("tech") String tech,
            @RequestParam("country") String country,
            @RequestParam(value = "items", required = false) Integer items,
            HttpServletResponse resp) throws IOException, InterruptedException {

        List<Job> all = new ArrayList<>();
//        all.addAll(indeed.scrape(position, experience, tech));
        all.addAll(linkedin.scrape(position, (experience != null) ? experience : 0, tech, country, items));
//        excel.exportJobs(all, resp);
        jsonExportService.exportJobsToJson(all, resp);
        return ResponseEntity.ok().body("Jobs fetched successfully");

    }


}
