package com.example.parkingmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.parking.client-id=test-client",
        "spring.security.oauth2.client.registration.parking.client-secret=test-secret",
        "spring.security.oauth2.client.provider.parking.token-uri=https://auth.example/token",
        "parking.api.base-url=https://parking.example",
        "parking.api.tenant=test-tenant"
})
class ParkingManagerApplicationTest {

    @Test
    void loadsApplicationContext() {
    }
}
