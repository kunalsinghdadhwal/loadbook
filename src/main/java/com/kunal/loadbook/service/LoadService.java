package com.kunal.loadbook.service;

import com.kunal.loadbook.dto.common.PagedResponse;
import com.kunal.loadbook.dto.load.CreateLoadRequest;
import com.kunal.loadbook.dto.load.LoadResponse;
import com.kunal.loadbook.dto.load.UpdateLoadRequest;
import com.kunal.loadbook.entity.Load;
import com.kunal.loadbook.enums.LoadStatus;
import com.kunal.loadbook.exception.BusinessLogicException;
import com.kunal.loadbook.exception.ResourceNotFoundException;
import com.kunal.loadbook.mapper.LoadMapper;
import com.kunal.loadbook.repository.LoadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoadService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoadService.class);
    
    private final LoadRepository loadRepository;
    private final LoadMapper loadMapper;
    
    @Autowired
    public LoadService(LoadRepository loadRepository, LoadMapper loadMapper) {
        this.loadRepository = loadRepository;
        this.loadMapper = loadMapper;
    }
    
    /**
     * Create a new load
     */
    public LoadResponse createLoad(CreateLoadRequest request) {
        logger.info("Creating new load for shipper: {}", request.getShipperId());
        
        // Validate facility dates
        if (request.getFacility().getLoadingDate().isAfter(request.getFacility().getUnloadingDate())) {
            throw new BusinessLogicException("Loading date cannot be after unloading date");
        }
        
        Load load = loadMapper.toEntity(request);
        Load savedLoad = loadRepository.save(load);
        
        logger.info("Load created successfully with ID: {}", savedLoad.getId());
        return loadMapper.toResponse(savedLoad);
    }
    
    /**
     * Get loads with filtering and pagination
     */
    @Transactional(readOnly = true)
    public PagedResponse<LoadResponse> getLoads(String shipperId, String truckType, 
                                               LoadStatus status, int page, int size) {
        
        logger.info("Fetching loads with filters - shipperId: {}, truckType: {}, status: {}, page: {}, size: {}",
                shipperId, truckType, status, page, size);
        
        // Validate pagination parameters
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("datePosted").descending());
        Page<Load> loadPage = loadRepository.findLoadsWithFilters(shipperId, truckType, status, pageable);
        
        List<LoadResponse> loadResponses = loadPage.getContent()
                .stream()
                .map(loadMapper::toResponse)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                loadResponses,
                loadPage.getNumber(),
                loadPage.getSize(),
                loadPage.getTotalElements(),
                loadPage.getTotalPages(),
                loadPage.isFirst(),
                loadPage.isLast(),
                loadPage.hasNext(),
                loadPage.hasPrevious()
        );
    }
    
    /**
     * Get load by ID
     */
    @Transactional(readOnly = true)
    public LoadResponse getLoadById(UUID loadId) {
        logger.info("Fetching load with ID: {}", loadId);
        
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> ResourceNotFoundException.load(loadId.toString()));
        
        return loadMapper.toResponse(load);
    }
    
    /**
     * Update load
     */
    public LoadResponse updateLoad(UUID loadId, UpdateLoadRequest request) {
        logger.info("Updating load with ID: {}", loadId);
        
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> ResourceNotFoundException.load(loadId.toString()));
        
        // Check if load can be updated
        if (load.getStatus() == LoadStatus.BOOKED) {
            throw new BusinessLogicException("Cannot update a booked load");
        }
        if (load.getStatus() == LoadStatus.CANCELLED) {
            throw new BusinessLogicException("Cannot update a cancelled load");
        }
        
        // Validate facility dates if being updated
        if (request.getFacility() != null && 
            request.getFacility().getLoadingDate() != null && 
            request.getFacility().getUnloadingDate() != null &&
            request.getFacility().getLoadingDate().isAfter(request.getFacility().getUnloadingDate())) {
            throw new BusinessLogicException("Loading date cannot be after unloading date");
        }
        
        loadMapper.updateEntity(load, request);
        Load updatedLoad = loadRepository.save(load);
        
        logger.info("Load updated successfully with ID: {}", updatedLoad.getId());
        return loadMapper.toResponse(updatedLoad);
    }
    
    /**
     * Delete load
     */
    public void deleteLoad(UUID loadId) {
        logger.info("Deleting load with ID: {}", loadId);
        
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> ResourceNotFoundException.load(loadId.toString()));
        
        // Check if load can be deleted
        if (load.getStatus() == LoadStatus.BOOKED) {
            throw new BusinessLogicException("Cannot delete a booked load");
        }
        
        loadRepository.delete(load);
        logger.info("Load deleted successfully with ID: {}", loadId);
    }
    
    /**
     * Update load status
     */
    public void updateLoadStatus(UUID loadId, LoadStatus newStatus) {
        logger.info("Updating load status for ID: {} to {}", loadId, newStatus);
        
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> ResourceNotFoundException.load(loadId.toString()));
        
        LoadStatus oldStatus = load.getStatus();
        
        // Validate status transition
        if (!isValidStatusTransition(oldStatus, newStatus)) {
            throw new BusinessLogicException("Invalid status transition from " + oldStatus + " to " + newStatus);
        }
        
        load.setStatus(newStatus);
        loadRepository.save(load);
        
        logger.info("Load status updated from {} to {} for ID: {}", oldStatus, newStatus, loadId);
    }
    
    /**
     * Get load entity by ID (for internal use)
     */
    @Transactional(readOnly = true)
    public Load getLoadEntityById(UUID loadId) {
        return loadRepository.findById(loadId)
                .orElseThrow(() -> ResourceNotFoundException.load(loadId.toString()));
    }
    
    /**
     * Validate status transition
     */
    private boolean isValidStatusTransition(LoadStatus from, LoadStatus to) {
        switch (from) {
            case POSTED:
                return to == LoadStatus.BOOKED || to == LoadStatus.CANCELLED;
            case BOOKED:
                return to == LoadStatus.CANCELLED || to == LoadStatus.POSTED;
            case CANCELLED:
                return false; // Cannot transition from cancelled to any other status
            default:
                return false;
        }
    }
}
