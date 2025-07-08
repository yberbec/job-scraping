package org.job.scraping.exception;

public class InvalidJobRequestException extends RuntimeException {
    public InvalidJobRequestException(String message) {
        super(message);
    }
}
