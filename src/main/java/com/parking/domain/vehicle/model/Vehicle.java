package com.parking.domain.vehicle.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "TB_VEHICLE")
public class Vehicle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "PLATE_CAR", nullable = false, unique = true, length = 8)
    private String plate;

    public Vehicle() {
    }

    public Vehicle(Long id, String plate) {
        this.id = id;
        this.plate = plate;
    }

    public Vehicle(String plate) {
        this.plate = plate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }
}
