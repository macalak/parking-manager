package com.example.parkingmanager.application.port;

import com.example.parkingmanager.domain.Facility;
import com.example.parkingmanager.domain.Occupancy;
import com.example.parkingmanager.domain.OpeningHoursEntry;

import java.util.List;
import java.util.Optional;

public interface ParkingFacilityPort {

    List<Facility> findFacilities();

    Optional<Occupancy> findOccupancy(String locationId);

    List<OpeningHoursEntry> findOpeningHours(String locationId);
}
