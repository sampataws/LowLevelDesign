package com.tenniscourt;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for Tennis Court Assignment System.
 */
public class CourtAssignmentSystemTest {
    
    private CourtAssignmentSystem system;
    
    @Before
    public void setUp() {
        system = new CourtAssignmentSystem();
    }
    
    // ==================== Part (a): Basic Court Assignment ====================
    
    @Test
    public void testBasicAssignment_SingleBooking() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        
        List<Court> courts = system.assignCourts(bookings);
        
        assertEquals(1, courts.size());
        assertEquals(1, courts.get(0).getBookings().size());
    }
    
    @Test
    public void testBasicAssignment_NonOverlappingBookings() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        bookings.add(new BookingRecord(2, 60, 120));
        bookings.add(new BookingRecord(3, 120, 180));
        
        List<Court> courts = system.assignCourts(bookings);
        
        assertEquals(1, courts.size());
        assertEquals(3, courts.get(0).getBookings().size());
    }
    
    @Test
    public void testBasicAssignment_OverlappingBookings() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        bookings.add(new BookingRecord(2, 30, 90));
        bookings.add(new BookingRecord(3, 60, 120));
        
        List<Court> courts = system.assignCourts(bookings);
        
        assertEquals(2, courts.size());
    }
    
    @Test
    public void testBasicAssignment_MaximumOverlap() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 100));
        bookings.add(new BookingRecord(2, 10, 90));
        bookings.add(new BookingRecord(3, 20, 80));
        bookings.add(new BookingRecord(4, 30, 70));
        
        List<Court> courts = system.assignCourts(bookings);
        
        assertEquals(4, courts.size());
    }
    
    @Test
    public void testBasicAssignment_UnsortedInput() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 60, 120));
        bookings.add(new BookingRecord(2, 0, 60));
        bookings.add(new BookingRecord(3, 120, 180));
        
        List<Court> courts = system.assignCourts(bookings);
        
        assertEquals(1, courts.size());
        assertEquals(3, courts.get(0).getBookings().size());
    }
    
    @Test
    public void testBasicAssignment_EmptyList() {
        List<BookingRecord> bookings = new ArrayList<>();
        
        List<Court> courts = system.assignCourts(bookings);
        
        assertEquals(0, courts.size());
    }
    
    @Test
    public void testBasicAssignment_NullList() {
        List<Court> courts = system.assignCourts(null);
        
        assertEquals(0, courts.size());
    }
    
    // ==================== Part (b): Fixed Maintenance ====================
    
    @Test
    public void testMaintenanceAfterEachBooking_NoMaintenance() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        bookings.add(new BookingRecord(2, 60, 120));
        
        List<Court> courts = system.assignCourtsWithMaintenance(bookings, 0);
        
        assertEquals(1, courts.size());
    }
    
    @Test
    public void testMaintenanceAfterEachBooking_WithMaintenance() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        bookings.add(new BookingRecord(2, 70, 130)); // Can use same court with 10 min maintenance

        List<Court> courts = system.assignCourtsWithMaintenance(bookings, 10);

        assertEquals(1, courts.size());
        assertEquals(140, courts.get(0).getAvailableFrom()); // 130 (finish) + 10 (maintenance)
    }
    
    @Test
    public void testMaintenanceAfterEachBooking_NeedsTwoCourts() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        bookings.add(new BookingRecord(2, 65, 125)); // Too soon, needs second court
        
        List<Court> courts = system.assignCourtsWithMaintenance(bookings, 10);
        
        assertEquals(2, courts.size());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMaintenanceAfterEachBooking_NegativeMaintenance() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        
        system.assignCourtsWithMaintenance(bookings, -5);
    }
    
    // ==================== Part (c): Periodic Maintenance ====================
    
    @Test
    public void testPeriodicMaintenance_BelowDurability() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        bookings.add(new BookingRecord(2, 60, 120));
        
        List<Court> courts = system.assignCourtsWithPeriodicMaintenance(bookings, 30, 3);
        
        assertEquals(1, courts.size());
        assertEquals(2, courts.get(0).getUsageCount());
        assertEquals(1, courts.get(0).getNextMaintenanceAt());
    }
    
    @Test
    public void testPeriodicMaintenance_ExactlyDurability() {
        List<BookingRecord> bookings = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            bookings.add(new BookingRecord(i + 1, i * 60, (i + 1) * 60));
        }
        
        List<Court> courts = system.assignCourtsWithPeriodicMaintenance(bookings, 30, 3);
        
        assertEquals(1, courts.size());
        assertEquals(3, courts.get(0).getUsageCount());
        assertEquals(0, courts.get(0).getNextMaintenanceAt());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPeriodicMaintenance_InvalidDurability() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        
        system.assignCourtsWithPeriodicMaintenance(bookings, 30, 0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPeriodicMaintenance_NegativeMaintenance() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        
        system.assignCourtsWithPeriodicMaintenance(bookings, -10, 3);
    }
    
    // ==================== Part (d): Minimum Courts Needed ====================
    
    @Test
    public void testMinimumCourtsNeeded_SingleBooking() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        
        int minCourts = system.findMinimumCourtsNeeded(bookings);
        
        assertEquals(1, minCourts);
    }
    
    @Test
    public void testMinimumCourtsNeeded_NonOverlapping() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        bookings.add(new BookingRecord(2, 60, 120));
        bookings.add(new BookingRecord(3, 120, 180));
        
        int minCourts = system.findMinimumCourtsNeeded(bookings);
        
        assertEquals(1, minCourts);
    }
    
    @Test
    public void testMinimumCourtsNeeded_TwoOverlapping() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        bookings.add(new BookingRecord(2, 30, 90));
        
        int minCourts = system.findMinimumCourtsNeeded(bookings);
        
        assertEquals(2, minCourts);
    }
    
    @Test
    public void testMinimumCourtsNeeded_ThreeOverlapping() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        bookings.add(new BookingRecord(2, 30, 90));
        bookings.add(new BookingRecord(3, 45, 105));
        
        int minCourts = system.findMinimumCourtsNeeded(bookings);
        
        assertEquals(3, minCourts);
    }
    
    @Test
    public void testMinimumCourtsNeeded_ComplexScenario() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 100));
        bookings.add(new BookingRecord(2, 50, 150));
        bookings.add(new BookingRecord(3, 100, 200));
        bookings.add(new BookingRecord(4, 150, 250));
        
        int minCourts = system.findMinimumCourtsNeeded(bookings);
        
        assertEquals(2, minCourts);
    }
    
    @Test
    public void testMinimumCourtsNeeded_SameStartTime() {
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        bookings.add(new BookingRecord(2, 0, 90));
        bookings.add(new BookingRecord(3, 0, 120));
        
        int minCourts = system.findMinimumCourtsNeeded(bookings);
        
        assertEquals(3, minCourts);
    }
    
    @Test
    public void testMinimumCourtsNeeded_EmptyList() {
        List<BookingRecord> bookings = new ArrayList<>();
        
        int minCourts = system.findMinimumCourtsNeeded(bookings);
        
        assertEquals(0, minCourts);
    }
    
    @Test
    public void testMinimumCourtsNeeded_NullList() {
        int minCourts = system.findMinimumCourtsNeeded(null);
        
        assertEquals(0, minCourts);
    }
    
    // ==================== Part (e): Conflict Detection ====================
    
    @Test
    public void testConflictDetection_Overlapping() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 30, 90);
        
        boolean conflict = system.checkBookingConflict(booking1, booking2);
        
        assertTrue(conflict);
    }
    
    @Test
    public void testConflictDetection_NonOverlapping() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 60, 120);
        
        boolean conflict = system.checkBookingConflict(booking1, booking2);
        
        assertFalse(conflict);
    }
    
    @Test
    public void testConflictDetection_PartialOverlap() {
        BookingRecord booking1 = new BookingRecord(1, 0, 100);
        BookingRecord booking2 = new BookingRecord(2, 50, 150);
        
        boolean conflict = system.checkBookingConflict(booking1, booking2);
        
        assertTrue(conflict);
    }
    
    @Test
    public void testConflictDetection_FullyContained() {
        BookingRecord booking1 = new BookingRecord(1, 0, 100);
        BookingRecord booking2 = new BookingRecord(2, 20, 80);
        
        boolean conflict = system.checkBookingConflict(booking1, booking2);
        
        assertTrue(conflict);
    }
    
    @Test
    public void testConflictDetection_WithMaintenance_NoConflict() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 70, 130);
        
        boolean conflict = system.checkBookingConflictWithMaintenance(booking1, booking2, 10);
        
        assertFalse(conflict);
    }
    
    @Test
    public void testConflictDetection_WithMaintenance_Conflict() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 65, 125);
        
        boolean conflict = system.checkBookingConflictWithMaintenance(booking1, booking2, 10);
        
        assertTrue(conflict);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConflictDetection_NullBooking1() {
        BookingRecord booking2 = new BookingRecord(2, 60, 120);
        
        system.checkBookingConflict(null, booking2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConflictDetection_NullBooking2() {
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        
        system.checkBookingConflict(booking1, null);
    }
}

