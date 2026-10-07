package com.example.parkingmanager.adapter.in.web;

import com.example.parkingmanager.adapter.out.parkingapi.ParkingApiException;
import com.example.parkingmanager.application.FacilityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ExceptionHandler(FacilityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String facilityNotFound(FacilityNotFoundException exception, Model model) {
        log.atWarn()
                .addKeyValue("facility_id", exception.locationId())
                .addKeyValue("operation", "load-facility-overview")
                .addKeyValue("status", "not-found")
                .log("Requested parking facility was not found");
        model.addAttribute("message", "The selected parking facility could not be found.");
        return "error";
    }

    @ExceptionHandler(ParkingApiException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public String parkingApiFailure(ParkingApiException exception, Model model) {
        model.addAttribute("message", "Parking information is temporarily unavailable.");
        return "error";
    }
}
