package com.wotos.wotosvehicleservice.web;

import com.wotos.wotosvehicleservice.armor.ArmorSerializationException;
import com.wotos.wotosvehicleservice.storage.AssetTooComplexException;
import com.wotos.wotosvehicleservice.storage.AssetTooLargeException;
import com.wotos.wotosvehicleservice.storage.MalformedGlbException;
import com.wotos.wotosvehicleservice.storage.StorageException;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.core.exception.SdkException;

import java.util.stream.Collectors;

/**
 * Translates exceptions across all controllers into the standard {@link ApiError}
 * envelope: not-found (404), bean/param validation (400), upstream WoT Feign errors
 * (502), object-storage errors (S3 502, oversize 413, too-complex 422, malformed 400),
 * and a 500 fallback. Keeps controllers thin — they throw, this maps.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        String message = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message, req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInvalidBody(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message.isBlank() ? "validation failed" : message, req);
    }

    @ExceptionHandler(AssetTooLargeException.class)
    public ResponseEntity<ApiError> handleTooLarge(AssetTooLargeException ex, HttpServletRequest req) {
        return build(HttpStatus.PAYLOAD_TOO_LARGE, ex.getMessage(), req);
    }

    @ExceptionHandler(AssetTooComplexException.class)
    public ResponseEntity<ApiError> handleTooComplex(AssetTooComplexException ex, HttpServletRequest req) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
    }

    @ExceptionHandler(MalformedGlbException.class)
    public ResponseEntity<ApiError> handleMalformedGlb(MalformedGlbException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiError> handleFeign(FeignException ex, HttpServletRequest req) {
        log.warn("upstream WoT API error: {}", ex.getMessage());
        return build(HttpStatus.BAD_GATEWAY, "upstream WoT API error", req);
    }

    @ExceptionHandler({StorageException.class, SdkException.class})
    public ResponseEntity<ApiError> handleStorage(RuntimeException ex, HttpServletRequest req) {
        log.warn("object-storage error: {}", ex.getMessage());
        return build(HttpStatus.BAD_GATEWAY, "object storage error", req);
    }

    @ExceptionHandler(ArmorSerializationException.class)
    public ResponseEntity<ApiError> handleArmorSerialization(ArmorSerializationException ex, HttpServletRequest req) {
        log.error("armor (de)serialization failure", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "armor profile processing error", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "unexpected error", req);
    }

    private static ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest req) {
        return ResponseEntity.status(status).body(ApiError.of(status, message, req.getRequestURI()));
    }
}
