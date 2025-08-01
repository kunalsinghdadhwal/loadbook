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
class BookingServiceTest {

        @Mock
        private BookingRepository bookingRepository;

        @Mock
        private LoadService loadService;

        @Mock
        private BookingMapper bookingMapper;

        @InjectMocks
        private BookingService bookingService;

        private Load testLoad;
        private Booking testBooking;
        private CreateBookingRequest createRequest;
        private BookingResponse bookingResponse;

        @BeforeEach
        void setUp() {
                testLoad = new Load();
                testLoad.setId(UUID.randomUUID());
                testLoad.setShipperId("SHIPPER_001");
                testLoad.setStatus(LoadStatus.POSTED);

                testBooking = new Booking();
                testBooking.setId(UUID.randomUUID());
                testBooking.setLoad(testLoad);
                testBooking.setTransporterId("TRANSPORTER_001");
                testBooking.setProposedRate(25000.0);
                testBooking.setStatus(BookingStatus.PENDING);
                testBooking.setRequestedAt(LocalDateTime.now());

                createRequest = new CreateBookingRequest(
                                testLoad.getId(),
                                "TRANSPORTER_001",
                                25000.0,
                                "Can deliver within 3 days");

                bookingResponse = new BookingResponse();
                bookingResponse.setId(testBooking.getId());
                bookingResponse.setLoadId(testLoad.getId());
                bookingResponse.setTransporterId("TRANSPORTER_001");
                bookingResponse.setProposedRate(25000.0);
                bookingResponse.setStatus(BookingStatus.PENDING);
        }

        @Test
        void createBooking_Success() {
                // Arrange
                when(loadService.getLoadEntityById(testLoad.getId())).thenReturn(testLoad);
                when(bookingRepository.existsByLoadIdAndTransporterId(testLoad.getId(), "TRANSPORTER_001"))
                                .thenReturn(false);
                when(bookingMapper.toEntity(createRequest)).thenReturn(testBooking);
                when(bookingRepository.save(testBooking)).thenReturn(testBooking);
                when(bookingMapper.toResponse(testBooking)).thenReturn(bookingResponse);

                // Act
                BookingResponse result = bookingService.createBooking(createRequest);

                // Assert
                assertNotNull(result);
                assertEquals(testBooking.getId(), result.getId());
                assertEquals("TRANSPORTER_001", result.getTransporterId());
                verify(loadService).updateLoadStatus(testLoad.getId(), LoadStatus.BOOKED);
                verify(bookingRepository).save(testBooking);
        }

        @Test
        void createBooking_CancelledLoad_ThrowsException() {
                // Arrange
                testLoad.setStatus(LoadStatus.CANCELLED);
                when(loadService.getLoadEntityById(testLoad.getId())).thenReturn(testLoad);

                // Act & Assert
                BusinessLogicException exception = assertThrows(
                                BusinessLogicException.class,
                                () -> bookingService.createBooking(createRequest));
                assertEquals("Cannot create booking for a cancelled load", exception.getMessage());
                verify(bookingRepository, never()).save(any());
        }

        @Test
        void createBooking_BookedLoad_ThrowsException() {
                // Arrange
                testLoad.setStatus(LoadStatus.BOOKED);
                when(loadService.getLoadEntityById(testLoad.getId())).thenReturn(testLoad);

                // Act & Assert
                BusinessLogicException exception = assertThrows(
                                BusinessLogicException.class,
                                () -> bookingService.createBooking(createRequest));
                assertEquals("Load is already booked", exception.getMessage());
                verify(bookingRepository, never()).save(any());
        }

        @Test
        void createBooking_BookingAlreadyExists_ThrowsException() {
                // Arrange
                when(loadService.getLoadEntityById(testLoad.getId())).thenReturn(testLoad);
                when(bookingRepository.existsByLoadIdAndTransporterId(testLoad.getId(), "TRANSPORTER_001"))
                                .thenReturn(true);

                // Act & Assert
                BusinessLogicException exception = assertThrows(
                                BusinessLogicException.class,
                                () -> bookingService.createBooking(createRequest));
                assertEquals("Booking already exists for this load and transporter", exception.getMessage());
                verify(bookingRepository, never()).save(any());
        }

        @Test
        void getBookings_Success() {
                // Arrange
                Pageable pageable = PageRequest.of(0, 10);
                Page<Booking> bookingPage = new PageImpl<>(List.of(testBooking), pageable, 1);

                when(bookingRepository.findBookingsWithFilters(eq(testLoad.getId()), eq("TRANSPORTER_001"),
                                eq(BookingStatus.PENDING), any(Pageable.class))).thenReturn(bookingPage);
                when(bookingMapper.toResponse(testBooking)).thenReturn(bookingResponse);

                // Act
                PagedResponse<BookingResponse> result = bookingService.getBookings(
                                testLoad.getId(), "TRANSPORTER_001", BookingStatus.PENDING, 0, 10);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals(0, result.getPage());
                assertEquals(10, result.getSize());
                assertEquals(1, result.getTotalElements());
        }

