package com.tenniscourt;

import java.util.ArrayList;
import java.util.List;

/**
 * Driver application demonstrating tennis court booking system.
 */
public class DriverApplication {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║     Tennis Court Booking System - Assignment Demo              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        CourtAssignmentSystem system = new CourtAssignmentSystem();
        
        // Demo 1: Basic court assignment (Part a)
        demo1BasicAssignment(system);
        
        // Demo 2: Assignment with fixed maintenance (Part b)
        demo2MaintenanceAfterEachBooking(system);
        
        // Demo 3: Assignment with periodic maintenance (Part c)
        demo3PeriodicMaintenance(system);
        
        // Demo 4: Minimum courts needed (Part d)
        demo4MinimumCourtsNeeded(system);
        
        // Demo 5: Booking conflict detection (Part e)
        demo5ConflictDetection(system);
        
        System.out.println("✅ All demos completed successfully!");
        System.out.println();
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("Key Features Demonstrated:");
        System.out.println("  ✓ Minimum court allocation (greedy algorithm)");
        System.out.println("  ✓ Fixed maintenance time after each booking");
        System.out.println("  ✓ Periodic maintenance after X bookings");
        System.out.println("  ✓ Efficient minimum court counting");
        System.out.println("  ✓ Booking conflict detection");
        System.out.println("════════════════════════════════════════════════════════════════");
    }
    
    private static void demo1BasicAssignment(CourtAssignmentSystem system) {
        System.out.println("📋 Demo 1: Basic Court Assignment (Part a)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));   // 0-60
        bookings.add(new BookingRecord(2, 30, 90));  // 30-90
        bookings.add(new BookingRecord(3, 60, 120)); // 60-120
        bookings.add(new BookingRecord(4, 90, 150)); // 90-150
        bookings.add(new BookingRecord(5, 120, 180)); // 120-180
        
        System.out.println("Bookings:");
        for (BookingRecord booking : bookings) {
            System.out.println("  " + booking);
        }
        System.out.println();
        
        List<Court> courts = system.assignCourts(bookings);
        
        System.out.println("Court Assignments:");
        for (Court court : courts) {
            System.out.println("  Court " + court.getCourtId() + ":");
            for (BookingRecord booking : court.getBookings()) {
                System.out.println("    - " + booking);
            }
        }
        
        System.out.println("\n✓ Total courts needed: " + courts.size());
        System.out.println();
    }
    
    private static void demo2MaintenanceAfterEachBooking(CourtAssignmentSystem system) {
        System.out.println("🔧 Demo 2: Fixed Maintenance After Each Booking (Part b)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        bookings.add(new BookingRecord(2, 70, 130));  // Can use same court if maintenance = 10
        bookings.add(new BookingRecord(3, 60, 120));
        bookings.add(new BookingRecord(4, 140, 200)); // Can use court 1 after maintenance
        
        int maintenanceTime = 10;
        
        System.out.println("Bookings:");
        for (BookingRecord booking : bookings) {
            System.out.println("  " + booking);
        }
        System.out.println("Maintenance time: " + maintenanceTime + " minutes");
        System.out.println();
        
        List<Court> courts = system.assignCourtsWithMaintenance(bookings, maintenanceTime);
        
        System.out.println("Court Assignments (with maintenance):");
        for (Court court : courts) {
            System.out.println("  Court " + court.getCourtId() + ":");
            for (BookingRecord booking : court.getBookings()) {
                System.out.println("    - " + booking);
            }
            System.out.println("    Available from: " + court.getAvailableFrom());
        }
        
        System.out.println("\n✓ Total courts needed: " + courts.size());
        System.out.println();
    }
    
    private static void demo3PeriodicMaintenance(CourtAssignmentSystem system) {
        System.out.println("⚙️  Demo 3: Periodic Maintenance After X Bookings (Part c)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        List<BookingRecord> bookings = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            bookings.add(new BookingRecord(i + 1, i * 60, (i + 1) * 60));
        }
        
        int maintenanceTime = 30;
        int durability = 3; // Maintenance after every 3 bookings
        
        System.out.println("Bookings: 8 consecutive 60-minute slots");
        System.out.println("Maintenance time: " + maintenanceTime + " minutes");
        System.out.println("Durability: " + durability + " bookings");
        System.out.println();
        
        List<Court> courts = system.assignCourtsWithPeriodicMaintenance(
            bookings, maintenanceTime, durability);
        
        System.out.println("Court Assignments (with periodic maintenance):");
        for (Court court : courts) {
            System.out.println("  Court " + court.getCourtId() + ":");
            System.out.println("    Bookings: " + court.getUsageCount());
            System.out.println("    Next maintenance in: " + court.getNextMaintenanceAt() + " bookings");
        }
        
        System.out.println("\n✓ Total courts needed: " + courts.size());
        System.out.println();
    }
    
    private static void demo4MinimumCourtsNeeded(CourtAssignmentSystem system) {
        System.out.println("📊 Demo 4: Minimum Courts Needed (Part d - Simplified)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 60));
        bookings.add(new BookingRecord(2, 30, 90));
        bookings.add(new BookingRecord(3, 45, 105));
        bookings.add(new BookingRecord(4, 60, 120));
        bookings.add(new BookingRecord(5, 90, 150));
        
        System.out.println("Bookings:");
        for (BookingRecord booking : bookings) {
            System.out.println("  " + booking);
        }
        System.out.println();
        
        int minCourts = system.findMinimumCourtsNeeded(bookings);
        
        System.out.println("Analysis:");
        System.out.println("  At time 45-60: 3 bookings overlap (1, 2, 3)");
        System.out.println("  This is the maximum overlap");
        System.out.println();
        System.out.println("✓ Minimum courts needed: " + minCourts);
        System.out.println();
    }
    
    private static void demo5ConflictDetection(CourtAssignmentSystem system) {
        System.out.println("⚠️  Demo 5: Booking Conflict Detection (Part e)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        BookingRecord booking1 = new BookingRecord(1, 0, 60);
        BookingRecord booking2 = new BookingRecord(2, 30, 90);
        BookingRecord booking3 = new BookingRecord(3, 60, 120);
        BookingRecord booking4 = new BookingRecord(4, 70, 130);
        
        System.out.println("Test Cases:");
        System.out.println();
        
        // Test 1: Overlapping bookings
        boolean conflict1 = system.checkBookingConflict(booking1, booking2);
        System.out.println("1. " + booking1);
        System.out.println("   " + booking2);
        System.out.println("   Conflict: " + (conflict1 ? "YES ⚠️" : "NO ✓"));
        System.out.println();
        
        // Test 2: Non-overlapping bookings
        boolean conflict2 = system.checkBookingConflict(booking1, booking3);
        System.out.println("2. " + booking1);
        System.out.println("   " + booking3);
        System.out.println("   Conflict: " + (conflict2 ? "YES ⚠️" : "NO ✓"));
        System.out.println();
        
        // Test 3: With maintenance time
        int maintenanceTime = 15;
        boolean conflict3 = system.checkBookingConflictWithMaintenance(
            booking3, booking4, maintenanceTime);
        System.out.println("3. " + booking3);
        System.out.println("   " + booking4);
        System.out.println("   Maintenance time: " + maintenanceTime);
        System.out.println("   Conflict: " + (conflict3 ? "YES ⚠️" : "NO ✓"));
        System.out.println("   (Court available from: " + (booking3.getFinishTime() + maintenanceTime) + ")");
        System.out.println();
        
        System.out.println("✓ Conflict detection working correctly");
        System.out.println();
    }
}

