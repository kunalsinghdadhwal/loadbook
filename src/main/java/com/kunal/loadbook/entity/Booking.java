package com.kunal.loadbook.entity;

import com.kunal.loadbook.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "load_id", nullable = false, foreignKey = @ForeignKey(name = "fk_booking_load"))
    @NotNull(message = "Load is required")
    private Load load;
    
    @NotBlank(message = "Transporter ID is required")
    @Column(name = "transporter_id", nullable = false)
    private String transporterId;
    
    @NotNull(message = "Proposed rate is required")
    @Positive(message = "Proposed rate must be positive")
    @Column(name = "proposed_rate", nullable = false)
    private Double proposedRate;
    
    @Column(length = 1000)
    private String comment;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Default constructor
    public Booking() {}
    
    // Constructor
    public Booking(Load load, String transporterId, Double proposedRate, String comment) {
        this.load = load;
        this.transporterId = transporterId;
        this.proposedRate = proposedRate;
        this.comment = comment;
        this.status = BookingStatus.PENDING;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Load getLoad() {
        return load;
    }
    
    public void setLoad(Load load) {
        this.load = load;
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
    
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", transporterId='" + transporterId + '\'' +
                ", proposedRate=" + proposedRate +
                ", status=" + status +
                ", requestedAt=" + requestedAt +
                '}';
    }
}
