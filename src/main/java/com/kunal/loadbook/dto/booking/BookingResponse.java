package com.kunal.loadbook.dto.booking;

import com.kunal.loadbook.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response DTO for booking information")
public class BookingResponse {

    @Schema(description = "Unique identifier of the booking")
    private UUID id;

    @Schema(description = "Load identifier for which booking was made")
    private UUID loadId;

    @Schema(description = "Transporter identifier")
    private String transporterId;

    @Schema(description = "Proposed rate for the booking")
    private Double proposedRate;

    @Schema(description = "Additional comments or notes")
    private String comment;

    @Schema(description = "Current status of the booking")
    private BookingStatus status;

    @Schema(description = "Date and time when booking was requested")
    private LocalDateTime requestedAt;

    @Schema(description = "Last updated timestamp")
    private LocalDateTime updatedAt;

    // Default constructor
    public BookingResponse() {
    }

    // Constructor
    public BookingResponse(UUID id, UUID loadId, String transporterId, Double proposedRate,
            String comment, BookingStatus status, LocalDateTime requestedAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.loadId = loadId;
        this.transporterId = transporterId;
        this.proposedRate = proposedRate;
        this.comment = comment;
        this.status = status;
        this.requestedAt = requestedAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
