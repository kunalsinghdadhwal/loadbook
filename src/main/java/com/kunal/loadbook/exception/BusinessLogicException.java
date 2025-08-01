package com.kunal.loadbook.exception;

public class BusinessLogicException extends RuntimeException {

    public BusinessLogicException(String message) {
        super(message);
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }

    public static BusinessLogicException loadAlreadyCancelled() {
        return new BusinessLogicException("Cannot create booking for a cancelled load");
    }

    public static BusinessLogicException loadAlreadyBooked() {
        return new BusinessLogicException("Load is already booked");
    }

    public static BusinessLogicException bookingAlreadyExists() {
        return new BusinessLogicException("Booking already exists for this load and transporter");
    }

    public static BusinessLogicException invalidStatusTransition(String from, String to) {
        return new BusinessLogicException("Invalid status transition from " + from + " to " + to);
    }

    public static BusinessLogicException cannotDeleteAcceptedBooking() {
        return new BusinessLogicException("Cannot delete an accepted booking");
    }
}
