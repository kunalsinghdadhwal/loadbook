package com.kunal.loadbook.controller;

import com.kunal.loadbook.dto.load.CreateLoadRequest;
import com.kunal.loadbook.dto.load.FacilityDto;
import com.kunal.loadbook.dto.load.LoadResponse;
import com.kunal.loadbook.dto.load.UpdateLoadRequest;
import com.kunal.loadbook.dto.common.PagedResponse;
import com.kunal.loadbook.enums.LoadStatus;
import com.kunal.loadbook.service.LoadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoadController.class)
class LoadControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private LoadService loadService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private CreateLoadRequest createRequest;
    private LoadResponse loadResponse;
    private UUID testLoadId;
    
    @BeforeEach
    void setUp() {
        testLoadId = UUID.randomUUID();
        
        FacilityDto facilityDto = new FacilityDto(
                "Mumbai Port",
                "Delhi Warehouse",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3)
        );
        
        createRequest = new CreateLoadRequest(
                "SHIPPER_001",
                facilityDto,
                "Electronics",
                "Container",
                2,
                15.5,
                "Handle with care"
        );
        
        loadResponse = new LoadResponse();
        loadResponse.setId(testLoadId);
        loadResponse.setShipperId("SHIPPER_001");
        loadResponse.setFacility(facilityDto);
        loadResponse.setProductType("Electronics");
        loadResponse.setTruckType("Container");
        loadResponse.setNoOfTrucks(2);
        loadResponse.setWeight(15.5);
        loadResponse.setComment("Handle with care");
        loadResponse.setStatus(LoadStatus.POSTED);
        loadResponse.setDatePosted(LocalDateTime.now());
    }
    
    @Test
    void createLoad_Success() throws Exception {
        // Arrange
        when(loadService.createLoad(any(CreateLoadRequest.class))).thenReturn(loadResponse);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testLoadId.toString()))
                .andExpect(jsonPath("$.shipperId").value("SHIPPER_001"))
                .andExpect(jsonPath("$.productType").value("Electronics"))
                .andExpect(jsonPath("$.status").value("POSTED"));
    }
    
    @Test
    void createLoad_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange - Invalid request with missing required fields
        CreateLoadRequest invalidRequest = new CreateLoadRequest();
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/load")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getLoads_Success() throws Exception {
        // Arrange
        PagedResponse<LoadResponse> pagedResponse = new PagedResponse<>(
                List.of(loadResponse), 0, 10, 1, 1, true, true, false, false);
        
        when(loadService.getLoads(eq("SHIPPER_001"), eq("Container"), 
                eq(LoadStatus.POSTED), eq(0), eq(10))).thenReturn(pagedResponse);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/load")
                        .param("shipperId", "SHIPPER_001")
                        .param("truckType", "Container")
                        .param("status", "POSTED")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(testLoadId.toString()))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpected(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    void getLoadById_Success() throws Exception {
        // Arrange
        when(loadService.getLoadById(testLoadId)).thenReturn(loadResponse);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/load/{loadId}", testLoadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testLoadId.toString()))
                .andExpect(jsonPath("$.shipperId").value("SHIPPER_001"));
    }
    
    @Test
    void updateLoad_Success() throws Exception {
        // Arrange
        UpdateLoadRequest updateRequest = new UpdateLoadRequest();
        updateRequest.setProductType("Updated Electronics");
        
        loadResponse.setProductType("Updated Electronics");
        when(loadService.updateLoad(eq(testLoadId), any(UpdateLoadRequest.class)))
                .thenReturn(loadResponse);
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/load/{loadId}", testLoadId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productType").value("Updated Electronics"));
    }
    
    @Test
    void deleteLoad_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/load/{loadId}", testLoadId))
                .andExpect(status().isNoContent());
    }
}
