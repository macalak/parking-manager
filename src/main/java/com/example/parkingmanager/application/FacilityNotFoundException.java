package com.example.parkingmanager.application;

public class FacilityNotFoundException extends RuntimeException {

    private final String locationId;

    public FacilityNotFoundException(String locationId) {
        super("Parking facility not found: " + locationId);
        this.locationId = locationId;
    }

    public String locationId() {
        return locationId;
    }
}
