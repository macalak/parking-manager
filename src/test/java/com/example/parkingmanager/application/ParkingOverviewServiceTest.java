package com.example.parkingmanager.application;

import com.example.parkingmanager.application.port.ParkingFacilityPort;
import com.example.parkingmanager.domain.Facility;
import com.example.parkingmanager.domain.Occupancy;
import com.example.parkingmanager.domain.OpeningHoursEntry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ParkingOverviewServiceTest {

    private final ParkingFacilityPort port = mock(ParkingFacilityPort.class);
    private final ParkingOverviewService service = new ParkingOverviewService(port);

    @Test
    void buildsOverviewFromFacilityOccupancyAndHours() {
        var facility = new Facility("loc-1", "Central", "Operator");
        var occupancy = new Occupancy(100, 40, 60);
        var hours = List.of(new OpeningHoursEntry("Monday", "08:00", "18:00"));
        when(port.findFacilities()).thenReturn(List.of(facility));
        when(port.findOccupancy("loc-1")).thenReturn(Optional.of(occupancy));
        when(port.findOpeningHours("loc-1")).thenReturn(hours);

        var overview = service.getOverview("loc-1");

        assertEquals(facility, overview.facility());
        assertEquals(occupancy, overview.occupancy());
        assertEquals(hours, overview.openingHours());
    }

    @Test
    void rejectsAnUnknownFacility() {
        when(port.findFacilities()).thenReturn(List.of());

        assertThrows(FacilityNotFoundException.class, () -> service.getOverview("unknown"));
    }
}
