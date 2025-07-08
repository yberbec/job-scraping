package org.job.scraping.exception;

public class JobParsingException extends RuntimeException {
    public JobParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
