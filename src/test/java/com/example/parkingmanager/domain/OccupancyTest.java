package com.example.parkingmanager.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class OccupancyTest {

    @Test
    void rejectsNegativeCapacityValues() {
        assertThrows(IllegalArgumentException.class, () -> new Occupancy(10, -1, 11));
    }
}
