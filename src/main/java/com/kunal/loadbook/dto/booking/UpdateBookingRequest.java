package com.kunal.loadbook.dto.booking;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request DTO for updating a booking")
public class UpdateBookingRequest {

    @Schema(description = "Proposed rate for the booking", example = "25000.50")
    @Positive(message = "Proposed rate must be positive")
    private Double proposedRate;

    @Schema(description = "Additional comments or notes", example = "Can deliver within 3 days")
    private String comment;

    // Default constructor
    public UpdateBookingRequest() {
    }

    // Constructor
    public UpdateBookingRequest(Double proposedRate, String comment) {
        this.proposedRate = proposedRate;
        this.comment = comment;
    }

    // Getters and Setters
    public Double getProposedRate() {
        return proposedRate;
    }

    public void setProposedRate(Double proposedRate) {
        this.proposedRate = proposedRate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
