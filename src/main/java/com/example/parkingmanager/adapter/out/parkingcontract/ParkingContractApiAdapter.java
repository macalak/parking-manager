package com.example.parkingmanager.adapter.out.parkingcontract;

import com.example.parkingmanager.adapter.out.parkingcontract.generated.ApiClient;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.ApiException;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.api.BusinessPartnersApi;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.api.ConsumerApi;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.api.ConsumerPassApi;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.api.ContractApi;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.api.CustomerApi;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.api.FlexibleBookingApi;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.api.InvitationApi;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.api.OperatorApi;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.api.PreBookingApi;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.api.ProductUpgradeApi;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.api.RegistrationApi;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.api.RegularConsumerApi;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.api.VisitorConsumerApi;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.model.CustomerDto;
import com.example.parkingmanager.application.port.ParkingContractPort;
import com.example.parkingmanager.configuration.ParkingContractApiProperties;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ParkingContractApiAdapter implements ParkingContractPort {

    private static final Logger log = LoggerFactory.getLogger(ParkingContractApiAdapter.class);
    private static final String REGISTRATION_ID = "parking";
    private static final String PRINCIPAL_NAME = "parking-manager";
    private static final String OPERATION = "customers-contracts-api";

    private final BusinessPartnersApi businessPartners;
    private final ConsumerApi consumers;
    private final ConsumerPassApi consumerPasses;
    private final ContractApi contracts;
    private final CustomerApi customers;
    private final FlexibleBookingApi flexibleBookings;
    private final InvitationApi invitations;
    private final OperatorApi operators;
    private final PreBookingApi preBookings;
    private final ProductUpgradeApi productUpgrades;
    private final RegistrationApi registrations;
    private final RegularConsumerApi regularConsumers;
    private final VisitorConsumerApi visitorConsumers;

    public ParkingContractApiAdapter(
            OAuth2AuthorizedClientManager authorizedClientManager,
            ParkingContractApiProperties properties) {
        var apiClient = new ApiClient();
        apiClient.setBasePath(properties.baseUrl().toString());
        apiClient.setBearerToken(() -> accessToken(authorizedClientManager));
        apiClient.setHttpClient(apiClient.getHttpClient().newBuilder()
                .addInterceptor(new OAuthLoggingInterceptor())
                .build());
        businessPartners = new BusinessPartnersApi(apiClient);
        consumers = new ConsumerApi(apiClient);
        consumerPasses = new ConsumerPassApi(apiClient);
        contracts = new ContractApi(apiClient);
        customers = new CustomerApi(apiClient);
        flexibleBookings = new FlexibleBookingApi(apiClient);
        invitations = new InvitationApi(apiClient);
        operators = new OperatorApi(apiClient);
        preBookings = new PreBookingApi(apiClient);
        productUpgrades = new ProductUpgradeApi(apiClient);
        registrations = new RegistrationApi(apiClient);
        regularConsumers = new RegularConsumerApi(apiClient);
        visitorConsumers = new VisitorConsumerApi(apiClient);
    }

    private static String accessToken(OAuth2AuthorizedClientManager authorizedClientManager) {
        try {
            var authorizedClient = authorizedClientManager.authorize(OAuth2AuthorizeRequest
                    .withClientRegistrationId(REGISTRATION_ID)
                    .principal(PRINCIPAL_NAME)
                    .build());
            if (authorizedClient == null) {
                logResult("GET", "failure", null);
                throw new ParkingContractApiException("Parking contract API authorization failed");
            }
            return authorizedClient.getAccessToken().getTokenValue();
        } catch (OAuth2AuthorizationException exception) {
            logResult("GET", "failure", null);
            throw new ParkingContractApiException("Parking contract API authorization failed", exception);
        }
    }

    @Override
    public CustomerDto findCustomer(String tenantName, String businessId) throws ApiException {
        return customers.publicGetCustomer(tenantName, businessId);
    }

    @Override
    public BusinessPartnersApi businessPartners() {
        return businessPartners;
    }

    @Override
    public ConsumerApi consumers() {
        return consumers;
    }

    @Override
    public ConsumerPassApi consumerPasses() {
        return consumerPasses;
    }

    @Override
    public ContractApi contracts() {
        return contracts;
    }

    @Override
    public CustomerApi customers() {
        return customers;
    }

    @Override
    public FlexibleBookingApi flexibleBookings() {
        return flexibleBookings;
    }

    @Override
    public InvitationApi invitations() {
        return invitations;
    }

    @Override
    public OperatorApi operators() {
        return operators;
    }

    @Override
    public PreBookingApi preBookings() {
        return preBookings;
    }

    @Override
    public ProductUpgradeApi productUpgrades() {
        return productUpgrades;
    }

    @Override
    public RegistrationApi registrations() {
        return registrations;
    }

    @Override
    public RegularConsumerApi regularConsumers() {
        return regularConsumers;
    }

    @Override
    public VisitorConsumerApi visitorConsumers() {
        return visitorConsumers;
    }

    private static final class OAuthLoggingInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            var request = chain.request();
            try {
                Response response = chain.proceed(request);
                logResult(request.method(), response.isSuccessful() ? "success" : "failure", response.code());
                return response;
            } catch (OAuth2AuthorizationException exception) {
                logResult(request.method(), "failure", null);
                throw new ParkingContractApiException("Parking contract API authorization failed", exception);
            } catch (ParkingContractApiException exception) {
                logResult(request.method(), "failure", null);
                throw exception;
            } catch (IOException exception) {
                logResult(request.method(), "failure", null);
                throw exception;
            }
        }
    }

    private static void logResult(String method, String status, Integer httpStatus) {
        var event = log.atLevel("success".equals(status) ? Level.INFO : Level.ERROR)
                .addKeyValue("facility_id", "not_applicable")
                .addKeyValue("operation", method + " " + OPERATION)
                .addKeyValue("status", status);
        if (httpStatus != null) {
            event.addKeyValue("http_status", httpStatus);
        }
        event.log("Parking contract API request completed");
    }
}
