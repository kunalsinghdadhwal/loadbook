package com.kunal.loadbook.controller;

import com.kunal.loadbook.dto.common.PagedResponse;
import com.kunal.loadbook.dto.load.CreateLoadRequest;
import com.kunal.loadbook.dto.load.LoadResponse;
import com.kunal.loadbook.dto.load.UpdateLoadRequest;
import com.kunal.loadbook.enums.LoadStatus;
import com.kunal.loadbook.service.LoadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/load")
@Tag(name = "Load Management", description = "APIs for managing loads")
public class LoadController {

        private final LoadService loadService;

        @Autowired
        public LoadController(LoadService loadService) {
                this.loadService = loadService;
        }

        @Operation(summary = "Create a new load", description = "Creates a new load with the provided details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Load created successfully", content = @Content(schema = @Schema(implementation = LoadResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PostMapping
        public ResponseEntity<LoadResponse> createLoad(@Valid @RequestBody CreateLoadRequest request) {
                LoadResponse response = loadService.createLoad(request);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        @Operation(summary = "Get loads with filtering and pagination", description = "Retrieves loads with optional filtering by shipper ID, truck type, and status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Loads retrieved successfully", content = @Content(schema = @Schema(implementation = PagedResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping
        public ResponseEntity<PagedResponse<LoadResponse>> getLoads(
                        @Parameter(description = "Filter by shipper ID") @RequestParam(required = false) String shipperId,

                        @Parameter(description = "Filter by truck type") @RequestParam(required = false) String truckType,

                        @Parameter(description = "Filter by load status") @RequestParam(required = false) LoadStatus status,

                        @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,

                        @Parameter(description = "Page size (max 100)", example = "10") @RequestParam(defaultValue = "10") int size) {

                PagedResponse<LoadResponse> response = loadService.getLoads(shipperId, truckType, status, page, size);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Get load by ID", description = "Retrieves a specific load by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Load found", content = @Content(schema = @Schema(implementation = LoadResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Load not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/{loadId}")
        public ResponseEntity<LoadResponse> getLoadById(
                        @Parameter(description = "Load ID") @PathVariable UUID loadId) {

                LoadResponse response = loadService.getLoadById(loadId);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Update load", description = "Updates an existing load with the provided details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Load updated successfully", content = @Content(schema = @Schema(implementation = LoadResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
                        @ApiResponse(responseCode = "404", description = "Load not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PutMapping("/{loadId}")
        public ResponseEntity<LoadResponse> updateLoad(
                        @Parameter(description = "Load ID") @PathVariable UUID loadId,
                        @Valid @RequestBody UpdateLoadRequest request) {

                LoadResponse response = loadService.updateLoad(loadId, request);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Delete load", description = "Deletes a specific load by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Load deleted successfully"),
                        @ApiResponse(responseCode = "400", description = "Business rule violation"),
                        @ApiResponse(responseCode = "404", description = "Load not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @DeleteMapping("/{loadId}")
        public ResponseEntity<Void> deleteLoad(
                        @Parameter(description = "Load ID") @PathVariable UUID loadId) {

                loadService.deleteLoad(loadId);
                return ResponseEntity.noContent().build();
        }
}
