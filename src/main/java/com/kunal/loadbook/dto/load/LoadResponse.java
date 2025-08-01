package com.kunal.loadbook.dto.load;

import com.kunal.loadbook.enums.LoadStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response DTO for load information")
public class LoadResponse {
    
    @Schema(description = "Unique identifier of the load")
    private UUID id;
    
    @Schema(description = "Shipper identifier")
    private String shipperId;
    
    @Schema(description = "Facility information")
    private FacilityDto facility;
    
    @Schema(description = "Type of product being shipped")
    private String productType;
    
    @Schema(description = "Type of truck required")
    private String truckType;
    
    @Schema(description = "Number of trucks required")
    private Integer noOfTrucks;
    
    @Schema(description = "Weight of the load in tons")
    private Double weight;
    
    @Schema(description = "Additional comments or notes")
    private String comment;
    
    @Schema(description = "Current status of the load")
    private LoadStatus status;
    
    @Schema(description = "Date when the load was posted")
    private LocalDateTime datePosted;
    
    @Schema(description = "Last updated timestamp")
    private LocalDateTime updatedAt;
    
    // Default constructor
    public LoadResponse() {}
    
    // Constructor
    public LoadResponse(UUID id, String shipperId, FacilityDto facility, String productType,
                       String truckType, Integer noOfTrucks, Double weight, String comment,
                       LoadStatus status, LocalDateTime datePosted, LocalDateTime updatedAt) {
        this.id = id;
        this.shipperId = shipperId;
        this.facility = facility;
        this.productType = productType;
        this.truckType = truckType;
        this.noOfTrucks = noOfTrucks;
        this.weight = weight;
        this.comment = comment;
        this.status = status;
        this.datePosted = datePosted;
        this.updatedAt = updatedAt;
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
    
    public FacilityDto getFacility() {
        return facility;
    }
    
    public void setFacility(FacilityDto facility) {
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
}
