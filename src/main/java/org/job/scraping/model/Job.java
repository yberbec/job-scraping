package org.job.scraping.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {
    @JsonProperty("title")
    private String title;
    @JsonProperty("company_name")
    private String company;
    @JsonProperty("location")
    private String location;
    //    private String summary;
    @JsonProperty("detected_extensions")
    private DetectedExtensions detectedExtensions;
    @JsonProperty("apply_options")
    private List<ApplyOptions> applyOptions;
    @JsonProperty("job_id")
    private String jobId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Job)) return false;
        Job job = (Job) o;
        return Objects.equals(jobId, job.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId);
    }

    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DetectedExtensions {
        @JsonProperty("posted_at")
        private String postedAt;
    }

    @Setter
    @Getter
    public static class ApplyOptions {
        @JsonProperty("title")
        private String title;
        @JsonProperty("link")
        private String applyLink;
    }
}
