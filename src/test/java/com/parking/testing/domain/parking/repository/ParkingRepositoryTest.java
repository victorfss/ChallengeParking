package com.parking.testing.domain.parking.repository;

import com.parking.domain.parking.model.Parking;
import com.parking.domain.parking.repository.ParkingRepository;
import com.parking.domain.vehicle.model.Vehicle;
import com.parking.domain.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
@DataJpaTest
class ParkingRepositoryTest {

    @Autowired
    ParkingRepository parkingRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    private Vehicle vehicle;
    private Parking parking;

    @BeforeEach
    void setUp(){
        vehicle = new Vehicle("ABC-1234");
        vehicle = vehicleRepository.save(vehicle);

        parking = new Parking();
        parking.setRegistrationDate(LocalDateTime.now());
        parking.setVehicle(vehicle);
        parking.setPaid(false);
        parking.setReservationCode(UUID.randomUUID().toString().substring(0,6));
        parking = parkingRepository.save(parking);

    }

    @Test
    void findParkingByVehicleId_thenReturnParking() {
        Optional<Parking> maybeParking = parkingRepository.findByVehicleId(parking.getVehicle().getId());
        assertTrue(maybeParking.isPresent());
        assertNotNull(maybeParking.get().getId());
        assertNotNull(maybeParking.get().getReservationCode());
        assertNotNull(maybeParking.get().getVehicle());
    }

    @Test
    void notFoundId_thenReturnOptionalEmpty() {
        Optional<Parking> maybeParking = parkingRepository.findByVehicleId(2L);
        assertTrue(maybeParking.isEmpty());
    }

    @Test
    void whenIfParkingIsPaidAndNotLeftTheParkingLot_thenReturnOptionalParking() {
        parking.setPaid(true);
        parking = parkingRepository.save(parking);
        Optional<Parking> maybeParking = parkingRepository.findByVehicleIdAndPaidIsTrueAndExitDateIsNull(parking.getVehicle().getId());
        assertTrue(maybeParking.isPresent());
        assertTrue(maybeParking.get().isPaid());
        assertNull(maybeParking.get().getExitDate());
        assertNull(maybeParking.get().getTimeSpent());
    }

    @Test
    void whenVehicleIsInTheParkingLot_thenReturnOptionalParking() {
        Optional<Parking> maybeParking = parkingRepository.findByVehicleIdAndExitDateIsNull(parking.getVehicle().getId());
        assertTrue(maybeParking.isPresent());
        assertFalse(maybeParking.get().isPaid());
        assertNull(maybeParking.get().getExitDate());
        assertNull(maybeParking.get().getTimeSpent());
    }

    @Test
    void whenThereAreParkingsUsedByVehicle_thenReturnListOptionalParking() {
        Optional<List<Parking>> maybeParking = parkingRepository.findByVehiclePlateOrderByIdDesc(vehicle.getPlate());
        assertTrue(maybeParking.isPresent());
        assertEquals(1, maybeParking.get().size());
        assertFalse(maybeParking.get().get(0).isPaid());
        assertNotNull(maybeParking.get().get(0).getReservationCode());
    }
}
