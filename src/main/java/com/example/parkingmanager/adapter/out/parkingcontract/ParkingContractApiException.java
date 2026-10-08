package com.example.parkingmanager.adapter.out.parkingcontract;

public class ParkingContractApiException extends RuntimeException {

    public ParkingContractApiException(String message) {
        super(message);
    }

    public ParkingContractApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
