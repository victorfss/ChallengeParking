package com.parking.exception;

import org.springframework.http.HttpStatus;

public enum Error {
    DUPLICATED_PARKING("there's already a car in the parking lot with the same license plate!", HttpStatus.CONFLICT),
    PLATE_BLANK_INVALID("Plate is empty. Use a valid format!. E.g.: ABC-1234", HttpStatus.UNPROCESSABLE_ENTITY),
    PLATE_FORMAT_INVALID("Plate format invalid. Use format AAA-1111", HttpStatus.UNPROCESSABLE_ENTITY),
    UNPAID_PARKING("parking wasn't paid!", HttpStatus.BAD_REQUEST),
    VEHICLE_NOT_FOUND("Vehicle not found", HttpStatus.NOT_FOUND),
    VEHICLE_HISTORY_NOT_FOUND("no history of this plate was found in the parking lot", HttpStatus.NOT_FOUND),
    VEHICLE_IN_PARKING_NOT_FOUND("No car parks were found with this license plate", HttpStatus.NOT_FOUND),
    ;

    private final String message;
    private final HttpStatus status;

    Error(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
