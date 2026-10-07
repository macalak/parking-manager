package com.example.parkingmanager.adapter.out.parkingapi;

import com.example.parkingmanager.application.port.ParkingFacilityPort;
import com.example.parkingmanager.configuration.ParkingApiProperties;
import com.example.parkingmanager.adapter.out.parkingapi.generated.model.PublicGetFacilities200ResponseEconnectDto;
import com.example.parkingmanager.adapter.out.parkingapi.generated.model.PublicGetOpeningHoursByLocationId200ResponseEconnectDto;
import com.example.parkingmanager.adapter.out.parkingapi.generated.model.PublicGetStandardCountersByLocationId200ResponseEconnectDto;
import com.example.parkingmanager.domain.Facility;
import com.example.parkingmanager.domain.Occupancy;
import com.example.parkingmanager.domain.OpeningHoursEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
public class ParkingApiAdapter implements ParkingFacilityPort {

    private static final Logger log = LoggerFactory.getLogger(ParkingApiAdapter.class);
    private static final String REGISTRATION_ID = "parking";
    private static final String PRINCIPAL_NAME = "parking-manager";

    private final RestClient restClient;
    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final ParkingApiProperties properties;
    private final ParkingApiMapper mapper;

    public ParkingApiAdapter(
            RestClient.Builder restClientBuilder,
            OAuth2AuthorizedClientManager authorizedClientManager,
            ParkingApiProperties properties,
            ParkingApiMapper mapper) {
        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl().toString())
                .requestInterceptor((request, requestBody, execution) -> {
                    logRawRequest(request, requestBody);
                    ClientHttpResponse response = execution.execute(request, requestBody);
                    byte[] responseBody;
                    try {
                        responseBody = response.getBody().readAllBytes();
                    } catch (IOException exception) {
                        response.close();
                        throw exception;
                    }
                    logRawResponse(request, response, responseBody);
                    return new BufferedClientHttpResponse(response, responseBody);
                })
                .build();
        this.authorizedClientManager = authorizedClientManager;
        this.properties = properties;
        this.mapper = mapper;
    }

    @Override
    public List<Facility> findFacilities() {
        var response = get(
                "list-facilities",
                "all",
                "/v2/{tenant}/occupancy/facilities",
                PublicGetFacilities200ResponseEconnectDto.class,
                properties.tenant());
        return mapper.toFacilities(response);
    }

    @Override
    public Optional<Occupancy> findOccupancy(String locationId) {
        var response = get(
                "load-facility-occupancy",
                locationId,
                "/v2/{tenant}/occupancy/standardcounters/{locationid}",
                PublicGetStandardCountersByLocationId200ResponseEconnectDto.class,
                properties.tenant(), locationId);
        return mapper.toOccupancy(response);
    }

    @Override
    public List<OpeningHoursEntry> findOpeningHours(String locationId) {
        var response = get(
                "load-facility-opening-hours",
                locationId,
                "/v2/{tenant}/occupancy/openinghours/{locationid}",
                PublicGetOpeningHoursByLocationId200ResponseEconnectDto.class,
                properties.tenant(), locationId);
        return mapper.toOpeningHours(response);
    }

    private <T> T get(String operation, String facilityId, String path, Class<T> responseType, Object... uriVariables) {
        try {
            var authorizedClient = authorizedClientManager.authorize(OAuth2AuthorizeRequest
                    .withClientRegistrationId(REGISTRATION_ID)
                    .principal(PRINCIPAL_NAME)
                    .build());
            if (authorizedClient == null) {
                throw new ParkingApiException("Parking API authorization failed");
            }
            T response = restClient.get()
                    .uri(path, uriVariables)
                    .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION,
                            "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                    .retrieve()
                    .body(responseType);
            if (response == null) {
                throw new ParkingApiException("Parking API returned an empty response");
            }
            logApiResult(operation, facilityId, "success", null);
            return response;
        } catch (RestClientResponseException exception) {
            logApiResult(operation, facilityId, "failure", exception.getStatusCode().value());
            throw new ParkingApiException(
                    "Parking API returned HTTP status " + exception.getStatusCode().value(), exception);
        } catch (RestClientException | OAuth2AuthorizationException exception) {
            logApiResult(operation, facilityId, "failure", null);
            throw new ParkingApiException("Parking API request failed", exception);
        } catch (ParkingApiException exception) {
            logApiResult(operation, facilityId, "failure", null);
            throw exception;
        }
    }

    private void logApiResult(String operation, String facilityId, String status, Integer httpStatus) {
        var event = log.atLevel("success".equals(status) ? Level.INFO : Level.ERROR)
                .addKeyValue("facility_id", facilityId)
                .addKeyValue("operation", operation)
                .addKeyValue("status", status);
        if (httpStatus != null) {
            event.addKeyValue("http_status", httpStatus);
        }
        event.log("Parking API request completed");
    }

    private void logRawRequest(HttpRequest request, byte[] body) {
        log.atInfo()
                .addKeyValue("http_direction", "request")
                .addKeyValue("http_method", request.getMethod().name())
                .addKeyValue("http_uri", request.getURI())
                .addKeyValue("http_headers", safeHeaders(request.getHeaders()))
                .addKeyValue("http_body", new String(body, StandardCharsets.UTF_8))
                .log("Raw Parking API HTTP request");
    }

    private void logRawResponse(
            HttpRequest request, ClientHttpResponse response, byte[] body) throws IOException {
        log.atInfo()
                .addKeyValue("http_direction", "response")
                .addKeyValue("http_method", request.getMethod().name())
                .addKeyValue("http_uri", request.getURI())
                .addKeyValue("http_status", response.getStatusCode().value())
                .addKeyValue("http_headers", safeHeaders(response.getHeaders()))
                .addKeyValue("http_body", new String(body, StandardCharsets.UTF_8))
                .log("Raw Parking API HTTP response");
    }

    private HttpHeaders safeHeaders(HttpHeaders headers) {
        var safeHeaders = new HttpHeaders();
        headers.forEach((name, values) -> {
            if (!name.equalsIgnoreCase(HttpHeaders.AUTHORIZATION)
                    && !name.equalsIgnoreCase(HttpHeaders.PROXY_AUTHORIZATION)
                    && !name.equalsIgnoreCase(HttpHeaders.COOKIE)
                    && !name.equalsIgnoreCase(HttpHeaders.SET_COOKIE)) {
                safeHeaders.put(name, values);
            }
        });
        return safeHeaders;
    }

    private static final class BufferedClientHttpResponse implements ClientHttpResponse {

        private final ClientHttpResponse delegate;
        private final byte[] body;

        private BufferedClientHttpResponse(ClientHttpResponse delegate, byte[] body) {
            this.delegate = delegate;
            this.body = body;
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return delegate.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return delegate.getStatusText();
        }

        @Override
        public org.springframework.http.HttpHeaders getHeaders() {
            return delegate.getHeaders();
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public void close() {
            delegate.close();
        }
    }
}
