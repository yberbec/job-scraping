package org.job.scraping.controller;

import feign.FeignException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.job.scraping.exception.ExportException;
import org.job.scraping.exception.ExternalApiException;
import org.job.scraping.exception.InvalidJobRequestException;
import org.job.scraping.exception.JobParsingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Counter errorCounter;

    public GlobalExceptionHandler(MeterRegistry meterRegistry) {
        this.errorCounter = meterRegistry.counter("app_errors_total");
    }

    @ExceptionHandler(InvalidJobRequestException.class)
    public ResponseEntity<String> handleInvalidJobRequest(InvalidJobRequestException ex) {
        errorCounter.increment();
        return ResponseEntity.badRequest().body("Invalid request: " + ex.getMessage());
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<String> handleExternalApi(ExternalApiException ex) {
        errorCounter.increment();
        if (ex.getMessage().contains("quota") || ex.getCause() instanceof FeignException.TooManyRequests) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Error: SerpApi quota exceeded. Please try again later.");
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("External API failed: " + ex.getMessage());
    }


    @ExceptionHandler(JobParsingException.class)
    public ResponseEntity<String> handleJobParsing(JobParsingException ex) {
        errorCounter.increment();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing job data.");
    }

    @ExceptionHandler(ExportException.class)
    public ResponseEntity<String> handleExport(ExportException ex) {
        errorCounter.increment();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage() + " Error exporting jobs to file.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        errorCounter.increment();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
    }
}
