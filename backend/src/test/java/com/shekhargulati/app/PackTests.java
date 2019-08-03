package com.shekhargulati.app;

import com.shekhargulati.pack.Pack;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PackTests {

    @Test
    public void testCrawlerDefaultInitialization() {
        Pack pack = new Pack.Builder("123", "south road", "150").build();
        assertEquals("123", pack.getID());
        assertEquals("150", pack.getRent());
        assertNull(pack.getPostDate());
        assertNull(pack.getDescription());
        assertEquals("south road", pack.getAddress());
        assertNull(pack.getLatitude());
        assertNull(pack.getLongitude());
    }

    @Test
    public void testCrawlerFullInitialization() {
        Pack pack = new Pack.Builder("123", "south road", "150")
                .setDescription("good")
                .setDate("1999")
                .build();
        assertEquals("123", pack.getID());
        assertEquals("150", pack.getRent());
        assertEquals("1999", pack.getPostDate());
        assertEquals("good", pack.getDescription());
        assertEquals("south road", pack.getAddress());
        assertNull(pack.getLatitude());
        assertNull(pack.getLongitude());
    }

    @Test
    public void testCrawlerInitNullDescription() {
        Pack pack = new Pack.Builder("1233", "south road", "150")
                .setDate("19992")
                .build();
        assertEquals("1233", pack.getID());
        assertEquals("150", pack.getRent());
        assertEquals("19992", pack.getPostDate());
        assertEquals("south road", pack.getAddress());
        assertNull(pack.getDescription());
        assertNull(pack.getLatitude());
        assertNull(pack.getLongitude());
    }

    @Test
    public void testCrawlerInitNullDate() {
        Pack pack = new Pack.Builder("1123", "sout2h road", "150")
                .setDescription("goooooood")
                .build();
        assertEquals("1123", pack.getID());
        assertEquals("150", pack.getRent());
        assertEquals("goooooood", pack.getDescription());
        assertEquals("sout2h road", pack.getAddress());
        assertNull(pack.getPostDate());
        assertNull(pack.getLatitude());
        assertNull(pack.getLongitude());
    }

}
