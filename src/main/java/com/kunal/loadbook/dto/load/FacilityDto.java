package com.kunal.loadbook.dto.load;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Facility information DTO")
public class FacilityDto {

    @Schema(description = "Loading point location", example = "Mumbai Port")
    @NotBlank(message = "Loading point is required")
    private String loadingPoint;

    @Schema(description = "Unloading point location", example = "Delhi Warehouse")
    @NotBlank(message = "Unloading point is required")
    private String unloadingPoint;

    @Schema(description = "Loading date and time", example = "2025-08-15T10:00:00")
    @NotNull(message = "Loading date is required")
    private LocalDateTime loadingDate;

    @Schema(description = "Unloading date and time", example = "2025-08-18T14:00:00")
    @NotNull(message = "Unloading date is required")
    private LocalDateTime unloadingDate;

    // Default constructor
    public FacilityDto() {
    }

    // Constructor
    public FacilityDto(String loadingPoint, String unloadingPoint,
            LocalDateTime loadingDate, LocalDateTime unloadingDate) {
        this.loadingPoint = loadingPoint;
        this.unloadingPoint = unloadingPoint;
        this.loadingDate = loadingDate;
        this.unloadingDate = unloadingDate;
    }

    // Getters and Setters
    public String getLoadingPoint() {
        return loadingPoint;
    }

    public void setLoadingPoint(String loadingPoint) {
        this.loadingPoint = loadingPoint;
    }

    public String getUnloadingPoint() {
        return unloadingPoint;
    }

    public void setUnloadingPoint(String unloadingPoint) {
        this.unloadingPoint = unloadingPoint;
    }

    public LocalDateTime getLoadingDate() {
        return loadingDate;
    }

    public void setLoadingDate(LocalDateTime loadingDate) {
        this.loadingDate = loadingDate;
    }

    public LocalDateTime getUnloadingDate() {
        return unloadingDate;
    }

    public void setUnloadingDate(LocalDateTime unloadingDate) {
        this.unloadingDate = unloadingDate;
    }
}
