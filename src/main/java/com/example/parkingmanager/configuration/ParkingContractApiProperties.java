package com.example.parkingmanager.configuration;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "parking.contract-api")
public record ParkingContractApiProperties(@NotNull URI baseUrl, @NotBlank String tenant) {
}
