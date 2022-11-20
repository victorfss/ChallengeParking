package com.parking.domain.parking.model;

import com.parking.domain.vehicle.model.Vehicle;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PARKING")
public class Parking implements Serializable {

    private static final long serialVersionUID = -1567966952955349300L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "RESERVATION_CODE", nullable = false, length = 6)
    private String reservationCode;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "VEHICLE_ID", nullable = false)
    private Vehicle vehicle;

    @Column(name = "REGISTRATION_DATE", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "EXIT_DATE")
    private LocalDateTime exitDate;

    @Column(name = "PAID")
    private boolean paid;

    @Column(name = "TIME_SPENT")
    private String timeSpent;

    public Parking() {
    }

    public Parking(Long id, String reservationCode, Vehicle vehicle, LocalDateTime registrationDate, LocalDateTime exitDate, boolean paid, String timeSpent) {
        this.id = id;
        this.reservationCode = reservationCode;
        this.vehicle = vehicle;
        this.registrationDate = registrationDate;
        this.exitDate = exitDate;
        this.paid = paid;
        this.timeSpent = timeSpent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getExitDate() {
        return exitDate;
    }

    public void setExitDate(LocalDateTime exitDate) {
        this.exitDate = exitDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }
}
