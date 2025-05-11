package org.job.scraping.service;

import lombok.extern.slf4j.Slf4j;
import org.job.scraping.model.Job;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Slf4j
public class LinkedInScraperService implements ScraperService {

    @Override
    public List<Job> scrapePage(String position, Integer experience, String tech, String country, Integer page) throws IOException {

        String url = "https://www.linkedin.com/jobs/search/"
          + "?f_E=" + experience
          + "&keywords=" + URLEncoder.encode(position + " " + tech, UTF_8)
          + "&location=" + URLEncoder.encode(country,UTF_8)
                + "&start=" + page + "&sortBy=DD";
        log.info("Scraping " + url + "...");
        log.info("URL ENCODE : " + URLEncoder.encode(position + " " + tech, UTF_8));
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .get();
        Elements cards = doc.select(".jobs-search__results-list li");
        log.info("Found " + cards.size() + " cards");
        return cards.stream().map(e -> {
            String title = e.select("h3").text();
            String company = e.select("h4").text();
            String loc = e.select(".job-search-card__location").text();
            String postedDate = e.select(".job-search-card__listdate").text();
            log.info("date posted: " + postedDate);

            String link = e.select("a").attr("href");
            return new Job(title, company, loc, "", postedDate, link);
        }).collect(Collectors.toList());
    }

    public List<Job> scrape(String position, int experience, String tech, String country, int items) throws IOException, InterruptedException {
        List<Job> allJobs = new ArrayList<>();
        int start = 0;
        int pageSize = 25;
        while (true) {
            System.out.println("Scraping page with start=" + start);

            List<Job> jobs = scrapePage(position, experience, tech, country, start);

            if (jobs.isEmpty()) {
                System.out.println("No more jobs found at start=" + start);
                break;
            }

            allJobs.addAll(jobs);


            if (allJobs.size() >= items) {
                System.out.println("Reached max items: " + items);
                break;
            }

            start += pageSize;
            Thread.sleep(1000);
        }

        return allJobs.subList(0, Math.min(allJobs.size(), items));
    }

}
