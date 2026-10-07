package com.example.parkingmanager.domain;

import java.util.List;

public record FacilityOverview(
        Facility facility,
        Occupancy occupancy,
        List<OpeningHoursEntry> openingHours) {

    public FacilityOverview {
        openingHours = List.copyOf(openingHours);
    }

    public boolean hasOccupancy() {
        return occupancy != null;
    }
}
