package com.parking.testing.domain.parking.service;

import com.parking.domain.parking.dto.ReservationHistoryDTO;
import com.parking.domain.parking.dto.ReservationOutDTO;
import com.parking.domain.parking.model.Parking;
import com.parking.domain.parking.repository.ParkingRepository;
import com.parking.domain.parking.service.ParkingServiceImpl;
import com.parking.domain.parking.utils.TimeUtils;
import com.parking.domain.vehicle.dto.VehicleDTO;
import com.parking.domain.vehicle.model.Vehicle;
import com.parking.domain.vehicle.service.VehicleService;
import com.parking.exception.ApiException;
import com.parking.exception.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    VehicleService vehicleService;
    @Mock
    ParkingRepository parkingRepository;

    ParkingServiceImpl parkingServiceImpl;

    private VehicleDTO vehicleDto;
    private Vehicle vehicle;
    private Vehicle expectedVehicle;
    private Parking parking;
    private Parking expectedParking;

    private String expectedReservationCode;

    @BeforeEach
    void setUp(){
        String reservationCode = UUID.randomUUID().toString().substring(0, 6);
        parkingServiceImpl = new ParkingServiceImpl(vehicleService, parkingRepository);
        vehicle = new Vehicle(1L, "XYZ-1234");
        vehicleDto = new VehicleDTO("XYZ-1234");
        parking = new Parking();
        parking.setPaid(false);
        parking.setRegistrationDate(LocalDateTime.now());
        parking.setVehicle(vehicle);
        parking.setReservationCode(reservationCode);

        expectedReservationCode = reservationCode;

        expectedVehicle = new Vehicle(1L, "XYZ-1234");
        expectedParking = new Parking();
        expectedParking.setPaid(false);
        expectedParking.setRegistrationDate(LocalDateTime.now());
        expectedParking.setVehicle(expectedVehicle);
        expectedParking.setReservationCode(reservationCode);
    }

    @Test
    void whenValidVehicleInParkingLot_thenReturnReservation() {
        when(vehicleService.create(vehicleDto.getPlate())).thenReturn(expectedVehicle);
        when(parkingRepository.save(any(Parking.class))).thenReturn(expectedParking);

        ReservationOutDTO actual = parkingServiceImpl.create(vehicleDto);

        assertEquals(expectedReservationCode, actual.getReservationCode());
    }

    @Test
    void whenExistPlateInParkingLot_throwApiExceptionDuplicateParking() {
        when(vehicleService.create(vehicleDto.getPlate())).thenReturn(expectedVehicle);
        when(parkingRepository.findByVehicleIdAndExitDateIsNull(expectedVehicle.getId())).thenReturn(Optional.of(expectedParking));

        try {
            parkingServiceImpl.create(vehicleDto);
            fail();
        } catch (ApiException e) {
            assertEquals(Error.DUPLICATED_PARKING, e.getError());
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    void whenPlateNotExist_throwApiExceptionVehicleNotFound() {
        when(vehicleService.findByPlate(vehicleDto.getPlate())).thenReturn(Optional.empty());
        String plate = vehicleDto.getPlate();
        try {
            parkingServiceImpl.leaveParking(plate);
            fail();
        } catch (ApiException e) {
            assertEquals(Error.VEHICLE_NOT_FOUND, e.getError());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void whenLeftParkingLotIsUnpaid_throwApiExceptionUnpaidParking() {
        when(vehicleService.findByPlate(vehicleDto.getPlate())).thenReturn(Optional.of(expectedVehicle));
        when(parkingRepository.findByVehicleIdAndPaidIsTrueAndExitDateIsNull(expectedVehicle.getId())).thenReturn(Optional.empty());
        String plate = vehicleDto.getPlate();
        try {
            parkingServiceImpl.leaveParking(plate);
            fail();
        } catch (ApiException e) {
            assertEquals(Error.UNPAID_PARKING, e.getError());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void whenLeftParkingLotIsPaid_ReturnParking() {
        when(vehicleService.findByPlate(vehicleDto.getPlate())).thenReturn(Optional.of(expectedVehicle));
        when(parkingRepository.findByVehicleIdAndPaidIsTrueAndExitDateIsNull(expectedVehicle.getId())).thenReturn(Optional.ofNullable(expectedParking));

        LocalDateTime now = LocalDateTime.now();
        parking.setPaid(true);
        parking.setExitDate(now);
        expectedParking.setExitDate(now);
        expectedParking.setPaid(true);
        expectedParking.setTimeSpent(TimeUtils.diffTime(parking.getRegistrationDate().toLocalTime(), parking.getExitDate().toLocalTime()));

        when(parkingRepository.save(any(Parking.class))).thenReturn(expectedParking);

        Parking actual = parkingServiceImpl.leaveParking(vehicleDto.getPlate());
        assertNotNull(actual);
    }

    @Test
    void whenValidPayParking_returnParking() {
        when(vehicleService.findByPlate(vehicleDto.getPlate())).thenReturn(Optional.of(expectedVehicle));
        when(parkingRepository.findByVehicleIdAndPaidIsTrueAndExitDateIsNull(expectedVehicle.getId())).thenReturn(Optional.ofNullable(expectedParking));
        parking.setPaid(true);
        when(parkingRepository.save(any(Parking.class))).thenReturn(expectedParking);

        Parking actual = parkingServiceImpl.leaveParking(vehicleDto.getPlate());
        assertNotNull(actual);
        assertEquals(actual.isPaid(), expectedParking.isPaid());
    }

    @Test
    void whenPayPlateNotExist_throwApiExceptionVehicleNotFound() {
        when(vehicleService.findByPlate(vehicleDto.getPlate())).thenReturn(Optional.empty());
        String plate = vehicleDto.getPlate();
        try {
            parkingServiceImpl.pay(plate);
            fail();
        } catch (ApiException e) {
            assertEquals(Error.VEHICLE_NOT_FOUND, e.getError());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void whenPayVehicleIsNotInParkingLot_throwApiExceptionUnpaidParking() {
        when(vehicleService.findByPlate(vehicleDto.getPlate())).thenReturn(Optional.of(expectedVehicle));
        when(parkingRepository.findByVehicleId(expectedVehicle.getId())).thenReturn(Optional.empty());
        String plate = vehicleDto.getPlate();
        try {
            parkingServiceImpl.pay(plate);
            fail();
        } catch (ApiException e) {
            assertEquals(Error.VEHICLE_IN_PARKING_NOT_FOUND, e.getError());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void whenInfoReservations_thenReturnListReservations() {

        List<Parking> parkings = List.of(
                new Parking(3L, "C1jsab", vehicle, LocalDateTime.now().minus(45, ChronoUnit.MINUTES), null, false, ""),
                new Parking(2L, "PeXs21", vehicle, LocalDateTime.now().minus(90, ChronoUnit.MINUTES), LocalDateTime.now(), false, "1 hours 30 minutes"),
                new Parking(1L, "Aejs35", vehicle, LocalDateTime.now().minus(30, ChronoUnit.MINUTES), LocalDateTime.now(), false, "30 minutes")
        );

        when(parkingRepository.findByVehiclePlateOrderByIdDesc(vehicleDto.getPlate())).thenReturn(Optional.of(parkings));

        List<ReservationHistoryDTO> actual = parkingServiceImpl.reservationHistory(vehicleDto.getPlate());
        assertNotNull(actual);
        assertEquals(3, actual.size());
    }

    @Test
    void whenNotExistInfoReservation_throwApiExceptionVehicleHistoryNotFound() {
        when(parkingRepository.findByVehiclePlateOrderByIdDesc(vehicleDto.getPlate())).thenReturn(Optional.empty());
        String plate = vehicleDto.getPlate();
        try {
            parkingServiceImpl.reservationHistory(plate);
            fail();
        } catch (ApiException e) {
            assertEquals(Error.VEHICLE_HISTORY_NOT_FOUND, e.getError());
        } catch (Exception e) {
            fail();
        }
    }

}
