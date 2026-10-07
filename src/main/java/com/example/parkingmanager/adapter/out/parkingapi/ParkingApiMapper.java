package com.example.parkingmanager.adapter.out.parkingapi;

import com.example.parkingmanager.adapter.out.parkingapi.generated.model.DefaultCounter;
import com.example.parkingmanager.adapter.out.parkingapi.generated.model.OpeningHour;
import com.example.parkingmanager.adapter.out.parkingapi.generated.model.PublicGetFacilities200ResponseEconnectDto;
import com.example.parkingmanager.adapter.out.parkingapi.generated.model.PublicGetOpeningHoursByLocationId200ResponseEconnectDto;
import com.example.parkingmanager.adapter.out.parkingapi.generated.model.PublicGetStandardCountersByLocationId200ResponseEconnectDto;
import com.example.parkingmanager.domain.Facility;
import com.example.parkingmanager.domain.OpeningHoursEntry;
import com.example.parkingmanager.domain.Occupancy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
final class ParkingApiMapper {

    List<Facility> toFacilities(PublicGetFacilities200ResponseEconnectDto response) {
        if (response.getFacility() == null) {
            return List.of();
        }
        return response.getFacility().stream()
                .filter(item -> item.getLocationId() != null)
                .map(item -> new Facility(
                        item.getLocationId(), item.getFacilityName(), item.getBusinessPartnerName()))
                .toList();
    }

    Optional<Occupancy> toOccupancy(PublicGetStandardCountersByLocationId200ResponseEconnectDto response) {
        if (response.getCounters() == null || response.getCounters().getStandardCounter() == null) {
            return Optional.empty();
        }
        return response.getCounters().getStandardCounter().stream()
                .filter(this::isFacilityCounter)
                .findFirst()
                .flatMap(this::toOccupancy);
    }

    List<OpeningHoursEntry> toOpeningHours(PublicGetOpeningHoursByLocationId200ResponseEconnectDto response) {
        if (response.getOpeningHours() == null || response.getOpeningHours().getOpenHour() == null) {
            return List.of();
        }
        return response.getOpeningHours().getOpenHour().stream()
                .map(this::toOpeningHoursEntry)
                .toList();
    }

    private boolean isFacilityCounter(DefaultCounter counter) {
        return counter.getId() != null && counter.getId() == 3;
    }

    private Optional<Occupancy> toOccupancy(DefaultCounter counter) {
        if (counter.getMax() == null || counter.getPresent() == null || counter.getFree() == null) {
            return Optional.empty();
        }
        return Optional.of(new Occupancy(counter.getMax(), counter.getPresent(), counter.getFree()));
    }

    private OpeningHoursEntry toOpeningHoursEntry(OpeningHour hour) {
        return new OpeningHoursEntry(
                hour.getWeekday(),
                formatTime(hour.getHourOpen(), hour.getMinutesOpen(), hour.getTimeFormat()),
                formatTime(hour.getHourClosing(), hour.getMinutesClosing(), hour.getTimeFormat()));
    }

    private String formatTime(Integer hour, Integer minute, String format) {
        if (hour == null || minute == null) {
            return "Closed";
        }
        if ("AM".equalsIgnoreCase(format) || "PM".equalsIgnoreCase(format)) {
            String suffix = format.toUpperCase(Locale.ROOT);
            int displayHour = hour % 12;
            if (displayHour == 0) {
                displayHour = 12;
            }
            return "%d:%02d %s".formatted(displayHour, minute, suffix);
        }
        return "%02d:%02d".formatted(hour, minute);
    }
}
