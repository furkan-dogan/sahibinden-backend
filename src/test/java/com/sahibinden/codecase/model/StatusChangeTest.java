package com.sahibinden.codecase.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class StatusChangeTest {

    @Test
    public void testStatusChangeConstructorAndGetters() {
        LocalDateTime changeTime = LocalDateTime.now();
        StatusChange statusChange = new StatusChange(1, 123, "ACTIVE", changeTime);

        assertEquals(1, statusChange.getId());
        assertEquals(123, statusChange.getAdvertId());
        assertEquals("ACTIVE", statusChange.getStatus());
        assertEquals(changeTime, statusChange.getChangeTime());
    }

    @Test
    public void testStatusChangeSetters() {
        LocalDateTime changeTime = LocalDateTime.now();
        StatusChange statusChange = new StatusChange();

        statusChange.setId(1);
        statusChange.setAdvertId(123);
        statusChange.setStatus("ACTIVE");
        statusChange.setChangeTime(changeTime);

        assertEquals(1, statusChange.getId());
        assertEquals(123, statusChange.getAdvertId());
        assertEquals("ACTIVE", statusChange.getStatus());
        assertEquals(changeTime, statusChange.getChangeTime());
    }

    @Test
    public void testNoArgsConstructor() {
        StatusChange statusChange = new StatusChange();

        assertNotNull(statusChange);
        assertNull(statusChange.getId());
    }
}
