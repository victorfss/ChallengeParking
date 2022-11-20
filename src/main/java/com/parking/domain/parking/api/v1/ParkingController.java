package com.parking.domain.parking.api.v1;

import com.parking.domain.parking.dto.ReservationHistoryDTO;
import com.parking.domain.parking.dto.ReservationOutDTO;
import com.parking.domain.parking.service.ParkingService;
import com.parking.domain.vehicle.dto.VehicleDTO;
import com.parking.domain.vehicle.service.VehicleService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation(value = "Histórico de veículo do estacionamento")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorna uma lista de histórico das reservas do veículo"),
            @ApiResponse(code = 404, message = "Não existe histórico para este veículo "),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @GetMapping(value = "/{plate}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReservationHistoryDTO>> reservationHistory(@PathVariable("plate") final String plate){
        this.vehicleService.isPlateInvalid(plate);
        return ResponseEntity.ok().body(this.parkingService.reservationHistory(plate));
    }


    @ApiOperation(value = "Criar código de reserva para o veículo entrar no estacionamento")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorna o código de reserva"),
            @ApiResponse(code = 409, message = "Já existe veículo no estacionamento com a mesma placa"),
            @ApiResponse(code = 422, message = "Existe algum problema no formato da placa do veículo"),
            @ApiResponse(code = 500, message = "Erro interno"),
    })
    @PostMapping(value= "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationOutDTO> park(@RequestBody VehicleDTO vehicleDTO){
        this.vehicleService.isPlateInvalid(vehicleDTO.getPlate());
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(parkingService.create(vehicleDTO));
    }

    @ApiOperation(value = "Serviço para sair do estacionamento")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = ""),
            @ApiResponse(code = 400, message = "A reserva não foi paga"),
            @ApiResponse(code = 400, message = "Veículo não encontrado"),
            @ApiResponse(code = 422, message = "Existe algum problema no formato da placa do veículo"),
            @ApiResponse(code = 500, message = "Erro Interno"),
    })
    @PutMapping(value= "/{plate}/out")
    public ResponseEntity<Object> leaveParkink(@PathVariable("plate") final String plate){
        this.vehicleService.isPlateInvalid(plate);
        parkingService.leaveParking(plate);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Serviço para pagar o estacionamento")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = ""),
            @ApiResponse(code = 404, message = "Não foi encontrado o veículo no estacionamento"),
            @ApiResponse(code = 422, message = "Existe algum problema no formato da placa do veículo"),
            @ApiResponse(code = 500, message = "Erro Interno"),
    })
    @PutMapping(value= "/{plate}/pay")
    public ResponseEntity<Object> pay(@PathVariable("plate") final String plate){
        this.vehicleService.isPlateInvalid(plate);
        parkingService.pay(plate);
        return ResponseEntity.noContent().build();
    }
}