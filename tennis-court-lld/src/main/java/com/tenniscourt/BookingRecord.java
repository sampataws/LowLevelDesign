package com.tenniscourt;

/**
 * Represents a tennis court booking with start and finish times.
 */
public class BookingRecord implements Comparable<BookingRecord> {
    
    private final int id;
    private final int startTime;
    private final int finishTime;
    
    public BookingRecord(int id, int startTime, int finishTime) {
        if (startTime >= finishTime) {
            throw new IllegalArgumentException("Start time must be before finish time");
        }
        if (startTime < 0 || finishTime < 0) {
            throw new IllegalArgumentException("Times cannot be negative");
        }
        
        this.id = id;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
    
    public int getId() {
        return id;
    }
    
    public int getStartTime() {
        return startTime;
    }
    
    public int getFinishTime() {
        return finishTime;
    }
    
    public int getDuration() {
        return finishTime - startTime;
    }
    
    /**
     * Checks if this booking conflicts with another booking.
     */
    public boolean conflictsWith(BookingRecord other) {
        // Two bookings conflict if they overlap in time
        // No conflict if: this ends before other starts OR other ends before this starts
        // Conflict if: NOT (this.finish <= other.start OR other.finish <= this.start)
        return !(this.finishTime <= other.startTime || other.finishTime <= this.startTime);
    }
    
    /**
     * Checks if this booking conflicts with another booking considering maintenance time.
     */
    public boolean conflictsWithMaintenance(BookingRecord other, int maintenanceTime) {
        // After this booking, we need maintenance time before next booking
        int thisEffectiveFinish = this.finishTime + maintenanceTime;
        int otherEffectiveFinish = other.finishTime + maintenanceTime;
        
        return !(thisEffectiveFinish <= other.startTime || otherEffectiveFinish <= this.startTime);
    }
    
    @Override
    public int compareTo(BookingRecord other) {
        // Sort by start time, then by finish time
        if (this.startTime != other.startTime) {
            return Integer.compare(this.startTime, other.startTime);
        }
        return Integer.compare(this.finishTime, other.finishTime);
    }
    
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", start=" + startTime +
                ", finish=" + finishTime +
                ", duration=" + getDuration() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingRecord that = (BookingRecord) o;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

