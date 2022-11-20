package com.parking.domain.parking.dto;

public class ReservationOutDTO {
    private String reservationCode;

    public ReservationOutDTO(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }
}
