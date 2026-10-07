package com.example.parkingmanager.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "parking.api")
public record ParkingApiProperties(@NotNull URI baseUrl, @NotBlank String tenant) {
}
