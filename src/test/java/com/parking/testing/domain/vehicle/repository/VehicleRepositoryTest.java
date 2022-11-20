package com.parking.testing.domain.vehicle.repository;

import com.parking.domain.vehicle.model.Vehicle;
import com.parking.domain.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class VehicleRepositoryTest {

    @Autowired
    VehicleRepository vehicleRepository;

    private Vehicle vehicle;

    @BeforeEach
    void setUp(){
        vehicle = new Vehicle("ABC-4444");
        vehicle = vehicleRepository.save(vehicle);

    }

    @Test
    void whenFindVehicleByPlate_thenReturnOptionalVehicle() {
        Optional<Vehicle> maybeVehicle = vehicleRepository.findByPlate(vehicle.getPlate());
        assertTrue(maybeVehicle.isPresent());
        assertNotNull(maybeVehicle.get().getId());
        assertNotNull(maybeVehicle.get().getPlate());
    }

    @Test
    void whenPlateNotExist_thenReturnOptionalEmptu() {
        Optional<Vehicle> maybeVehicle = vehicleRepository.findByPlate("ZZZ-1234");
        assertTrue(maybeVehicle.isEmpty());
    }
}
