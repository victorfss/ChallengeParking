package com.parking.testing.domain.vehicle.service;

import com.parking.domain.vehicle.model.Vehicle;
import com.parking.domain.vehicle.repository.VehicleRepository;
import com.parking.domain.vehicle.service.VehicleServiceImpl;
import com.parking.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    VehicleServiceImpl vehicleServiceImpl;

    @Mock
    VehicleRepository vehicleRepository;

    private Vehicle vehicle;
    private Vehicle expectedVehicle;

    @BeforeEach
    void setUp(){
        vehicleServiceImpl = new VehicleServiceImpl(vehicleRepository);
        vehicle = new Vehicle(1L, "XYZ-1234");

        expectedVehicle = new Vehicle(1L, "XYZ-1234");

    }

    @Test
    void whenValidPlate_ThenReturnVehicle() {
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(expectedVehicle);
        Vehicle actual = vehicleServiceImpl.create(vehicle.getPlate());
        assertEquals(1, actual.getId());
        assertEquals("XYZ-1234", actual.getPlate());
    }

    @Test
    void whenBlankOrNullInvalidPlate_ThenThrowApiException(){
        assertThrows(ApiException.class, () -> vehicleServiceImpl.isPlateInvalid(""));
        assertThrows(ApiException.class, () -> vehicleServiceImpl.isPlateInvalid(null));
    }

    @Test
    void whenFormatInvalidPlate_ThenThrowApiException() {
        assertThrows(ApiException.class, () -> vehicleServiceImpl.isPlateInvalid("VVV-123"));
    }

    @Test
    void whenQueryByPlate_ThenReturnVehicle() {
        when(vehicleRepository.findByPlate(vehicle.getPlate())).thenReturn(Optional.ofNullable(expectedVehicle));
        Optional<Vehicle> actual = vehicleServiceImpl.findByPlate(vehicle.getPlate());
        assertTrue(actual.isPresent());
        assertEquals(1L, actual.get().getId());
        assertEquals("XYZ-1234", actual.get().getPlate());
    }

}
