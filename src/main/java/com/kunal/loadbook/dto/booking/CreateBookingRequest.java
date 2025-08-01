package com.kunal.loadbook.dto.booking;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

@Schema(description = "Request DTO for creating a new booking")
public class CreateBookingRequest {

    @Schema(description = "Load identifier for which booking is being made")
    @NotNull(message = "Load ID is required")
    private UUID loadId;

    @Schema(description = "Transporter identifier", example = "TRANSPORTER_001")
    @NotBlank(message = "Transporter ID is required")
    private String transporterId;

    @Schema(description = "Proposed rate for the booking", example = "25000.50")
    @NotNull(message = "Proposed rate is required")
    @Positive(message = "Proposed rate must be positive")
    private Double proposedRate;

    @Schema(description = "Additional comments or notes", example = "Can deliver within 3 days")
    private String comment;

    // Default constructor
    public CreateBookingRequest() {
    }

    // Constructor
    public CreateBookingRequest(UUID loadId, String transporterId, Double proposedRate, String comment) {
        this.loadId = loadId;
        this.transporterId = transporterId;
        this.proposedRate = proposedRate;
        this.comment = comment;
    }

    // Getters and Setters
    public UUID getLoadId() {
        return loadId;
    }

    public void setLoadId(UUID loadId) {
        this.loadId = loadId;
    }

    public String getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(String transporterId) {
        this.transporterId = transporterId;
    }

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
