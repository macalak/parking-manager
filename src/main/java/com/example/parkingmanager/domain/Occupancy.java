package com.example.parkingmanager.domain;

public record Occupancy(int maximumPlaces, int occupiedPlaces, int freePlaces) {
    public Occupancy {
        if (maximumPlaces < 0 || occupiedPlaces < 0 || freePlaces < 0) {
            throw new IllegalArgumentException("Occupancy values cannot be negative");
        }
    }
}
