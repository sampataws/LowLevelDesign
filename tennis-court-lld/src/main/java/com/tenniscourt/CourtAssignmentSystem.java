package com.tenniscourt;

import java.util.*;

/**
 * Main system for assigning tennis court bookings to courts.
 * Implements various strategies for court assignment.
 */
public class CourtAssignmentSystem {
    
    /**
     * Part (a): Assigns bookings to courts using minimum number of courts.
     * Uses greedy algorithm with priority queue.
     * 
     * Algorithm:
     * 1. Sort bookings by start time
     * 2. Use min-heap to track when each court becomes available
     * 3. For each booking, assign to earliest available court
     * 4. If no court available, create new court
     * 
     * Time Complexity: O(N log N) where N is number of bookings
     * Space Complexity: O(K) where K is number of courts needed
     */
    public List<Court> assignCourts(List<BookingRecord> bookingRecords) {
        if (bookingRecords == null || bookingRecords.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Sort bookings by start time
        List<BookingRecord> sortedBookings = new ArrayList<>(bookingRecords);
        Collections.sort(sortedBookings);
        
        // Min-heap to track courts by their available time
        PriorityQueue<Court> availableCourts = new PriorityQueue<>(
            Comparator.comparingInt(Court::getAvailableFrom)
        );
        
        List<Court> allCourts = new ArrayList<>();
        int courtIdCounter = 1;
        
        for (BookingRecord booking : sortedBookings) {
            Court assignedCourt = null;
            
            // Check if any existing court is available
            if (!availableCourts.isEmpty() && 
                availableCourts.peek().canAccommodate(booking)) {
                assignedCourt = availableCourts.poll();
            } else {
                // Need a new court
                assignedCourt = new Court(courtIdCounter++);
                allCourts.add(assignedCourt);
            }
            
            // Assign booking to court
            assignedCourt.assignBooking(booking);
            availableCourts.offer(assignedCourt);
        }
        
        return allCourts;
    }
    
    /**
     * Part (b): Assigns bookings with fixed maintenance time after each booking.
     * 
     * Algorithm: Same as part (a) but adds maintenance time after each booking
     */
    public List<Court> assignCourtsWithMaintenance(
            List<BookingRecord> bookingRecords, 
            int maintenanceTime) {
        
        if (bookingRecords == null || bookingRecords.isEmpty()) {
            return new ArrayList<>();
        }
        
        if (maintenanceTime < 0) {
            throw new IllegalArgumentException("Maintenance time cannot be negative");
        }
        
        // Sort bookings by start time
        List<BookingRecord> sortedBookings = new ArrayList<>(bookingRecords);
        Collections.sort(sortedBookings);
        
        // Min-heap to track courts by their available time
        PriorityQueue<Court> availableCourts = new PriorityQueue<>(
            Comparator.comparingInt(Court::getAvailableFrom)
        );
        
        List<Court> allCourts = new ArrayList<>();
        int courtIdCounter = 1;
        
        for (BookingRecord booking : sortedBookings) {
            Court assignedCourt = null;
            
            // Check if any existing court is available (considering maintenance)
            if (!availableCourts.isEmpty() && 
                availableCourts.peek().canAccommodate(booking)) {
                assignedCourt = availableCourts.poll();
            } else {
                // Need a new court
                assignedCourt = new Court(courtIdCounter++);
                allCourts.add(assignedCourt);
            }
            
            // Assign booking with maintenance time
            assignedCourt.assignBookingWithMaintenance(booking, maintenanceTime);
            availableCourts.offer(assignedCourt);
        }
        
        return allCourts;
    }
    
    /**
     * Part (c): Assigns bookings with maintenance after X bookings (durability).
     * 
     * Algorithm: Track usage count per court and schedule maintenance
     */
    public List<Court> assignCourtsWithPeriodicMaintenance(
            List<BookingRecord> bookingRecords,
            int maintenanceTime,
            int durability) {
        
        if (bookingRecords == null || bookingRecords.isEmpty()) {
            return new ArrayList<>();
        }
        
        if (maintenanceTime < 0 || durability <= 0) {
            throw new IllegalArgumentException("Invalid maintenance parameters");
        }
        
        // Sort bookings by start time
        List<BookingRecord> sortedBookings = new ArrayList<>(bookingRecords);
        Collections.sort(sortedBookings);
        
        // Min-heap to track courts by their available time
        PriorityQueue<Court> availableCourts = new PriorityQueue<>(
            Comparator.comparingInt(Court::getAvailableFrom)
        );
        
        List<Court> allCourts = new ArrayList<>();
        int courtIdCounter = 1;
        
        for (BookingRecord booking : sortedBookings) {
            Court assignedCourt = null;
            
            // Find an available court
            while (!availableCourts.isEmpty()) {
                Court court = availableCourts.poll();
                
                // Check if court needs maintenance
                if (court.needsMaintenanceBeforeBooking(durability)) {
                    court.performMaintenance(maintenanceTime, durability);
                }
                
                // Check if court can accommodate booking
                if (court.canAccommodate(booking)) {
                    assignedCourt = court;
                    break;
                } else {
                    // Put back in queue
                    availableCourts.offer(court);
                    break;
                }
            }
            
            // If no court available, create new one
            if (assignedCourt == null) {
                assignedCourt = new Court(courtIdCounter++, durability);
                allCourts.add(assignedCourt);
            }
            
            // Assign booking
            assignedCourt.assignBooking(booking);
            availableCourts.offer(assignedCourt);
        }
        
        return allCourts;
    }
    
    /**
     * Part (d): Finds minimum number of courts needed (simplified version).
     * Doesn't assign bookings to specific courts, just counts.
     * 
     * Algorithm: Interval scheduling - count maximum overlapping intervals
     * 
     * Time Complexity: O(N log N)
     * Space Complexity: O(N)
     */
    public int findMinimumCourtsNeeded(List<BookingRecord> bookingRecords) {
        if (bookingRecords == null || bookingRecords.isEmpty()) {
            return 0;
        }
        
        // Create events for start and end times
        List<Event> events = new ArrayList<>();
        for (BookingRecord booking : bookingRecords) {
            events.add(new Event(booking.getStartTime(), EventType.START));
            events.add(new Event(booking.getFinishTime(), EventType.END));
        }
        
        // Sort events by time
        // If times are equal, process END before START (to reuse court)
        Collections.sort(events, (e1, e2) -> {
            if (e1.time != e2.time) {
                return Integer.compare(e1.time, e2.time);
            }
            // END events before START events at same time
            return e1.type == EventType.END ? -1 : 1;
        });
        
        int currentCourts = 0;
        int maxCourts = 0;
        
        for (Event event : events) {
            if (event.type == EventType.START) {
                currentCourts++;
                maxCourts = Math.max(maxCourts, currentCourts);
            } else {
                currentCourts--;
            }
        }
        
        return maxCourts;
    }
    
    /**
     * Part (e): Checks if two bookings conflict with each other.
     * 
     * Time Complexity: O(1)
     */
    public boolean checkBookingConflict(BookingRecord booking1, BookingRecord booking2) {
        if (booking1 == null || booking2 == null) {
            throw new IllegalArgumentException("Bookings cannot be null");
        }
        
        return booking1.conflictsWith(booking2);
    }
    
    /**
     * Checks if two bookings conflict considering maintenance time.
     */
    public boolean checkBookingConflictWithMaintenance(
            BookingRecord booking1, 
            BookingRecord booking2, 
            int maintenanceTime) {
        
        if (booking1 == null || booking2 == null) {
            throw new IllegalArgumentException("Bookings cannot be null");
        }
        
        if (maintenanceTime < 0) {
            throw new IllegalArgumentException("Maintenance time cannot be negative");
        }
        
        return booking1.conflictsWithMaintenance(booking2, maintenanceTime);
    }
    
    /**
     * Helper class for event-based algorithm.
     */
    private static class Event {
        int time;
        EventType type;
        
        Event(int time, EventType type) {
            this.time = time;
            this.type = type;
        }
    }
    
    private enum EventType {
        START, END
    }
}

