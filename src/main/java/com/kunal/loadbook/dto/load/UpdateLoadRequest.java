package com.kunal.loadbook.dto.load;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request DTO for updating a load")
public class UpdateLoadRequest {

    @Schema(description = "Facility information including loading and unloading details")
    @Valid
    private FacilityDto facility;

    @Schema(description = "Type of product being shipped", example = "Electronics")
    private String productType;

    @Schema(description = "Type of truck required", example = "Container")
    private String truckType;

    @Schema(description = "Number of trucks required", example = "2")
    @Positive(message = "Number of trucks must be positive")
    private Integer noOfTrucks;

    @Schema(description = "Weight of the load in tons", example = "15.5")
    @Positive(message = "Weight must be positive")
    private Double weight;

    @Schema(description = "Additional comments or notes", example = "Handle with care")
    private String comment;

    // Default constructor
    public UpdateLoadRequest() {
    }

    // Getters and Setters
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
