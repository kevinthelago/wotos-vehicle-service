package com.wotos.wotosvehicleservice.web;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Standard error envelope returned by {@link GlobalExceptionHandler} for every
 * 4xx/5xx across the service (Tankopedia, armor, and model endpoints). Kept flat and
 * stable so the edge service can surface it uniformly.
 *
 * @param timestamp when the error was produced
 * @param status    HTTP status code
 * @param error     HTTP reason phrase (e.g. "Not Found")
 * @param message   human-readable, non-sensitive detail
 * @param path      request path
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {

    public static ApiError of(org.springframework.http.HttpStatus status, String message, String path) {
        return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, path);
    }
}
