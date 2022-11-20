package com.parking.exception;

public class ApiException extends RuntimeException {
    private final Error error;
    public ApiException(Error error) {
        super(error.getMessage());
        this.error = error;
    }

    public Error getError() {
        return error;
    }
}
