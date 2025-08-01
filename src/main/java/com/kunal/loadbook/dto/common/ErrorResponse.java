package com.kunal.loadbook.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard error response")
public class ErrorResponse {

    @Schema(description = "Error message")
    private String message;

    @Schema(description = "HTTP status code")
    private int status;

    @Schema(description = "Error code")
    private String error;

    @Schema(description = "Request path where error occurred")
    private String path;

    @Schema(description = "Timestamp when error occurred")
    private LocalDateTime timestamp;

    // Default constructor
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor
    public ErrorResponse(String message, int status, String error, String path) {
        this.message = message;
        this.status = status;
        this.error = error;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
