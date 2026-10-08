package com.example.parkingmanager.application;

import com.example.parkingmanager.adapter.out.parkingcontract.generated.ApiException;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.model.CustomerDto;
import com.example.parkingmanager.application.port.ParkingContractPort;
import com.example.parkingmanager.configuration.ParkingContractApiProperties;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerDetailsServiceTest {

    private final ParkingContractPort parkingContractPort = mock(ParkingContractPort.class);
    private final CustomerDetailsService service = new CustomerDetailsService(
            parkingContractPort,
            new ParkingContractApiProperties(URI.create("https://parking.example"), "demo"));

    @Test
    void loadsCustomerUsingConfiguredTenant() throws Exception {
        var customer = new CustomerDto("customer-1", null, null, false);
        when(parkingContractPort.findCustomer("demo", "customer-1")).thenReturn(customer);

        assertSame(customer, service.getCustomer("customer-1"));
        verify(parkingContractPort).findCustomer("demo", "customer-1");
    }

    @Test
    void mapsMissingCustomersToNotFound() throws Exception {
        when(parkingContractPort.findCustomer("demo", "missing"))
                .thenThrow(new ApiException(404, "Not found"));

        var exception = assertThrows(CustomerNotFoundException.class, () -> service.getCustomer("missing"));

        assertEquals("missing", exception.businessId());
    }

    @Test
    void reportsOtherApiFailures() throws Exception {
        when(parkingContractPort.findCustomer("demo", "customer-1"))
                .thenThrow(new ApiException(503, "Unavailable"));

        var exception = assertThrows(CustomerApiException.class, () -> service.getCustomer("customer-1"));

        assertEquals("customer-1", exception.businessId());
    }
}
