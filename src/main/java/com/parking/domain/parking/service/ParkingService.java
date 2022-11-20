package com.parking.domain.parking.service;

import com.parking.domain.parking.dto.ReservationHistoryDTO;
import com.parking.domain.parking.dto.ReservationOutDTO;
import com.parking.domain.parking.model.Parking;
import com.parking.domain.vehicle.dto.VehicleDTO;

import java.util.List;

public interface ParkingService {
    ReservationOutDTO create(final VehicleDTO vehicleDTO);

    Parking pay(final String plate);

    Parking leaveParking(final String plate);

    List<ReservationHistoryDTO> reservationHistory(String plate);
}
