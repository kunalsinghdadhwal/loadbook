package com.kunal.loadbook.entity;

import com.kunal.loadbook.enums.LoadStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "loads")
public class Load {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Shipper ID is required")
    @Column(name = "shipper_id", nullable = false)
    private String shipperId;

    @Embedded
    private Facility facility;

    @NotBlank(message = "Product type is required")
    @Column(name = "product_type", nullable = false)
    private String productType;

    @NotBlank(message = "Truck type is required")
    @Column(name = "truck_type", nullable = false)
    private String truckType;

    @NotNull(message = "Number of trucks is required")
    @Positive(message = "Number of trucks must be positive")
    @Column(name = "no_of_trucks", nullable = false)
    private Integer noOfTrucks;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    @Column(nullable = false)
    private Double weight;

    @Column(length = 1000)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoadStatus status = LoadStatus.POSTED;

    @CreationTimestamp
    @Column(name = "date_posted", nullable = false, updatable = false)
    private LocalDateTime datePosted;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    // Default constructor
    public Load() {
    }

    // Constructor
    public Load(String shipperId, Facility facility, String productType, String truckType,
            Integer noOfTrucks, Double weight, String comment) {
        this.shipperId = shipperId;
        this.facility = facility;
        this.productType = productType;
        this.truckType = truckType;
        this.noOfTrucks = noOfTrucks;
        this.weight = weight;
        this.comment = comment;
        this.status = LoadStatus.POSTED;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getShipperId() {
        return shipperId;
    }

    public void setShipperId(String shipperId) {
        this.shipperId = shipperId;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public Integer getNoOfTrucks() {
        return noOfTrucks;
    }

    public void setNoOfTrucks(Integer noOfTrucks) {
        this.noOfTrucks = noOfTrucks;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LoadStatus getStatus() {
        return status;
    }

    public void setStatus(LoadStatus status) {
        this.status = status;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    // Helper methods
    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setLoad(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setLoad(null);
    }

    @Override
    public String toString() {
        return "Load{" +
                "id=" + id +
                ", shipperId='" + shipperId + '\'' +
                ", productType='" + productType + '\'' +
                ", truckType='" + truckType + '\'' +
                ", status=" + status +
                ", datePosted=" + datePosted +
                '}';
    }
}
