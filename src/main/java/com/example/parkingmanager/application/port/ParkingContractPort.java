package com.example.parkingmanager.application.port;

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
import com.example.parkingmanager.adapter.out.parkingcontract.generated.ApiException;
import com.example.parkingmanager.adapter.out.parkingcontract.generated.model.CustomerDto;

public interface ParkingContractPort {

    CustomerDto findCustomer(String tenantName, String businessId) throws ApiException;

    BusinessPartnersApi businessPartners();

    ConsumerApi consumers();

    ConsumerPassApi consumerPasses();

    ContractApi contracts();

    CustomerApi customers();

    FlexibleBookingApi flexibleBookings();

    InvitationApi invitations();

    OperatorApi operators();

    PreBookingApi preBookings();

    ProductUpgradeApi productUpgrades();

    RegistrationApi registrations();

    RegularConsumerApi regularConsumers();

    VisitorConsumerApi visitorConsumers();
}
