package com.kunal.loadbook.dto.load;

import com.kunal.loadbook.enums.LoadStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request DTO for creating a new load")
public class CreateLoadRequest {
    
    @Schema(description = "Shipper identifier", example = "SHIPPER_001")
    @NotBlank(message = "Shipper ID is required")
    private String shipperId;
    
    @Schema(description = "Facility information including loading and unloading details")
    @Valid
    @NotNull(message = "Facility information is required")
    private FacilityDto facility;
    
    @Schema(description = "Type of product being shipped", example = "Electronics")
    @NotBlank(message = "Product type is required")
    private String productType;
    
    @Schema(description = "Type of truck required", example = "Container")
    @NotBlank(message = "Truck type is required")
    private String truckType;
    
    @Schema(description = "Number of trucks required", example = "2")
    @NotNull(message = "Number of trucks is required")
    @Positive(message = "Number of trucks must be positive")
    private Integer noOfTrucks;
    
    @Schema(description = "Weight of the load in tons", example = "15.5")
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;
    
    @Schema(description = "Additional comments or notes", example = "Handle with care")
    private String comment;
    
    // Default constructor
    public CreateLoadRequest() {}
    
    // Constructor
    public CreateLoadRequest(String shipperId, FacilityDto facility, String productType, 
                           String truckType, Integer noOfTrucks, Double weight, String comment) {
        this.shipperId = shipperId;
        this.facility = facility;
        this.productType = productType;
        this.truckType = truckType;
        this.noOfTrucks = noOfTrucks;
        this.weight = weight;
        this.comment = comment;
    }
    
    // Getters and Setters
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
}
