package com.example.parkingmanager.application;

public class CustomerNotFoundException extends RuntimeException {

    private final String businessId;

    public CustomerNotFoundException(String businessId) {
        super("Customer not found: " + businessId);
        this.businessId = businessId;
    }

    public String businessId() {
        return businessId;
    }
}
