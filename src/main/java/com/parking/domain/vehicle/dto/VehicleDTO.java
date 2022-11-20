package com.parking.domain.vehicle.dto;

public class VehicleDTO {
    private String plate;

    public VehicleDTO() {}

    public VehicleDTO(String plate) {
        this.plate = plate;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }
}
