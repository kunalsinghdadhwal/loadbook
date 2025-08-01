package com.kunal.loadbook.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static ResourceNotFoundException load(String loadId) {
        return new ResourceNotFoundException("Load not found with ID: " + loadId);
    }
    
    public static ResourceNotFoundException booking(String bookingId) {
        return new ResourceNotFoundException("Booking not found with ID: " + bookingId);
    }
}
