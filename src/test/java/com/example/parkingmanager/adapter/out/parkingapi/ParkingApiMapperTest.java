package com.example.parkingmanager.adapter.out.parkingapi;

import com.example.parkingmanager.adapter.out.parkingapi.generated.model.DefaultCounter;
import com.example.parkingmanager.adapter.out.parkingapi.generated.model.OpeningHour;
import com.example.parkingmanager.adapter.out.parkingapi.generated.model.OpeningHours;
import com.example.parkingmanager.adapter.out.parkingapi.generated.model.PublicGetOpeningHoursByLocationId200ResponseEconnectDto;
import com.example.parkingmanager.adapter.out.parkingapi.generated.model.PublicGetStandardCounters200ResponseFacilityInnerCountersEconnectDto;
import com.example.parkingmanager.adapter.out.parkingapi.generated.model.PublicGetStandardCountersByLocationId200ResponseEconnectDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParkingApiMapperTest {

    private final ParkingApiMapper mapper = new ParkingApiMapper();

    @Test
    void mapsPhysicalCounterIntoOccupancy() {
        var counters = new PublicGetStandardCounters200ResponseFacilityInnerCountersEconnectDto()
                .standardCounter(List.of(
                        new DefaultCounter().id(1).max(80).present(25).free(55),
                        new DefaultCounter().id(3).max(100).present(40).free(60)));
        var response = new PublicGetStandardCountersByLocationId200ResponseEconnectDto().counters(counters);

        var occupancy = mapper.toOccupancy(response).orElseThrow();

        assertEquals(100, occupancy.maximumPlaces());
        assertEquals(40, occupancy.occupiedPlaces());
        assertEquals(60, occupancy.freePlaces());
    }

    @Test
    void formatsOpeningHoursAndClosedDays() {
        var openingHours = new OpeningHours(1).openHour(List.of(
                new OpeningHour().weekday("Monday").timeFormat("24h")
                        .hourOpen(8).minutesOpen(5).hourClosing(18).minutesClosing(30),
                new OpeningHour().weekday("Tuesday").timeFormat("AM")
                        .hourOpen(0).minutesOpen(0),
                new OpeningHour().weekday("Wednesday").timeFormat("PM")
                        .hourOpen(8).minutesOpen(0).hourClosing(11).minutesClosing(30)));
        var response = new PublicGetOpeningHoursByLocationId200ResponseEconnectDto()
                .openingHours(openingHours);

        var entries = mapper.toOpeningHours(response);

        assertEquals("08:05", entries.getFirst().opensAt());
        assertEquals("18:30", entries.getFirst().closesAt());
        assertEquals("12:00 AM", entries.get(1).opensAt());
        assertEquals("Closed", entries.get(1).closesAt());
        assertEquals("8:00 PM", entries.get(2).opensAt());
        assertEquals("11:30 PM", entries.get(2).closesAt());
    }

    @Test
    void returnsNoOccupancyWhenFacilityCounterIsMissing() {
        var counters = new PublicGetStandardCounters200ResponseFacilityInnerCountersEconnectDto()
                .standardCounter(List.of(new DefaultCounter().id(1).max(80).present(25).free(55)));
        var response = new PublicGetStandardCountersByLocationId200ResponseEconnectDto().counters(counters);

        assertTrue(mapper.toOccupancy(response).isEmpty());
    }
}
