package org.job.scraping.util;

public enum ExportConstants {
    USER_DIR(System.getProperty("user.dir")),
    APP_NAME("job-scraping-app"),
    OUTPUT_DIR("output"),
    FILE_NAME("jobs.xlsx");

    private final String value;

    ExportConstants(String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }
}
