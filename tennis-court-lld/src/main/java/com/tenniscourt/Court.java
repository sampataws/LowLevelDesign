package com.tenniscourt;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tennis court with assigned bookings.
 */
public class Court {
    
    private final int courtId;
    private final List<BookingRecord> bookings;
    private int availableFrom; // Time when court becomes available
    private int usageCount; // Number of bookings on this court
    private int nextMaintenanceAt; // After how many more bookings maintenance is needed
    
    public Court(int courtId) {
        this.courtId = courtId;
        this.bookings = new ArrayList<>();
        this.availableFrom = 0;
        this.usageCount = 0;
        this.nextMaintenanceAt = -1; // -1 means no maintenance tracking
    }
    
    public Court(int courtId, int durability) {
        this.courtId = courtId;
        this.bookings = new ArrayList<>();
        this.availableFrom = 0;
        this.usageCount = 0;
        this.nextMaintenanceAt = durability;
    }
    
    public int getCourtId() {
        return courtId;
    }
    
    public List<BookingRecord> getBookings() {
        return new ArrayList<>(bookings);
    }
    
    public int getAvailableFrom() {
        return availableFrom;
    }
    
    public int getUsageCount() {
        return usageCount;
    }
    
    public int getNextMaintenanceAt() {
        return nextMaintenanceAt;
    }
    
    /**
     * Checks if the court can accommodate a booking at the given time.
     */
    public boolean canAccommodate(BookingRecord booking) {
        return booking.getStartTime() >= availableFrom;
    }
    
    /**
     * Checks if the court can accommodate a booking with maintenance time.
     */
    public boolean canAccommodateWithMaintenance(BookingRecord booking, int maintenanceTime) {
        int requiredStartTime = availableFrom + maintenanceTime;
        return booking.getStartTime() >= requiredStartTime;
    }
    
    /**
     * Checks if the court needs maintenance before this booking.
     */
    public boolean needsMaintenanceBeforeBooking(int durability) {
        return nextMaintenanceAt != -1 && nextMaintenanceAt <= 0;
    }
    
    /**
     * Assigns a booking to this court.
     */
    public void assignBooking(BookingRecord booking) {
        bookings.add(booking);
        availableFrom = booking.getFinishTime();
        usageCount++;
        
        if (nextMaintenanceAt > 0) {
            nextMaintenanceAt--;
        }
    }
    
    /**
     * Assigns a booking with maintenance time after.
     */
    public void assignBookingWithMaintenance(BookingRecord booking, int maintenanceTime) {
        bookings.add(booking);
        availableFrom = booking.getFinishTime() + maintenanceTime;
        usageCount++;
        
        if (nextMaintenanceAt > 0) {
            nextMaintenanceAt--;
        }
    }
    
    /**
     * Performs maintenance on the court.
     */
    public void performMaintenance(int maintenanceTime, int durability) {
        availableFrom += maintenanceTime;
        nextMaintenanceAt = durability;
    }
    
    /**
     * Resets the court to initial state.
     */
    public void reset() {
        bookings.clear();
        availableFrom = 0;
        usageCount = 0;
    }
    
    @Override
    public String toString() {
        return "Court{" +
                "id=" + courtId +
                ", bookings=" + bookings.size() +
                ", availableFrom=" + availableFrom +
                ", usageCount=" + usageCount +
                '}';
    }
}

