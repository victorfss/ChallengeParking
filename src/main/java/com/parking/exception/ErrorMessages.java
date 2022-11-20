package com.parking.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ErrorMessages {
    private final Map<String, Object> error;
    public ErrorMessages() {
        error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
    }

    public void put(String message) {
        error.put("message", message);
    }

    public Map<String, Object> getBody() {
        return error;
    }
}
