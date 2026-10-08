package com.example.parkingmanager.application;

import com.example.parkingmanager.adapter.out.parkingcontract.generated.ApiException;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.model.CustomerDto;
import com.example.parkingmanager.application.port.ParkingContractPort;
import com.example.parkingmanager.configuration.ParkingContractApiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomerDetailsService.class);

    private final ParkingContractPort parkingContractPort;
    private final ParkingContractApiProperties properties;

    public CustomerDetailsService(
            ParkingContractPort parkingContractPort,
            ParkingContractApiProperties properties) {
        this.parkingContractPort = parkingContractPort;
        this.properties = properties;
    }

    public CustomerDto getCustomer(String businessId) {
        try {
            CustomerDto customer = parkingContractPort.findCustomer(properties.tenant(), businessId);
            if (customer == null) {
                logFailure(businessId);
                throw new CustomerApiException(businessId, new IllegalStateException("Customer API returned no data"));
            }
            log.atInfo()
                    .addKeyValue("customer_id", businessId)
                    .addKeyValue("operation", "load-customer-details")
                    .addKeyValue("status", "success")
                    .log("Loaded customer details");
            return customer;
        } catch (ApiException exception) {
            if (exception.getCode() == 404) {
                throw new CustomerNotFoundException(businessId);
            }
            logFailure(businessId);
            throw new CustomerApiException(businessId, exception);
        }
    }

    private void logFailure(String businessId) {
        log.atError()
                .addKeyValue("customer_id", businessId)
                .addKeyValue("operation", "load-customer-details")
                .addKeyValue("status", "failure")
                .log("Failed to load customer details");
    }
}
