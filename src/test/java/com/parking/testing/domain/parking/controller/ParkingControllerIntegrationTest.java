package com.parking.testing.domain.parking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parking.domain.parking.dto.ReservationHistoryDTO;
import com.parking.domain.parking.dto.ReservationOutDTO;
import com.parking.domain.parking.model.Parking;
import com.parking.domain.parking.repository.ParkingRepository;
import com.parking.domain.parking.service.ParkingService;
import com.parking.domain.vehicle.dto.VehicleDTO;
import com.parking.exception.ApiException;
import com.parking.exception.Error;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ParkingControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ParkingService parkingService;

    @MockBean
    private ParkingRepository parkingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private VehicleDTO vehicle;
    private ReservationOutDTO reservationOutDTO;
    private ReservationHistoryDTO reservationHistoryDTO;

    @BeforeEach
    void setUp() {
        vehicle = new VehicleDTO("ABC-1234");
        reservationHistoryDTO = new ReservationHistoryDTO();
        reservationOutDTO = new ReservationOutDTO("ABs32C");
    }

    @Test
    void whenValidParking_thenCreateParking() throws Exception {
        given(parkingService.create(any())).willReturn(reservationOutDTO);

        mvc.perform(post("/v1/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicle)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @ParameterizedTest
    @MethodSource("invalidPlates")
    void whenPostVehicleWithPlateFormatInvalid_thenUnprocessableEntity(String args) throws Exception {
        VehicleDTO vehicle = new VehicleDTO(args);
        mvc.perform(post("/v1/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicle)))
                .andExpect(status().isUnprocessableEntity());
    }

    @MethodSource("invalidParkinkSamePlate")
    @ParameterizedTest
    void whenDuplicatedCarParking_thenThrowConflict(VehicleDTO vehicleDTO) throws Exception {
        when(parkingService.create(any(VehicleDTO.class))).thenThrow(new ApiException(Error.DUPLICATED_PARKING));

        mvc.perform(post("/v1/parking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleDTO))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void whenGetHistoryParking_thenReturnReservationsHistory() throws Exception {
        reservationHistoryDTO.setId(1L);
        reservationHistoryDTO.setTime("30 minutes");
        reservationHistoryDTO.setPaid(false);
        reservationHistoryDTO.setExit(false);
        when(parkingService.reservationHistory(vehicle.getPlate())).thenReturn(List.of(reservationHistoryDTO));

        mvc.perform(get("/v1/parking/" + vehicle.getPlate()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.notNullValue(ReservationHistoryDTO.class)))
                .andExpect(jsonPath("$[0].time", Matchers.notNullValue(ReservationHistoryDTO.class)))
                .andExpect(jsonPath("$[0].paid", Matchers.notNullValue(ReservationHistoryDTO.class)))
                .andExpect(jsonPath("$[0].id", Matchers.notNullValue(ReservationHistoryDTO.class)));
    }

    @Test
    void whenNotExistParkingWithThisLicensePlate_thenThrowNotFound() throws Exception {
        when(parkingService.reservationHistory(vehicle.getPlate())).thenThrow(new ApiException(Error.VEHICLE_HISTORY_NOT_FOUND));

        mvc.perform(get("/v1/parking/"+ vehicle.getPlate()))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenLeaveParking_thenReturnOk() throws Exception {
        when(parkingService.leaveParking(vehicle.getPlate())).thenReturn(new Parking());
        mvc.perform(put("/v1/parking/"+vehicle.getPlate() + "/out"))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenLeaveParkingWithoutPay_thenReturnBadRequest() throws Exception {
        when(parkingService.leaveParking(vehicle.getPlate())).thenThrow(new ApiException(Error.UNPAID_PARKING));
        mvc.perform(put("/v1/parking/"+vehicle.getPlate() + "/out"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPayParking_thenReturnOk() throws Exception {
        mvc.perform(put("/v1/parking/"+vehicle.getPlate() + "/pay"))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenPayParkingAndNotExistVehicleInParking_thenReturnOk() throws Exception {
        when(parkingService.pay(vehicle.getPlate())).thenThrow(new ApiException(Error.VEHICLE_IN_PARKING_NOT_FOUND));
        mvc.perform(put("/v1/parking/"+vehicle.getPlate() + "/pay"))
                .andExpect(status().isNotFound());
    }

    public static Stream<Arguments> invalidParkinkSamePlate() {
        return Stream.of(
                Arguments.of(new VehicleDTO("ABC-1234")),
                Arguments.of(new VehicleDTO("ABC-1234"))
        );
    }

    public static Stream<Arguments> invalidPlates() {
        return Stream.of(
                Arguments.of("ABC-123"),
                Arguments.of("")
        );
    }
}
