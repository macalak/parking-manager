package com.example.parkingmanager.adapter.out.parkingapi;

import com.example.parkingmanager.configuration.ParkingApiProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import org.springframework.http.HttpMethod;

class ParkingApiAdapterTest {

    @Test
    void loadsFacilityDtoWithOAuthBearerToken() {
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var authorizedClientManager = mock(OAuth2AuthorizedClientManager.class);
        var authorizedClient = mock(OAuth2AuthorizedClient.class);
        var accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "test-token", Instant.now(), Instant.now().plusSeconds(60));
        when(authorizedClient.getAccessToken()).thenReturn(accessToken);
        when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(authorizedClient);
        server.expect(requestTo("https://parking.example/v2/demo/occupancy/facilities"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-token"))
                .andRespond(withSuccess("""
                        {"facility":[{"facility-name":"Central Garage",
                        "business-partner-name":"Parking Co","location-id":"loc-1"}]}
                        """, MediaType.APPLICATION_JSON));

        var adapter = new ParkingApiAdapter(
                builder, authorizedClientManager,
                new ParkingApiProperties(URI.create("https://parking.example"), "demo"), new ParkingApiMapper());

        var facilities = adapter.findFacilities();

        assertEquals(1, facilities.size());
        assertEquals("Central Garage", facilities.getFirst().name());
        assertEquals("Parking Co", facilities.getFirst().businessPartnerName());
        server.verify();
    }

    @Test
    void selectsThePhysicalFacilityCounter() {
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var authorizedClientManager = mock(OAuth2AuthorizedClientManager.class);
        var authorizedClient = mock(OAuth2AuthorizedClient.class);
        when(authorizedClient.getAccessToken()).thenReturn(new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "test-token", Instant.now(), Instant.now().plusSeconds(60)));
        when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(authorizedClient);
        server.expect(requestTo("https://parking.example/v2/demo/occupancy/standardcounters/loc-1"))
                .andRespond(withSuccess("""
                        {"counters":{"standard-counter":[
                          {"id":1,"present":25,"max":80,"free":55},
                          {"id":3,"present":40,"max":100,"free":60}]}}
                        """, MediaType.APPLICATION_JSON));

        var adapter = new ParkingApiAdapter(
                builder, authorizedClientManager,
                new ParkingApiProperties(URI.create("https://parking.example"), "demo"), new ParkingApiMapper());

        var occupancy = adapter.findOccupancy("loc-1").orElseThrow();

        assertEquals(100, occupancy.maximumPlaces());
        assertEquals(40, occupancy.occupiedPlaces());
        assertEquals(60, occupancy.freePlaces());
        server.verify();
    }

    @Test
    void loadsAndMapsOpeningHours() {
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var authorizedClientManager = mock(OAuth2AuthorizedClientManager.class);
        var authorizedClient = mock(OAuth2AuthorizedClient.class);
        when(authorizedClient.getAccessToken()).thenReturn(new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "test-token", Instant.now(), Instant.now().plusSeconds(60)));
        when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(authorizedClient);
        server.expect(requestTo("https://parking.example/v2/demo/occupancy/openinghours/loc-1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"opening-hours":{"facility-no":1,"open-hour":[
                          {"weekday":"Monday","time-format":"24h","hour-open":8,"minutes-open":0,
                           "hour-closing":18,"minutes-closing":30}]}}
                        """, MediaType.APPLICATION_JSON));

        var adapter = new ParkingApiAdapter(
                builder, authorizedClientManager,
                new ParkingApiProperties(URI.create("https://parking.example"), "demo"), new ParkingApiMapper());

        var openingHours = adapter.findOpeningHours("loc-1");

        assertEquals("Monday", openingHours.getFirst().weekday());
        assertEquals("08:00", openingHours.getFirst().opensAt());
        assertEquals("18:30", openingHours.getFirst().closesAt());
        server.verify();
    }
}
