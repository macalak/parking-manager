package com.example.parkingmanager.adapter.out.parkingcontract;

import com.example.parkingmanager.configuration.ParkingContractApiProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ParkingContractApiAdapterTest {

    @Test
    void exposesEveryGeneratedApiGroupAndAuthorizesRequests() throws Exception {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse().setResponseCode(204));
            server.start();
            var authorizedClientManager = mock(OAuth2AuthorizedClientManager.class);
            var authorizedClient = mock(OAuth2AuthorizedClient.class);
            when(authorizedClient.getAccessToken()).thenReturn(new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    "test-token",
                    Instant.now(),
                    Instant.now().plusSeconds(60)));
            when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(authorizedClient);

            var adapter = new ParkingContractApiAdapter(
                    authorizedClientManager,
                    new ParkingContractApiProperties(server.url("/customers-contracts").uri(), "demo"));

            assertNotNull(adapter.businessPartners());
            assertNotNull(adapter.consumers());
            assertNotNull(adapter.consumerPasses());
            assertNotNull(adapter.contracts());
            assertNotNull(adapter.customers());
            assertNotNull(adapter.flexibleBookings());
            assertNotNull(adapter.invitations());
            assertNotNull(adapter.operators());
            assertNotNull(adapter.preBookings());
            assertNotNull(adapter.productUpgrades());
            assertNotNull(adapter.registrations());
            assertNotNull(adapter.regularConsumers());
            assertNotNull(adapter.visitorConsumers());

            adapter.preBookings().publicDeletePreBooking("demo", "reservation-1");

            var request = server.takeRequest();
            assertEquals("DELETE", request.getMethod());
            assertEquals("/customers-contracts/v2/demo/pre-bookings/reservation-1", request.getPath());
            assertEquals("Bearer test-token", request.getHeader("Authorization"));
            verify(authorizedClientManager).authorize(any(OAuth2AuthorizeRequest.class));
        }
    }

    @Test
    void retrievesCustomerDetailsFromTheContractApi() throws Exception {
        try (var server = new MockWebServer()) {
            server.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody("""
                            {
                              "businessId": "customer-1",
                              "customerType": "PERSON",
                              "person": {
                                "firstName": "Jane",
                                "lastName": "Doe",
                                "email": "jane@example.test"
                              }
                            }
                            """));
            server.start();
            var authorizedClientManager = mock(OAuth2AuthorizedClientManager.class);
            var authorizedClient = mock(OAuth2AuthorizedClient.class);
            when(authorizedClient.getAccessToken()).thenReturn(new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    "test-token",
                    Instant.now(),
                    Instant.now().plusSeconds(60)));
            when(authorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(authorizedClient);
            var adapter = new ParkingContractApiAdapter(
                    authorizedClientManager,
                    new ParkingContractApiProperties(server.url("/customers-contracts").uri(), "demo"));

            var customer = adapter.findCustomer("demo", "customer-1");

            assertEquals("customer-1", customer.getBusinessId());
            assertEquals("PERSON", customer.getCustomerType().getValue());
            assertEquals("Jane", customer.getPerson().getFirstName());
            assertEquals("jane@example.test", customer.getPerson().getEmail());
            var request = server.takeRequest();
            assertEquals("/customers-contracts/v2/demo/customers/customer-1", request.getPath());
            assertTrue(request.getHeader("Authorization").startsWith("Bearer "));
            assertTrue(request.getHeader("Authorization").length() > "Bearer ".length());
        }
    }
}
