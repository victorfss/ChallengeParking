package com.parking.domain.vehicle.service;

import com.parking.domain.vehicle.model.Vehicle;

import java.util.Optional;

public interface VehicleService {

    void isPlateInvalid(final String plate);

    Vehicle create(final String plate);

    Optional<Vehicle> findByPlate(final String plate);
}
