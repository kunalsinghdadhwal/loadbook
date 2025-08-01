package com.kunal.loadbook.config;

import com.kunal.loadbook.entity.Booking;
import com.kunal.loadbook.entity.Facility;
import com.kunal.loadbook.entity.Load;
import com.kunal.loadbook.enums.BookingStatus;
import com.kunal.loadbook.enums.LoadStatus;
import com.kunal.loadbook.repository.BookingRepository;
import com.kunal.loadbook.repository.LoadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private LoadRepository loadRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing sample data...");

        // Check if data already exists
        if (loadRepository.count() > 0) {
            logger.info("Data already exists, skipping initialization");
            return;
        }

        // Create sample loads
        createSampleLoads();

        logger.info("Sample data initialization completed");
    }

    private void createSampleLoads() {
        // Load 1: Electronics shipment
        Facility facility1 = new Facility(
                "Mumbai Port",
                "Delhi Warehouse",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5));

        Load load1 = new Load(
                "SHIPPER_001",
                facility1,
                "Electronics",
                "Container",
                2,
                15.5,
                "Fragile items - handle with care");

        Load savedLoad1 = loadRepository.save(load1);

        // Add some bookings for load1
        Booking booking1 = new Booking(savedLoad1, "TRANSPORTER_001", 25000.0, "Can deliver on time");
        Booking booking2 = new Booking(savedLoad1, "TRANSPORTER_002", 23500.0, "Experienced with electronics");

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        // Load 2: Textile shipment
        Facility facility2 = new Facility(
                "Chennai Port",
                "Bangalore Warehouse",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3));

        Load load2 = new Load(
                "SHIPPER_002",
                facility2,
                "Textiles",
                "Open Truck",
                1,
                8.0,
                "Standard textile shipment");

        Load savedLoad2 = loadRepository.save(load2);

        // Add booking for load2 and accept it
        Booking booking3 = new Booking(savedLoad2, "TRANSPORTER_003", 18000.0, "Regular textile transporter");
        booking3.setStatus(BookingStatus.ACCEPTED);
        bookingRepository.save(booking3);

        // Update load status to BOOKED
        savedLoad2.setStatus(LoadStatus.BOOKED);
        loadRepository.save(savedLoad2);

        // Load 3: Machinery shipment
        Facility facility3 = new Facility(
                "Kolkata Port",
                "Hyderabad Industrial Area",
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(10));

        Load load3 = new Load(
                "SHIPPER_003",
                facility3,
                "Machinery",
                "Flatbed",
                3,
                25.0,
                "Heavy machinery - special handling required");

        loadRepository.save(load3);

        // Load 4: Food products shipment
        Facility facility4 = new Facility(
                "Pune Distribution Center",
                "Ahmedabad Market",
                LocalDateTime.now().plusHours(12),
                LocalDateTime.now().plusDays(1));

        Load load4 = new Load(
                "SHIPPER_001",
                facility4,
                "Food Products",
                "Refrigerated",
                1,
                5.5,
                "Temperature controlled shipment");

        loadRepository.save(load4);

        // Load 5: Cancelled load
        Facility facility5 = new Facility(
                "Goa Port",
                "Mangalore Warehouse",
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(6));

        Load load5 = new Load(
                "SHIPPER_004",
                facility5,
                "Chemicals",
                "Tanker",
                1,
                12.0,
                "Chemical products - hazardous");
        load5.setStatus(LoadStatus.CANCELLED);

        loadRepository.save(load5);

        logger.info("Created {} sample loads", 5);
        logger.info("Created {} sample bookings", 3);
    }
}
