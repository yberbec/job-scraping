package org.job.scraping.service;

import org.job.scraping.model.Job;

import java.io.IOException;
import java.util.List;

public interface ScraperService {
    List<Job> scrapePage(String position, Integer experience, String tech, String country, Integer page) throws IOException;
}
