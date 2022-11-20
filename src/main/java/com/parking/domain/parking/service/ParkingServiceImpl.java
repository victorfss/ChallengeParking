package com.parking.domain.parking.service;

import com.parking.domain.parking.dto.ReservationHistoryDTO;
import com.parking.domain.parking.dto.ReservationOutDTO;
import com.parking.domain.parking.model.Parking;
import com.parking.domain.parking.repository.ParkingRepository;
import com.parking.domain.parking.utils.TimeUtils;
import com.parking.domain.vehicle.dto.VehicleDTO;
import com.parking.domain.vehicle.model.Vehicle;
import com.parking.domain.vehicle.service.VehicleService;
import com.parking.exception.ApiException;
import com.parking.exception.Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ParkingServiceImpl implements ParkingService {

    private final VehicleService vehicleService;
    private final ParkingRepository parkingRepository;

    public ParkingServiceImpl(VehicleService vehicleService, ParkingRepository parkingRepository) {
        this.vehicleService = vehicleService;
        this.parkingRepository = parkingRepository;
    }

    @Transactional
    @Override
    public ReservationOutDTO create(final VehicleDTO vehicleDTO) {
        Vehicle vehicle = this.vehicleService.findByPlate(vehicleDTO.getPlate()).orElse(null);
        if(Objects.isNull(vehicle)) {
            vehicle = this.vehicleService.create(vehicleDTO.getPlate());
        }

        Optional<Parking> parking = this.parkingRepository.findByVehicleIdAndExitDateIsNull(vehicle.getId());
        if(!parking.isEmpty()) throw new ApiException(Error.DUPLICATED_PARKING);

        String reservationCode = UUID.randomUUID().toString().substring(0,6);
        Parking park = new Parking();
        park.setVehicle(vehicle);
        park.setReservationCode(reservationCode);
        park.setPaid(false);
        park.setRegistrationDate(LocalDateTime.now());
        park = this.parkingRepository.save(park);

        return new ReservationOutDTO(park.getReservationCode());
    }

    @Transactional
    @Override
    public Parking pay(final String plate) {
        Vehicle vehicle = this.vehicleService.findByPlate(plate).orElseThrow(() -> new ApiException(Error.VEHICLE_NOT_FOUND));
        Parking parking = this.parkingRepository.findByVehicleId(vehicle.getId()).orElseThrow(() -> new ApiException(Error.VEHICLE_IN_PARKING_NOT_FOUND));
        parking.setPaid(true);
        return this.parkingRepository.save(parking);
    }

    @Transactional
    @Override
    public Parking leaveParking(final String plate) {
        Vehicle vehicle = this.vehicleService.findByPlate(plate).orElseThrow(() -> new ApiException(Error.VEHICLE_NOT_FOUND));
        Parking parking = this.getParkingPaid(vehicle.getId()).orElseThrow(() ->new ApiException(Error.UNPAID_PARKING));
        parking.setExitDate(LocalDateTime.now());
        parking.setTimeSpent(TimeUtils.diffTime(parking.getRegistrationDate().toLocalTime(), parking.getExitDate().toLocalTime()));
        return this.parkingRepository.save(parking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReservationHistoryDTO> reservationHistory(final String plate) {
        List<Parking> parkingsVehicle = this.parkingRepository.findByVehiclePlateOrderByIdDesc(plate).
                orElseThrow(() -> new ApiException(Error.VEHICLE_HISTORY_NOT_FOUND));
        List<ReservationHistoryDTO> reservations = new ArrayList<>();
        convertToDTO(parkingsVehicle, reservations);
        return reservations;
    }

    @Transactional(readOnly = true)
    public Optional<Parking> getParkingPaid(final Long idVehicle){
        return this.parkingRepository.findByVehicleIdAndPaidIsTrueAndExitDateIsNull(idVehicle);
    }

    private void convertToDTO(final List<Parking> parkingsVehicle, final List<ReservationHistoryDTO> reservartions){
        parkingsVehicle.forEach(parking -> {
            ReservationHistoryDTO reservationHistoryDTO = new ReservationHistoryDTO();
            reservationHistoryDTO.setExit(Objects.isNull(parking.getExitDate()));
            reservationHistoryDTO.setId(parking.getId());
            reservationHistoryDTO.setPaid(parking.isPaid());
            reservationHistoryDTO.setTime(parking.getTimeSpent());
            reservartions.add(reservationHistoryDTO);
        });
    }
}
