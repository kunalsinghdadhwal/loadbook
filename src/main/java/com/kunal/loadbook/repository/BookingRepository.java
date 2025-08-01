package com.kunal.loadbook.repository;

import com.kunal.loadbook.entity.Booking;
import com.kunal.loadbook.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    
    /**
     * Find bookings with optional filtering and pagination
     */
    @Query("SELECT b FROM Booking b WHERE " +
           "(:loadId IS NULL OR b.load.id = :loadId) AND " +
           "(:transporterId IS NULL OR b.transporterId = :transporterId) AND " +
           "(:status IS NULL OR b.status = :status)")
    Page<Booking> findBookingsWithFilters(@Param("loadId") UUID loadId,
                                         @Param("transporterId") String transporterId,
                                         @Param("status") BookingStatus status,
                                         Pageable pageable);
    
    /**
     * Find bookings by load ID
     */
    List<Booking> findByLoadId(UUID loadId);
    
    /**
     * Find bookings by load ID with pagination
     */
    Page<Booking> findByLoadId(UUID loadId, Pageable pageable);
    
    /**
     * Find bookings by transporter ID
     */
    Page<Booking> findByTransporterId(String transporterId, Pageable pageable);
    
    /**
     * Find bookings by status
     */
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);
    
    /**
     * Find bookings by load ID and status
     */
    List<Booking> findByLoadIdAndStatus(UUID loadId, BookingStatus status);
    
    /**
     * Find bookings by transporter ID and status
     */
    Page<Booking> findByTransporterIdAndStatus(String transporterId, BookingStatus status, Pageable pageable);
    
    /**
     * Find bookings by load ID and transporter ID
     */
    List<Booking> findByLoadIdAndTransporterId(UUID loadId, String transporterId);
    
    /**
     * Check if a booking exists for a load and transporter
     */
    boolean existsByLoadIdAndTransporterId(UUID loadId, String transporterId);
    
    /**
     * Count bookings by load ID
     */
    long countByLoadId(UUID loadId);
    
    /**
     * Count bookings by load ID and status
     */
    long countByLoadIdAndStatus(UUID loadId, BookingStatus status);
    
    /**
     * Count accepted bookings for a load
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.load.id = :loadId AND b.status = 'ACCEPTED'")
    long countAcceptedBookingsByLoadId(@Param("loadId") UUID loadId);
}
