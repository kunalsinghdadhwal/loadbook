package com.kunal.loadbook.mapper;

import com.kunal.loadbook.dto.load.CreateLoadRequest;
import com.kunal.loadbook.dto.load.FacilityDto;
import com.kunal.loadbook.dto.load.LoadResponse;
import com.kunal.loadbook.dto.load.UpdateLoadRequest;
import com.kunal.loadbook.entity.Facility;
import com.kunal.loadbook.entity.Load;
import org.springframework.stereotype.Component;

@Component
public class LoadMapper {
    
    /**
     * Convert CreateLoadRequest to Load entity
     */
    public Load toEntity(CreateLoadRequest request) {
        if (request == null) {
            return null;
        }
        
        Load load = new Load();
        load.setShipperId(request.getShipperId());
        load.setFacility(toFacilityEntity(request.getFacility()));
        load.setProductType(request.getProductType());
        load.setTruckType(request.getTruckType());
        load.setNoOfTrucks(request.getNoOfTrucks());
        load.setWeight(request.getWeight());
        load.setComment(request.getComment());
        
        return load;
    }
    
    /**
     * Convert Load entity to LoadResponse
     */
    public LoadResponse toResponse(Load load) {
        if (load == null) {
            return null;
        }
        
        LoadResponse response = new LoadResponse();
        response.setId(load.getId());
        response.setShipperId(load.getShipperId());
        response.setFacility(toFacilityDto(load.getFacility()));
        response.setProductType(load.getProductType());
        response.setTruckType(load.getTruckType());
        response.setNoOfTrucks(load.getNoOfTrucks());
        response.setWeight(load.getWeight());
        response.setComment(load.getComment());
        response.setStatus(load.getStatus());
        response.setDatePosted(load.getDatePosted());
        response.setUpdatedAt(load.getUpdatedAt());
        
        return response;
    }
    
    /**
     * Update Load entity from UpdateLoadRequest
     */
    public void updateEntity(Load load, UpdateLoadRequest request) {
        if (load == null || request == null) {
            return;
        }
        
        if (request.getFacility() != null) {
            load.setFacility(toFacilityEntity(request.getFacility()));
        }
        
        if (request.getProductType() != null) {
            load.setProductType(request.getProductType());
        }
        
        if (request.getTruckType() != null) {
            load.setTruckType(request.getTruckType());
        }
        
        if (request.getNoOfTrucks() != null) {
            load.setNoOfTrucks(request.getNoOfTrucks());
        }
        
        if (request.getWeight() != null) {
            load.setWeight(request.getWeight());
        }
        
        if (request.getComment() != null) {
            load.setComment(request.getComment());
        }
    }
    
    /**
     * Convert FacilityDto to Facility entity
     */
    public Facility toFacilityEntity(FacilityDto facilityDto) {
        if (facilityDto == null) {
            return null;
        }
        
        Facility facility = new Facility();
        facility.setLoadingPoint(facilityDto.getLoadingPoint());
        facility.setUnloadingPoint(facilityDto.getUnloadingPoint());
        facility.setLoadingDate(facilityDto.getLoadingDate());
        facility.setUnloadingDate(facilityDto.getUnloadingDate());
        
        return facility;
    }
    
    /**
     * Convert Facility entity to FacilityDto
     */
    public FacilityDto toFacilityDto(Facility facility) {
        if (facility == null) {
            return null;
        }
        
        FacilityDto facilityDto = new FacilityDto();
        facilityDto.setLoadingPoint(facility.getLoadingPoint());
        facilityDto.setUnloadingPoint(facility.getUnloadingPoint());
        facilityDto.setLoadingDate(facility.getLoadingDate());
        facilityDto.setUnloadingDate(facility.getUnloadingDate());
        
        return facilityDto;
    }
}
