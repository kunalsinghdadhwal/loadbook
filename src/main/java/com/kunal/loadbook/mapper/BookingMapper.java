package com.kunal.loadbook.mapper;

import com.kunal.loadbook.dto.booking.BookingResponse;
import com.kunal.loadbook.dto.booking.CreateBookingRequest;
import com.kunal.loadbook.dto.booking.UpdateBookingRequest;
import com.kunal.loadbook.entity.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    /**
     * Convert CreateBookingRequest to Booking entity
     */
    public Booking toEntity(CreateBookingRequest request) {
        if (request == null) {
            return null;
        }

        Booking booking = new Booking();
        booking.setTransporterId(request.getTransporterId());
        booking.setProposedRate(request.getProposedRate());
        booking.setComment(request.getComment());

        return booking;
    }

    /**
     * Convert Booking entity to BookingResponse
     */
    public BookingResponse toResponse(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setLoadId(booking.getLoad() != null ? booking.getLoad().getId() : null);
        response.setTransporterId(booking.getTransporterId());
        response.setProposedRate(booking.getProposedRate());
        response.setComment(booking.getComment());
        response.setStatus(booking.getStatus());
        response.setRequestedAt(booking.getRequestedAt());
        response.setUpdatedAt(booking.getUpdatedAt());

        return response;
    }

    /**
     * Update Booking entity from UpdateBookingRequest
     */
    public void updateEntity(Booking booking, UpdateBookingRequest request) {
        if (booking == null || request == null) {
            return;
        }

        if (request.getProposedRate() != null) {
            booking.setProposedRate(request.getProposedRate());
        }

        if (request.getComment() != null) {
            booking.setComment(request.getComment());
        }
    }
}
