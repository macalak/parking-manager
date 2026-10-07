package com.example.parkingmanager.adapter.in.web;

import com.example.parkingmanager.application.ParkingOverviewService;
import com.example.parkingmanager.domain.Facility;
import com.example.parkingmanager.domain.FacilityOverview;
import com.example.parkingmanager.domain.Occupancy;
import com.example.parkingmanager.domain.OpeningHoursEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(
        value = ParkingController.class,
        excludeAutoConfiguration = OAuth2ClientWebSecurityAutoConfiguration.class,
        properties = {
                "spring.security.oauth2.client.registration.parking.client-id=test-client",
                "spring.security.oauth2.client.registration.parking.client-secret=test-secret",
                "spring.security.oauth2.client.provider.parking.token-uri=https://auth.example/token",
                "parking.api.base-url=https://parking.example",
                "parking.api.tenant=test-tenant"
        })
class ParkingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ParkingOverviewService parkingOverviewService;

    @Test
    void rendersSelectedFacilityOverview() throws Exception {
        var facility = new Facility("loc-1", "Central Garage", "Parking Co");
        var anotherFacility = new Facility("loc-2", "North Garage", "Parking Co");
        var overview = new FacilityOverview(
                facility, new Occupancy(100, 40, 60), List.of(new OpeningHoursEntry("Monday", "08:00", "18:00")));
        when(parkingOverviewService.findFacilities()).thenReturn(List.of(anotherFacility, facility));
        when(parkingOverviewService.getOverview("loc-1")).thenReturn(overview);

        mockMvc.perform(get("/parking-manager/").contextPath("/parking-manager").param("facilityId", "loc-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("selectedFacilityId", "loc-1"))
                .andExpect(header().doesNotExist("X-Correlation-Id"));
    }
}
