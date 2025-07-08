package org.job.scraping.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URISyntaxException;

@FeignClient(name = "serpClient", url = "https://serpapi.com")
public interface GoogleJobsApi {

    @GetMapping("/search.json?engine=google_jobs")
    ResponseEntity<String> getJobs(@RequestParam("q") String query,
                                   @RequestParam("tbs") String timeFilter,
                                   @RequestParam("api_key") String apiKey,
                                   @RequestParam(value = "next_page_token", required = false) String startToken) throws URISyntaxException;
}
