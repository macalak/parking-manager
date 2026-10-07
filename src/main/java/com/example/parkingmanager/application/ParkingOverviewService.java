package com.example.parkingmanager.application;

import com.example.parkingmanager.application.port.ParkingFacilityPort;
import com.example.parkingmanager.domain.Facility;
import com.example.parkingmanager.domain.FacilityOverview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingOverviewService {

    private static final Logger log = LoggerFactory.getLogger(ParkingOverviewService.class);

    private final ParkingFacilityPort parkingFacilityPort;

    public ParkingOverviewService(ParkingFacilityPort parkingFacilityPort) {
        this.parkingFacilityPort = parkingFacilityPort;
    }

    public List<Facility> findFacilities() {
        return parkingFacilityPort.findFacilities();
    }

    public FacilityOverview getOverview(String locationId) {
        Facility facility = parkingFacilityPort.findFacilities().stream()
                .filter(candidate -> candidate.locationId().equals(locationId))
                .findFirst()
                .orElseThrow(() -> new FacilityNotFoundException(locationId));

        var overview = new FacilityOverview(
                facility,
                parkingFacilityPort.findOccupancy(locationId).orElse(null),
                parkingFacilityPort.findOpeningHours(locationId));

        log.atInfo()
                .addKeyValue("facility_id", locationId)
                .addKeyValue("operation", "load-facility-overview")
                .addKeyValue("status", "success")
                .log("Loaded parking facility overview");
        return overview;
    }
}
