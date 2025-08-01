package com.kunal.loadbook.integration;

import com.kunal.loadbook.dto.load.CreateLoadRequest;
import com.kunal.loadbook.dto.load.FacilityDto;
import com.kunal.loadbook.dto.load.LoadResponse;
import com.kunal.loadbook.enums.LoadStatus;
import com.kunal.loadbook.repository.LoadRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class LoadIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private LoadRepository loadRepository;

        @Test
        void createAndGetLoad_Success() throws Exception {
                // Arrange
                FacilityDto facilityDto = new FacilityDto(
                                "Mumbai Port",
                                "Delhi Warehouse",
                                LocalDateTime.now().plusDays(1),
                                LocalDateTime.now().plusDays(3));

                CreateLoadRequest createRequest = new CreateLoadRequest(
                                "SHIPPER_001",
                                facilityDto,
                                "Electronics",
                                "Container",
                                2,
                                15.5,
                                "Handle with care");

                // Act - Create load
                String response = mockMvc.perform(post("/api/v1/load")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.shipperId").value("SHIPPER_001"))
                                .andExpect(jsonPath("$.status").value("POSTED"))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                LoadResponse loadResponse = objectMapper.readValue(response, LoadResponse.class);
                String loadId = loadResponse.getId().toString();

                // Assert - Verify load was created in database
                assertEquals(1, loadRepository.count());

                // Act - Get the created load
                mockMvc.perform(get("/api/v1/load/{loadId}", loadId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(loadId))
                                .andExpect(jsonPath("$.shipperId").value("SHIPPER_001"))
                                .andExpect(jsonPath("$.productType").value("Electronics"))
                                .andExpect(jsonPath("$.status").value("POSTED"));
        }

        @Test
        void getLoadsWithFiltering_Success() throws Exception {
                // Arrange - Create multiple loads
                createTestLoad("SHIPPER_001", "Container", LoadStatus.POSTED);
                createTestLoad("SHIPPER_002", "Truck", LoadStatus.POSTED);
                createTestLoad("SHIPPER_001", "Truck", LoadStatus.BOOKED);

                // Act & Assert - Filter by shipper ID
                mockMvc.perform(get("/api/v1/load")
                                .param("shipperId", "SHIPPER_001")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.totalElements").value(2));

                // Act & Assert - Filter by truck type
                mockMvc.perform(get("/api/v1/load")
                                .param("truckType", "Container")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalElements").value(1));

                // Act & Assert - Filter by status
                mockMvc.perform(get("/api/v1/load")
                                .param("status", "POSTED")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalElements").value(2));
        }

        private void createTestLoad(String shipperId, String truckType, LoadStatus status) throws Exception {
                FacilityDto facilityDto = new FacilityDto(
                                "Loading Point",
                                "Unloading Point",
                                LocalDateTime.now().plusDays(1),
                                LocalDateTime.now().plusDays(3));

                CreateLoadRequest request = new CreateLoadRequest(
                                shipperId,
                                facilityDto,
                                "Test Product",
                                truckType,
                                1,
                                10.0,
                                "Test comment");

                mockMvc.perform(post("/api/v1/load")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());
        }
}
