package com.kunal.loadbook.service;

import com.kunal.loadbook.dto.load.CreateLoadRequest;
import com.kunal.loadbook.dto.load.FacilityDto;
import com.kunal.loadbook.dto.load.LoadResponse;
import com.kunal.loadbook.dto.load.UpdateLoadRequest;
import com.kunal.loadbook.dto.common.PagedResponse;
import com.kunal.loadbook.entity.Load;
import com.kunal.loadbook.enums.LoadStatus;
import com.kunal.loadbook.exception.BusinessLogicException;
import com.kunal.loadbook.exception.ResourceNotFoundException;
import com.kunal.loadbook.mapper.LoadMapper;
import com.kunal.loadbook.repository.LoadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoadServiceTest {
    
    @Mock
    private LoadRepository loadRepository;
    
    @Mock
    private LoadMapper loadMapper;
    
    @InjectMocks
    private LoadService loadService;
    
    private Load testLoad;
    private CreateLoadRequest createRequest;
    private LoadResponse loadResponse;
    private FacilityDto facilityDto;
    
    @BeforeEach
    void setUp() {
        facilityDto = new FacilityDto(
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
        
        testLoad = new Load();
        testLoad.setId(UUID.randomUUID());
        testLoad.setShipperId("SHIPPER_001");
        testLoad.setStatus(LoadStatus.POSTED);
        
        loadResponse = new LoadResponse();
        loadResponse.setId(testLoad.getId());
        loadResponse.setShipperId("SHIPPER_001");
        loadResponse.setStatus(LoadStatus.POSTED);
    }
    
    @Test
    void createLoad_Success() {
        // Arrange
        when(loadMapper.toEntity(createRequest)).thenReturn(testLoad);
        when(loadRepository.save(testLoad)).thenReturn(testLoad);
        when(loadMapper.toResponse(testLoad)).thenReturn(loadResponse);
        
        // Act
        LoadResponse result = loadService.createLoad(createRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals(testLoad.getId(), result.getId());
        assertEquals("SHIPPER_001", result.getShipperId());
        verify(loadRepository).save(testLoad);
        verify(loadMapper).toEntity(createRequest);
        verify(loadMapper).toResponse(testLoad);
    }
    
    @Test
    void createLoad_InvalidDates_ThrowsException() {
        // Arrange
        FacilityDto invalidFacility = new FacilityDto(
                "Mumbai Port",
                "Delhi Warehouse",
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(1) // Unloading before loading
        );
        createRequest.setFacility(invalidFacility);
        
        // Act & Assert
        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class,
                () -> loadService.createLoad(createRequest)
        );
        assertEquals("Loading date cannot be after unloading date", exception.getMessage());
        verify(loadRepository, never()).save(any());
    }
    
    @Test
    void getLoads_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Load> loadPage = new PageImpl<>(List.of(testLoad), pageable, 1);
        
        when(loadRepository.findLoadsWithFilters(eq("SHIPPER_001"), eq("Container"), 
                eq(LoadStatus.POSTED), any(Pageable.class))).thenReturn(loadPage);
        when(loadMapper.toResponse(testLoad)).thenReturn(loadResponse);
        
        // Act
        PagedResponse<LoadResponse> result = loadService.getLoads(
                "SHIPPER_001", "Container", LoadStatus.POSTED, 0, 10);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getTotalElements());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
    }
    
    @Test
    void getLoads_InvalidPageNumber_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loadService.getLoads(null, null, null, -1, 10)
        );
        assertEquals("Page number cannot be negative", exception.getMessage());
    }
    
    @Test
    void getLoads_InvalidPageSize_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loadService.getLoads(null, null, null, 0, 0)
        );
        assertEquals("Page size must be between 1 and 100", exception.getMessage());
    }
    
    @Test
    void getLoadById_Success() {
        // Arrange
        UUID loadId = testLoad.getId();
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));
        when(loadMapper.toResponse(testLoad)).thenReturn(loadResponse);
        
        // Act
        LoadResponse result = loadService.getLoadById(loadId);
        
        // Assert
        assertNotNull(result);
        assertEquals(loadId, result.getId());
        verify(loadRepository).findById(loadId);
        verify(loadMapper).toResponse(testLoad);
    }
    
    @Test
    void getLoadById_NotFound_ThrowsException() {
        // Arrange
        UUID loadId = UUID.randomUUID();
        when(loadRepository.findById(loadId)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> loadService.getLoadById(loadId)
        );
        assertEquals("Load not found with ID: " + loadId, exception.getMessage());
    }
    
    @Test
    void updateLoad_Success() {
        // Arrange
        UUID loadId = testLoad.getId();
        UpdateLoadRequest updateRequest = new UpdateLoadRequest();
        updateRequest.setProductType("Updated Electronics");
        
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));
        when(loadRepository.save(testLoad)).thenReturn(testLoad);
        when(loadMapper.toResponse(testLoad)).thenReturn(loadResponse);
        
        // Act
        LoadResponse result = loadService.updateLoad(loadId, updateRequest);
        
        // Assert
        assertNotNull(result);
        verify(loadMapper).updateEntity(testLoad, updateRequest);
        verify(loadRepository).save(testLoad);
    }
    
    @Test
    void updateLoad_BookedLoad_ThrowsException() {
        // Arrange
        testLoad.setStatus(LoadStatus.BOOKED);
        UUID loadId = testLoad.getId();
        UpdateLoadRequest updateRequest = new UpdateLoadRequest();
        
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));
        
        // Act & Assert
        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class,
                () -> loadService.updateLoad(loadId, updateRequest)
        );
        assertEquals("Cannot update a booked load", exception.getMessage());
        verify(loadRepository, never()).save(any());
    }
    
    @Test
    void deleteLoad_Success() {
        // Arrange
        UUID loadId = testLoad.getId();
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));
        
        // Act
        loadService.deleteLoad(loadId);
        
        // Assert
        verify(loadRepository).delete(testLoad);
    }
    
    @Test
    void deleteLoad_BookedLoad_ThrowsException() {
        // Arrange
        testLoad.setStatus(LoadStatus.BOOKED);
        UUID loadId = testLoad.getId();
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));
        
        // Act & Assert
        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class,
                () -> loadService.deleteLoad(loadId)
        );
        assertEquals("Cannot delete a booked load", exception.getMessage());
        verify(loadRepository, never()).delete(any());
    }
    
    @Test
    void updateLoadStatus_ValidTransition_Success() {
        // Arrange
        UUID loadId = testLoad.getId();
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));
        when(loadRepository.save(testLoad)).thenReturn(testLoad);
        
        // Act
        loadService.updateLoadStatus(loadId, LoadStatus.BOOKED);
        
        // Assert
        assertEquals(LoadStatus.BOOKED, testLoad.getStatus());
        verify(loadRepository).save(testLoad);
    }
    
    @Test
    void updateLoadStatus_InvalidTransition_ThrowsException() {
        // Arrange
        testLoad.setStatus(LoadStatus.CANCELLED);
        UUID loadId = testLoad.getId();
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));
        
        // Act & Assert
        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class,
                () -> loadService.updateLoadStatus(loadId, LoadStatus.POSTED)
        );
        assertTrue(exception.getMessage().contains("Invalid status transition"));
        verify(loadRepository, never()).save(any());
    }
}
