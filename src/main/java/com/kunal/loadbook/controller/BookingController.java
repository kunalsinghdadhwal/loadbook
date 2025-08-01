package com.kunal.loadbook.controller;

import com.kunal.loadbook.dto.booking.BookingResponse;
import com.kunal.loadbook.dto.booking.CreateBookingRequest;
import com.kunal.loadbook.dto.booking.UpdateBookingRequest;
import com.kunal.loadbook.dto.common.PagedResponse;
import com.kunal.loadbook.enums.BookingStatus;
import com.kunal.loadbook.service.BookingService;
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
@RequestMapping("/api/v1/booking")
@Tag(name = "Booking Management", description = "APIs for managing bookings")
public class BookingController {

        private final BookingService bookingService;

        @Autowired
        public BookingController(BookingService bookingService) {
                this.bookingService = bookingService;
        }

        @Operation(summary = "Create a new booking", description = "Creates a new booking for a load")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Booking created successfully", content = @Content(schema = @Schema(implementation = BookingResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
                        @ApiResponse(responseCode = "404", description = "Load not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PostMapping
        public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
                BookingResponse response = bookingService.createBooking(request);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        @Operation(summary = "Get bookings with filtering and pagination", description = "Retrieves bookings with optional filtering by load ID, transporter ID, and status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully", content = @Content(schema = @Schema(implementation = PagedResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping
        public ResponseEntity<PagedResponse<BookingResponse>> getBookings(
                        @Parameter(description = "Filter by load ID") @RequestParam(required = false) UUID loadId,

                        @Parameter(description = "Filter by transporter ID") @RequestParam(required = false) String transporterId,

                        @Parameter(description = "Filter by booking status") @RequestParam(required = false) BookingStatus status,

                        @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,

                        @Parameter(description = "Page size (max 100)", example = "10") @RequestParam(defaultValue = "10") int size) {

                PagedResponse<BookingResponse> response = bookingService.getBookings(loadId, transporterId, status,
                                page, size);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Get booking by ID", description = "Retrieves a specific booking by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Booking found", content = @Content(schema = @Schema(implementation = BookingResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Booking not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/{bookingId}")
        public ResponseEntity<BookingResponse> getBookingById(
                        @Parameter(description = "Booking ID") @PathVariable UUID bookingId) {

                BookingResponse response = bookingService.getBookingById(bookingId);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Update booking", description = "Updates an existing booking with the provided details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Booking updated successfully", content = @Content(schema = @Schema(implementation = BookingResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
                        @ApiResponse(responseCode = "404", description = "Booking not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PutMapping("/{bookingId}")
        public ResponseEntity<BookingResponse> updateBooking(
                        @Parameter(description = "Booking ID") @PathVariable UUID bookingId,
                        @Valid @RequestBody UpdateBookingRequest request) {

                BookingResponse response = bookingService.updateBooking(bookingId, request);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Accept booking", description = "Accepts a pending booking")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Booking accepted successfully", content = @Content(schema = @Schema(implementation = BookingResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Business rule violation"),
                        @ApiResponse(responseCode = "404", description = "Booking not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PatchMapping("/{bookingId}/accept")
        public ResponseEntity<BookingResponse> acceptBooking(
                        @Parameter(description = "Booking ID") @PathVariable UUID bookingId) {

                BookingResponse response = bookingService.acceptBooking(bookingId);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Reject booking", description = "Rejects a pending booking")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Booking rejected successfully", content = @Content(schema = @Schema(implementation = BookingResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Business rule violation"),
                        @ApiResponse(responseCode = "404", description = "Booking not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PatchMapping("/{bookingId}/reject")
        public ResponseEntity<BookingResponse> rejectBooking(
                        @Parameter(description = "Booking ID") @PathVariable UUID bookingId) {

                BookingResponse response = bookingService.rejectBooking(bookingId);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Delete booking", description = "Deletes a specific booking by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
                        @ApiResponse(responseCode = "400", description = "Business rule violation"),
                        @ApiResponse(responseCode = "404", description = "Booking not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @DeleteMapping("/{bookingId}")
        public ResponseEntity<Void> deleteBooking(
                        @Parameter(description = "Booking ID") @PathVariable UUID bookingId) {

                bookingService.deleteBooking(bookingId);
                return ResponseEntity.noContent().build();
        }
}