        @Test
        void getBookingById_Success() {
                // Arrange
                UUID bookingId = testBooking.getId();
                when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
                when(bookingMapper.toResponse(testBooking)).thenReturn(bookingResponse);

                // Act
                BookingResponse result = bookingService.getBookingById(bookingId);

                // Assert
                assertNotNull(result);
                assertEquals(bookingId, result.getId());
                verify(bookingRepository).findById(bookingId);
                verify(bookingMapper).toResponse(testBooking);
        }

        @Test
        void getBookingById_NotFound_ThrowsException() {
                // Arrange
                UUID bookingId = UUID.randomUUID();
                when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

                // Act & Assert
                ResourceNotFoundException exception = assertThrows(
                                ResourceNotFoundException.class,
                                () -> bookingService.getBookingById(bookingId));
                assertEquals("Booking not found with ID: " + bookingId, exception.getMessage());
        }

        @Test
        void updateBooking_Success() {
                // Arrange
                UUID bookingId = testBooking.getId();
                UpdateBookingRequest updateRequest = new UpdateBookingRequest();
                updateRequest.setProposedRate(30000.0);

                when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
                when(bookingRepository.save(testBooking)).thenReturn(testBooking);
                when(bookingMapper.toResponse(testBooking)).thenReturn(bookingResponse);

                // Act
                BookingResponse result = bookingService.updateBooking(bookingId, updateRequest);

                // Assert
                assertNotNull(result);
                verify(bookingMapper).updateEntity(testBooking, updateRequest);
                verify(bookingRepository).save(testBooking);
        }

        @Test
        void updateBooking_AcceptedBooking_ThrowsException() {
                // Arrange
                testBooking.setStatus(BookingStatus.ACCEPTED);
                UUID bookingId = testBooking.getId();
                UpdateBookingRequest updateRequest = new UpdateBookingRequest();

                when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

                // Act & Assert
                BusinessLogicException exception = assertThrows(
                                BusinessLogicException.class,
                                () -> bookingService.updateBooking(bookingId, updateRequest));
                assertEquals("Cannot update an accepted booking", exception.getMessage());
                verify(bookingRepository, never()).save(any());
        }

        @Test
        void acceptBooking_Success() {
                // Arrange
                UUID bookingId = testBooking.getId();
                when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
                when(bookingRepository.save(testBooking)).thenReturn(testBooking);
                when(bookingMapper.toResponse(testBooking)).thenReturn(bookingResponse);
                when(bookingRepository.findByLoadIdAndStatus(testLoad.getId(), BookingStatus.PENDING))
                                .thenReturn(List.of());

                // Act
                BookingResponse result = bookingService.acceptBooking(bookingId);

                // Assert
                assertNotNull(result);
                assertEquals(BookingStatus.ACCEPTED, testBooking.getStatus());
                verify(bookingRepository).save(testBooking);
        }

        @Test
        void acceptBooking_NonPendingBooking_ThrowsException() {
                // Arrange
                testBooking.setStatus(BookingStatus.ACCEPTED);
                UUID bookingId = testBooking.getId();
                when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

                // Act & Assert
                BusinessLogicException exception = assertThrows(
                                BusinessLogicException.class,
                                () -> bookingService.acceptBooking(bookingId));
                assertTrue(exception.getMessage().contains("Invalid status transition"));
        }

        @Test
        void rejectBooking_Success() {
                // Arrange
                UUID bookingId = testBooking.getId();
                when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
                when(bookingRepository.save(testBooking)).thenReturn(testBooking);
                when(bookingMapper.toResponse(testBooking)).thenReturn(bookingResponse);
                when(bookingRepository.findByLoadIdAndStatus(testLoad.getId(), BookingStatus.PENDING))
                                .thenReturn(List.of());
                when(bookingRepository.findByLoadIdAndStatus(testLoad.getId(), BookingStatus.ACCEPTED))
                                .thenReturn(List.of());
                when(loadService.getLoadEntityById(testLoad.getId())).thenReturn(testLoad);

                // Act
                BookingResponse result = bookingService.rejectBooking(bookingId);

                // Assert
                assertNotNull(result);
                assertEquals(BookingStatus.REJECTED, testBooking.getStatus());
                verify(bookingRepository).save(testBooking);
        }

        @Test
        void deleteBooking_Success() {
                // Arrange
                UUID bookingId = testBooking.getId();
                when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
                when(bookingRepository.findByLoadIdAndStatus(testLoad.getId(), BookingStatus.PENDING))
                                .thenReturn(List.of());
                when(bookingRepository.findByLoadIdAndStatus(testLoad.getId(), BookingStatus.ACCEPTED))
                                .thenReturn(List.of());
                when(loadService.getLoadEntityById(testLoad.getId())).thenReturn(testLoad);

                // Act
                bookingService.deleteBooking(bookingId);

                // Assert
                verify(bookingRepository).delete(testBooking);
        }

        @Test
        void deleteBooking_AcceptedBooking_ThrowsException() {
                // Arrange
                testBooking.setStatus(BookingStatus.ACCEPTED);
                UUID bookingId = testBooking.getId();
                when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

                // Act & Assert
                BusinessLogicException exception = assertThrows(
                                BusinessLogicException.class,
                                () -> bookingService.deleteBooking(bookingId));
                assertEquals("Cannot delete an accepted booking", exception.getMessage());
                verify(bookingRepository, never()).delete(any());
        }
}
