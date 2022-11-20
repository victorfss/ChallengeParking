package com.parking.domain.parking.api.v1;

import com.parking.domain.parking.dto.ReservationHistoryDTO;
import com.parking.domain.parking.dto.ReservationOutDTO;
import com.parking.domain.parking.service.ParkingService;
import com.parking.domain.vehicle.dto.VehicleDTO;
import com.parking.domain.vehicle.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/parking")
public class ParkingController {

    private ParkingService parkingService;
    private VehicleService vehicleService;

    public ParkingController(ParkingService parkingService, VehicleService vehicleService) {
        this.parkingService = parkingService;
        this.vehicleService = vehicleService;
    }

    @GetMapping(value = "/{plate}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReservationHistoryDTO>> reservationHistory(@PathVariable("plate") final String plate){
        this.vehicleService.isPlateInvalid(plate);
        return ResponseEntity.ok().body(this.parkingService.reservationHistory(plate));
    }

    @PostMapping(value= "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationOutDTO> park(@RequestBody VehicleDTO vehicleDTO){
        this.vehicleService.isPlateInvalid(vehicleDTO.getPlate());
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(parkingService.create(vehicleDTO));
    }

    @PutMapping(value= "/{plate}/out")
    public ResponseEntity<Object> leaveParkink(@PathVariable("plate") final String plate){
        this.vehicleService.isPlateInvalid(plate);
        parkingService.leaveParking(plate);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value= "/{plate}/pay")
    public ResponseEntity<Object> pay(@PathVariable("plate") final String plate){
        this.vehicleService.isPlateInvalid(plate);
        parkingService.pay(plate);
        return ResponseEntity.noContent().build();
    }
}