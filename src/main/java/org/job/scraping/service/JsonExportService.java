package org.job.scraping.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.job.scraping.model.Job;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class JsonExportService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void exportJobsToJson(List<Job> jobs, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=jobs.json");
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(response.getOutputStream(), jobs);
    }
}
