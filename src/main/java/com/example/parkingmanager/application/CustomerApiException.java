package com.example.parkingmanager.application;

public class CustomerApiException extends RuntimeException {

    private final String businessId;

    public CustomerApiException(String businessId, Throwable cause) {
        super("Failed to load customer: " + businessId, cause);
        this.businessId = businessId;
    }

    public String businessId() {
        return businessId;
    }
}
