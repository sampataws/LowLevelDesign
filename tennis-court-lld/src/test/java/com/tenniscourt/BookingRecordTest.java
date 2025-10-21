package com.tenniscourt;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for BookingRecord class.
 */
public class BookingRecordTest {
    
    @Test
    public void testBookingRecord_ValidCreation() {
        BookingRecord booking = new BookingRecord(1, 0, 60);
        
        assertEquals(1, booking.getId());
        assertEquals(0, booking.getStartTime());
        assertEquals(60, booking.getFinishTime());
        assertEquals(60, booking.getDuration());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBookingRecord_StartAfterFinish() {
        new BookingRecord(1, 60, 30);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBookingRecord_StartEqualsFinish() {
        new BookingRecord(1, 60, 60);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBookingRecord_NegativeStartTime() {
        new BookingRecord(1, -10, 60);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBookingRecord_NegativeFinishTime() {
        new BookingRecord(1, 0, -60);
    }
    
    @Test
    public void testConflictsWith_Overlapping() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 30, 90);
        
        assertTrue(booking1.conflictsWith(booking2));
        assertTrue(booking2.conflictsWith(booking1));
    }
    
    @Test
    public void testConflictsWith_NonOverlapping() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 60, 120);
        
        assertFalse(booking1.conflictsWith(booking2));
        assertFalse(booking2.conflictsWith(booking1));
    }
    
    @Test
    public void testConflictsWith_FullyContained() {
        BookingRecord booking1 = new BookingRecord(1, 0, 100);
        BookingRecord booking2 = new BookingRecord(2, 20, 80);
        
        assertTrue(booking1.conflictsWith(booking2));
        assertTrue(booking2.conflictsWith(booking1));
    }
    
    @Test
    public void testConflictsWithMaintenance_NoConflict() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 70, 130);
        
        assertFalse(booking1.conflictsWithMaintenance(booking2, 10));
    }
    
    @Test
    public void testConflictsWithMaintenance_Conflict() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 65, 125);
        
        assertTrue(booking1.conflictsWithMaintenance(booking2, 10));
    }
    
    @Test
    public void testCompareTo_DifferentStartTimes() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 30, 90);
        
        assertTrue(booking1.compareTo(booking2) < 0);
        assertTrue(booking2.compareTo(booking1) > 0);
    }
    
    @Test
    public void testCompareTo_SameStartTime() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 0, 90);
        
        assertTrue(booking1.compareTo(booking2) < 0);
        assertTrue(booking2.compareTo(booking1) > 0);
    }
    
    @Test
    public void testEquals_SameId() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(1, 30, 90);
        
        assertEquals(booking1, booking2);
    }
    
    @Test
    public void testEquals_DifferentId() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 0, 60);
        
        assertNotEquals(booking1, booking2);
    }
    
    @Test
    public void testHashCode_SameId() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(1, 30, 90);
        
        assertEquals(booking1.hashCode(), booking2.hashCode());
    }
    
    @Test
    public void testToString() {
        BookingRecord booking = new BookingRecord(1, 0, 60);
        String str = booking.toString();
        
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("start=0"));
        assertTrue(str.contains("finish=60"));
    }
}

