package com.kunal.loadbook.service;

import com.kunal.loadbook.dto.booking.BookingResponse;
import com.kunal.loadbook.dto.booking.CreateBookingRequest;
import com.kunal.loadbook.dto.booking.UpdateBookingRequest;
import com.kunal.loadbook.dto.common.PagedResponse;
import com.kunal.loadbook.entity.Booking;
import com.kunal.loadbook.entity.Load;
import com.kunal.loadbook.enums.BookingStatus;
import com.kunal.loadbook.enums.LoadStatus;
import com.kunal.loadbook.exception.BusinessLogicException;
import com.kunal.loadbook.exception.ResourceNotFoundException;
import com.kunal.loadbook.mapper.BookingMapper;
import com.kunal.loadbook.repository.BookingRepository;
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
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final LoadService loadService;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingService(BookingRepository bookingRepository, LoadService loadService,
            BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.loadService = loadService;
        this.bookingMapper = bookingMapper;
    }

    /**
     * Create a new booking
     */
    public BookingResponse createBooking(CreateBookingRequest request) {
        logger.info("Creating new booking for load: {} by transporter: {}",
                request.getLoadId(), request.getTransporterId());

        // Get the load and validate
        Load load = loadService.getLoadEntityById(request.getLoadId());

        // Validate business rules
        if (load.getStatus() == LoadStatus.CANCELLED) {
            throw BusinessLogicException.loadAlreadyCancelled();
        }

        // Check if booking already exists for this load and transporter
        if (bookingRepository.existsByLoadIdAndTransporterId(request.getLoadId(), request.getTransporterId())) {
            throw BusinessLogicException.bookingAlreadyExists();
        }

        // Create booking with PENDING status (default)
        Booking booking = bookingMapper.toEntity(request);
        booking.setLoad(load);

        Booking savedBooking = bookingRepository.save(booking);

        logger.info("Booking created successfully with ID: {}", savedBooking.getId());
        return bookingMapper.toResponse(savedBooking);
    }

    /**
     * Get bookings with filtering and pagination
     */
    @Transactional(readOnly = true)
    public PagedResponse<BookingResponse> getBookings(UUID loadId, String transporterId,
            BookingStatus status, int page, int size) {

        logger.info("Fetching bookings with filters - loadId: {}, transporterId: {}, status: {}, page: {}, size: {}",
                loadId, transporterId, status, page, size);

        // Validate pagination parameters
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("requestedAt").descending());
        Page<Booking> bookingPage = bookingRepository.findBookingsWithFilters(loadId, transporterId, status, pageable);

        List<BookingResponse> bookingResponses = bookingPage.getContent()
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                bookingResponses,
                bookingPage.getNumber(),
                bookingPage.getSize(),
                bookingPage.getTotalElements(),
                bookingPage.getTotalPages(),
                bookingPage.isFirst(),
                bookingPage.isLast(),
                bookingPage.hasNext(),
                bookingPage.hasPrevious());
    }

    /**
     * Get booking by ID
     */
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(UUID bookingId) {
        logger.info("Fetching booking with ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> ResourceNotFoundException.booking(bookingId.toString()));

        return bookingMapper.toResponse(booking);
    }

    /**
     * Update booking
     */
    public BookingResponse updateBooking(UUID bookingId, UpdateBookingRequest request) {
        logger.info("Updating booking with ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> ResourceNotFoundException.booking(bookingId.toString()));

        // Check if booking can be updated
        if (booking.getStatus() == BookingStatus.ACCEPTED) {
            throw new BusinessLogicException("Cannot update an accepted booking");
        }
        if (booking.getStatus() == BookingStatus.REJECTED) {
            throw new BusinessLogicException("Cannot update a rejected booking");
        }

        bookingMapper.updateEntity(booking, request);
        Booking updatedBooking = bookingRepository.save(booking);

        logger.info("Booking updated successfully with ID: {}", updatedBooking.getId());
        return bookingMapper.toResponse(updatedBooking);
    }

    /**
     * Accept booking
     */
    public BookingResponse acceptBooking(UUID bookingId) {
        logger.info("Accepting booking with ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> ResourceNotFoundException.booking(bookingId.toString()));

        // Validate business rules
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw BusinessLogicException.invalidStatusTransition(
                    booking.getStatus().toString(), BookingStatus.ACCEPTED.toString());
        }

        // Check if load is still available for booking
        Load load = booking.getLoad();
        if (load.getStatus() == LoadStatus.CANCELLED) {
            throw new BusinessLogicException("Cannot accept booking for a cancelled load");
        }

        // Check if load is already booked (has another accepted booking)
        long acceptedBookingsCount = bookingRepository.countAcceptedBookingsByLoadId(load.getId());
        if (acceptedBookingsCount > 0) {
            throw new BusinessLogicException("Load is already booked by another transporter");
        }

        // Update booking status
        booking.setStatus(BookingStatus.ACCEPTED);
        Booking updatedBooking = bookingRepository.save(booking);

        // Update load status to BOOKED
        loadService.updateLoadStatus(load.getId(), LoadStatus.BOOKED);

        // Reject all other pending bookings for this load
        List<Booking> otherPendingBookings = bookingRepository.findByLoadIdAndStatus(
                load.getId(), BookingStatus.PENDING);

        for (Booking otherBooking : otherPendingBookings) {
            if (!otherBooking.getId().equals(bookingId)) {
                otherBooking.setStatus(BookingStatus.REJECTED);
                bookingRepository.save(otherBooking);
            }
        }

        logger.info("Booking accepted successfully with ID: {}", bookingId);
        return bookingMapper.toResponse(updatedBooking);
    }

    /**
     * Reject booking
     */
    public BookingResponse rejectBooking(UUID bookingId) {
        logger.info("Rejecting booking with ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> ResourceNotFoundException.booking(bookingId.toString()));

        // Validate business rules
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw BusinessLogicException.invalidStatusTransition(
                    booking.getStatus().toString(), BookingStatus.REJECTED.toString());
        }

        // Update booking status
        booking.setStatus(BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);

        // Check if all bookings are rejected/deleted, revert load status to POSTED
        checkAndRevertLoadStatus(booking.getLoad().getId());

        logger.info("Booking rejected successfully with ID: {}", bookingId);
        return bookingMapper.toResponse(updatedBooking);
    }

    /**
     * Delete booking
     */
    public void deleteBooking(UUID bookingId) {
        logger.info("Deleting booking with ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> ResourceNotFoundException.booking(bookingId.toString()));

        // Check if booking can be deleted
        if (booking.getStatus() == BookingStatus.ACCEPTED) {
            throw BusinessLogicException.cannotDeleteAcceptedBooking();
        }

        UUID loadId = booking.getLoad().getId();
        bookingRepository.delete(booking);

        // Check if all bookings are deleted/rejected, revert load status to POSTED
        checkAndRevertLoadStatus(loadId);

        logger.info("Booking deleted successfully with ID: {}", bookingId);
    }

    /**
     * Check and revert load status to POSTED if all bookings are rejected/deleted
     */
    private void checkAndRevertLoadStatus(UUID loadId) {
        List<Booking> activeBookings = bookingRepository.findByLoadIdAndStatus(loadId, BookingStatus.PENDING);
        List<Booking> acceptedBookings = bookingRepository.findByLoadIdAndStatus(loadId, BookingStatus.ACCEPTED);

        // If no pending or accepted bookings, revert load status to POSTED
        if (activeBookings.isEmpty() && acceptedBookings.isEmpty()) {
            Load load = loadService.getLoadEntityById(loadId);
            if (load.getStatus() == LoadStatus.BOOKED) {
                loadService.updateLoadStatus(loadId, LoadStatus.POSTED);
                logger.info("Load status reverted to POSTED for load ID: {}", loadId);
            }
        }
    }
}
