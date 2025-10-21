package com.tenniscourt;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for Court class.
 */
public class CourtTest {
    
    @Test
    public void testCourt_Creation() {
        Court court = new Court(1);
        
        assertEquals(1, court.getCourtId());
        assertEquals(0, court.getBookings().size());
        assertEquals(0, court.getAvailableFrom());
        assertEquals(0, court.getUsageCount());
    }
    
    @Test
    public void testCourt_CreationWithDurability() {
        Court court = new Court(1, 3);
        
        assertEquals(1, court.getCourtId());
        assertEquals(3, court.getNextMaintenanceAt());
    }
    
    @Test
    public void testCanAccommodate_Available() {
        Court court = new Court(1);
        BookingRecord booking = new BookingRecord(1, 0, 60);
        
        assertTrue(court.canAccommodate(booking));
    }
    
    @Test
    public void testCanAccommodate_NotAvailable() {
        Court court = new Court(1);
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 30, 90);
        
        court.assignBooking(booking1);
        
        assertFalse(court.canAccommodate(booking2));
    }
    
    @Test
    public void testCanAccommodate_AvailableAfterPrevious() {
        Court court = new Court(1);
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 60, 120);
        
        court.assignBooking(booking1);
        
        assertTrue(court.canAccommodate(booking2));
    }
    
    @Test
    public void testAssignBooking_UpdatesState() {
        Court court = new Court(1);
        BookingRecord booking = new BookingRecord(1, 0, 60);
        
        court.assignBooking(booking);
        
        assertEquals(1, court.getBookings().size());
        assertEquals(60, court.getAvailableFrom());
        assertEquals(1, court.getUsageCount());
    }
    
    @Test
    public void testAssignBooking_MultipleBookings() {
        Court court = new Court(1);
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 60, 120);
        
        court.assignBooking(booking1);
        court.assignBooking(booking2);
        
        assertEquals(2, court.getBookings().size());
        assertEquals(120, court.getAvailableFrom());
        assertEquals(2, court.getUsageCount());
    }
    
    @Test
    public void testAssignBookingWithMaintenance() {
        Court court = new Court(1);
        BookingRecord booking = new BookingRecord(1, 0, 60);
        
        court.assignBookingWithMaintenance(booking, 10);
        
        assertEquals(1, court.getBookings().size());
        assertEquals(70, court.getAvailableFrom());
    }
    
    @Test
    public void testNeedsMaintenanceBeforeBooking_NotNeeded() {
        Court court = new Court(1, 3);
        
        assertFalse(court.needsMaintenanceBeforeBooking(3));
    }
    
    @Test
    public void testNeedsMaintenanceBeforeBooking_Needed() {
        Court court = new Court(1, 3);
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 60, 120);
        BookingRecord booking3 = new BookingRecord(3, 120, 180);
        
        court.assignBooking(booking1);
        court.assignBooking(booking2);
        court.assignBooking(booking3);
        
        assertTrue(court.needsMaintenanceBeforeBooking(3));
    }
    
    @Test
    public void testPerformMaintenance() {
        Court court = new Court(1, 3);
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 60, 120);
        BookingRecord booking3 = new BookingRecord(3, 120, 180);
        
        court.assignBooking(booking1);
        court.assignBooking(booking2);
        court.assignBooking(booking3);
        
        court.performMaintenance(30, 3);
        
        assertEquals(210, court.getAvailableFrom());
        assertEquals(3, court.getNextMaintenanceAt());
    }
    
    @Test
    public void testReset() {
        Court court = new Court(1);
        BookingRecord booking = new BookingRecord(1, 0, 60);
        
        court.assignBooking(booking);
        court.reset();
        
        assertEquals(0, court.getBookings().size());
        assertEquals(0, court.getAvailableFrom());
        assertEquals(0, court.getUsageCount());
    }
    
    @Test
    public void testGetBookings_ReturnsDefensiveCopy() {
        Court court = new Court(1);
        BookingRecord booking = new BookingRecord(1, 0, 60);
        
        court.assignBooking(booking);
        
        court.getBookings().clear();
        
        assertEquals(1, court.getBookings().size());
    }
}

