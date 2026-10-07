package com.example.parkingmanager.adapter.out.parkingapi;

public class ParkingApiException extends RuntimeException {

    public ParkingApiException(String message) {
        super(message);
    }

    public ParkingApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
