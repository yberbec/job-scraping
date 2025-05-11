package org.job.scraping.service;

import org.job.scraping.model.Job;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndeedScraperService implements ScraperService {
    private static final String INDEED_URL =
      "https://www.indeed.com/jobs?q=%s&l=&start=%d";

    @Override
    public List<Job> scrapePage(String position, Integer experience, String tech, String country, Integer page) throws IOException {
        String query = URLEncoder.encode(position + "+" + tech + "+" + experience + "+years", StandardCharsets.UTF_8);
        String url = String.format(INDEED_URL, query, 0);
        Document doc = Jsoup.connect(url).get();

        return doc.select("div.jobsearch-SerpJobCard").stream().map(elem -> {
            String title = elem.select("h2.title").text();
            String company = elem.select("span.company").text();
            String location = elem.select("div.location").text();
            String summary = elem.select("div.summary").text();
            String date = elem.select("span.date").text();
            String link = "https://www.indeed.com" + elem.select("a").attr("href");
            return new Job(title, company, location, summary, date, link);
        }).collect(Collectors.toList());
    }
}
