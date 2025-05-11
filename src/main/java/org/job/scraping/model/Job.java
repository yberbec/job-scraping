package org.job.scraping.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Job {
    private String title;
    private String company;
    private String location;
    private String summary;
    private String postedDate;
    private String url;
}
