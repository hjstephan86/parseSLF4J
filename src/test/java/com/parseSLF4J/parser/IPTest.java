package com.parseSLF4J.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IPTest {

    private OffsetDateTime dt1;
    private OffsetDateTime dt2;
    private OffsetDateTime dt3;

    @BeforeEach
    void setUp() {
        // Using a fixed ZoneOffset for consistency in tests
        ZoneOffset zoneOffset = ZoneOffset.ofHours(2); // Example: +02:00
        dt1 = OffsetDateTime.of(2023, 1, 1, 10, 0, 0, 0, zoneOffset);
        dt2 = OffsetDateTime.of(2023, 1, 1, 10, 5, 0, 0, zoneOffset);
        dt3 = OffsetDateTime.of(2023, 1, 1, 9, 30, 0, 0, zoneOffset); // Earlier than dt1
    }

    @Test
    @DisplayName("Constructor should correctly initialize IP and date times")
    void testConstructor() {
        IP ip = new IP(dt1, "192.168.1.1");

        assertEquals("192.168.1.1", ip.getIpString(), "IP string should be correctly set");
        assertArrayEquals(new int[] { 192, 168, 1, 1 }, ip.getIPArray(), "IP array should be correctly parsed");
        assertEquals(1, ip.getOccurrence(), "Occurrence should be 1 after initial construction");
        assertTrue(ip.getDateTimes().contains(dt1), "DateTimes set should contain the initial dateTime");
        assertEquals(dt1, ip.getFirstDateTime(), "First dateTime should be the initial dateTime");
    }

    @Test
    @DisplayName("Constructor with invalid IP should result in null IP array")
    void testConstructorInvalidIP() {
        IP ipInvalidFormat = new IP(dt1, "192.168.1"); // Missing octet
        assertNull(ipInvalidFormat.getIPArray(), "IP array should be null for invalid format");

        IP ipInvalidChars = new IP(dt1, "192.168.1.abc"); // Non-numeric octet
        assertNull(ipInvalidChars.getIPArray(), "IP array should be null for non-numeric octet");

        IP ipOutOfRange = new IP(dt1, "256.0.0.1"); // Octet out of range (though parsing as int, then logic would be
                                                    // needed elsewhere for validation)
        // Note: Current setIPArray does not validate if octets are > 255, it only
        // checks if they are integers.
        // If that validation is desired, it should be added to setIPArray.
        assertNotNull(ipOutOfRange.getIPArray(), "IP array should not be null if octets are numbers, even if > 255");
        assertArrayEquals(new int[] { 256, 0, 0, 1 }, ipOutOfRange.getIPArray());
    }

    @Test
    @DisplayName("addDateTime should add new date times and update firstDateTime")
    void testAddDateTime() {
        IP ip = new IP(dt1, "10.0.0.1");

        // Add a later date time
        assertTrue(ip.addDateTime(dt2), "Should return true for adding a new dateTime");
        assertEquals(2, ip.getOccurrence(), "Occurrence should increase");
        assertTrue(ip.getDateTimes().contains(dt2), "DateTimes set should contain the new dateTime");
        assertEquals(dt1, ip.getFirstDateTime(), "First dateTime should remain dt1 as dt2 is later");

        // Add an earlier date time
        assertTrue(ip.addDateTime(dt3), "Should return true for adding an earlier dateTime");
        assertEquals(3, ip.getOccurrence(), "Occurrence should increase again");
        assertTrue(ip.getDateTimes().contains(dt3), "DateTimes set should contain the earlier dateTime");
        assertEquals(dt3, ip.getFirstDateTime(), "First dateTime should be updated to dt3");

        // Add a duplicate date time
        assertFalse(ip.addDateTime(dt1), "Should return false for adding a duplicate dateTime");
        assertEquals(3, ip.getOccurrence(), "Occurrence should not change for duplicate");
    }

    @Test
    @DisplayName("getOccurrence should return the correct count")
    void testGetOccurrence() {
        IP ip = new IP(dt1, "192.168.1.1");
        assertEquals(1, ip.getOccurrence());

        ip.addDateTime(dt2);
        assertEquals(2, ip.getOccurrence());

        ip.addDateTime(dt3);
        assertEquals(3, ip.getOccurrence());
    }

    @Test
    @DisplayName("getFirstDateTime should return the earliest date time")
    void testGetFirstDateTime() {
        IP ip = new IP(dt1, "192.168.1.1");
        assertEquals(dt1, ip.getFirstDateTime());

        ip.addDateTime(dt2); // Later
        assertEquals(dt1, ip.getFirstDateTime());

        ip.addDateTime(dt3); // Earlier
        assertEquals(dt3, ip.getFirstDateTime());
    }

    @Test
    @DisplayName("getDateTimes should return the LinkedHashSet of date times")
    void testGetDateTimes() {
        IP ip = new IP(dt1, "192.168.1.1");
        LinkedHashSet<OffsetDateTime> expectedSet = new LinkedHashSet<>();
        expectedSet.add(dt1);

        assertEquals(expectedSet, ip.getDateTimes(), "Initial dateTimes set should match");

        ip.addDateTime(dt2);
        expectedSet.add(dt2);
        assertEquals(expectedSet, ip.getDateTimes(), "DateTimes set should update correctly");
    }

    @Test
    @DisplayName("getIpString should return the correct IP string")
    void testGetIpString() {
        IP ip = new IP(dt1, "192.168.1.1");
        assertEquals("192.168.1.1", ip.getIpString());

        IP ip2 = new IP(dt1, "10.0.0.255");
        assertEquals("10.0.0.255", ip2.getIpString());
    }

    @Test
    @DisplayName("toString should return the correct formatted string")
    void testToString() {
        IP ip = new IP(dt1, "192.168.1.1");
        assertEquals("192.168.1.1 requested 1 times", ip.toString());

        ip.addDateTime(dt2);
        assertEquals("192.168.1.1 requested 2 times", ip.toString());
    }

    @Test
    @DisplayName("compareTo should correctly order IPs")
    void testCompareTo() {
        IP ip1 = new IP(dt1, "192.168.1.1");
        IP ip2 = new IP(dt1, "192.168.1.10");
        IP ip3 = new IP(dt1, "192.168.2.1");
        IP ip4 = new IP(dt1, "10.0.0.1");
        IP ip5 = new IP(dt1, "192.168.1.1"); // Same as ip1

        // Less than
        assertTrue(ip1.compareTo(ip2) < 0, "192.168.1.1 should be less than 192.168.1.10");
        assertTrue(ip1.compareTo(ip3) < 0, "192.168.1.1 should be less than 192.168.2.1");
        assertTrue(ip4.compareTo(ip1) < 0, "10.0.0.1 should be less than 192.168.1.1");

        // Greater than
        assertTrue(ip2.compareTo(ip1) > 0, "192.168.1.10 should be greater than 192.168.1.1");
        assertTrue(ip3.compareTo(ip1) > 0, "192.168.2.1 should be greater than 192.168.1.1");
        assertTrue(ip1.compareTo(ip4) > 0, "192.168.1.1 should be greater than 10.0.0.1");

        // Equal
        assertEquals(0, ip1.compareTo(ip5), "192.168.1.1 should be equal to 192.168.1.1");
        assertEquals(0, ip1.compareTo(ip1), "IP should be equal to itself");
    }

    @Test
    @DisplayName("getIPArray should return the integer array representation")
    void testGetIPArray() {
        IP ip = new IP(dt1, "192.168.1.1");
        assertArrayEquals(new int[] { 192, 168, 1, 1 }, ip.getIPArray());
    }
}