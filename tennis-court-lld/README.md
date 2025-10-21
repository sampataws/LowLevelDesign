# Tennis Court Booking System - Low Level Design

A comprehensive tennis court booking system that efficiently assigns bookings to courts using minimum resources. This implementation covers all aspects of the classic "Expanding Tennis Club" interview question.

## 📋 Problem Statement

Design a system for a tennis club that:
- Assigns bookings to specific courts
- Uses the minimum number of courts
- Handles maintenance requirements
- Detects booking conflicts

## 🎯 Interview Question Parts Covered

### Part (a): Basic Court Assignment
Implement a function that assigns bookings to courts using minimum number of courts.

**Function Signature:**
```java
List<Court> assignCourts(List<BookingRecord> bookingRecords)
```

**Algorithm:** Greedy approach with min-heap
- Sort bookings by start time
- Use priority queue to track court availability
- Assign to earliest available court or create new court

**Time Complexity:** O(N log N)  
**Space Complexity:** O(K) where K = number of courts

### Part (b): Fixed Maintenance Time
After each booking, a fixed maintenance time X is needed before the court can be used again.

**Function Signature:**
```java
List<Court> assignCourtsWithMaintenance(
    List<BookingRecord> bookingRecords, 
    int maintenanceTime)
```

### Part (c): Periodic Maintenance
Courts need maintenance after X bookings (durability).

**Function Signature:**
```java
List<Court> assignCourtsWithPeriodicMaintenance(
    List<BookingRecord> bookingRecords,
    int maintenanceTime,
    int durability)
```

**Features:**
- Tracks usage count per court
- Schedules maintenance after durability threshold
- Adds maintenance time to court availability

### Part (d): Minimum Courts Needed (Simplified)
Find minimum number of courts without assigning to specific courts.

**Function Signature:**
```java
int findMinimumCourtsNeeded(List<BookingRecord> bookingRecords)
```

**Algorithm:** Interval scheduling with event sweep
- Create START and END events for each booking
- Sort events by time
- Count maximum concurrent bookings

**Time Complexity:** O(N log N)  
**Space Complexity:** O(N)

### Part (e): Conflict Detection
Check if two bookings conflict with each other.

**Function Signature:**
```java
boolean checkBookingConflict(BookingRecord booking1, BookingRecord booking2)
```

**Logic:** Two bookings conflict if they overlap in time
- No conflict if: `booking1.finish <= booking2.start OR booking2.finish <= booking1.start`
- Conflict otherwise

## 🏗️ Architecture

### Core Classes

#### 1. **BookingRecord**
Represents a tennis court booking.

```java
class BookingRecord {
    int id;
    int startTime;
    int finishTime;
    
    boolean conflictsWith(BookingRecord other);
    boolean conflictsWithMaintenance(BookingRecord other, int maintenanceTime);
}
```

#### 2. **Court**
Represents a tennis court with assigned bookings.

```java
class Court {
    int courtId;
    List<BookingRecord> bookings;
    int availableFrom;
    int usageCount;
    int nextMaintenanceAt;
    
    boolean canAccommodate(BookingRecord booking);
    void assignBooking(BookingRecord booking);
    void performMaintenance(int maintenanceTime, int durability);
}
```

#### 3. **CourtAssignmentSystem**
Main system for court assignment algorithms.

```java
class CourtAssignmentSystem {
    List<Court> assignCourts(List<BookingRecord> bookings);
    List<Court> assignCourtsWithMaintenance(List<BookingRecord> bookings, int maintenanceTime);
    List<Court> assignCourtsWithPeriodicMaintenance(...);
    int findMinimumCourtsNeeded(List<BookingRecord> bookings);
    boolean checkBookingConflict(BookingRecord b1, BookingRecord b2);
}
```

## 🚀 Usage Examples

### Basic Court Assignment
```java
CourtAssignmentSystem system = new CourtAssignmentSystem();

List<BookingRecord> bookings = Arrays.asList(
    new BookingRecord(1, 0, 60),
    new BookingRecord(2, 30, 90),
    new BookingRecord(3, 60, 120)
);

List<Court> courts = system.assignCourts(bookings);
System.out.println("Courts needed: " + courts.size()); // 2
```

### With Maintenance Time
```java
List<Court> courts = system.assignCourtsWithMaintenance(bookings, 10);
// Each court needs 10 minutes maintenance after each booking
```

### Minimum Courts Calculation
```java
int minCourts = system.findMinimumCourtsNeeded(bookings);
System.out.println("Minimum courts: " + minCourts);
```

### Conflict Detection
```java
BookingRecord b1 = new BookingRecord(1, 0, 60);
BookingRecord b2 = new BookingRecord(2, 30, 90);

boolean conflict = system.checkBookingConflict(b1, b2);
System.out.println("Conflict: " + conflict); // true
```

## 🧮 Algorithm Details

### Greedy Court Assignment (Part a, b, c)

**Key Insight:** Always assign booking to the earliest available court.

**Steps:**
1. Sort bookings by start time: O(N log N)
2. Maintain min-heap of courts ordered by availability: O(log K) per operation
3. For each booking:
   - Check if earliest available court can accommodate
   - If yes, assign to that court
   - If no, create new court
4. Update court availability after assignment

**Why it works:** 
- Greedy choice is optimal for interval scheduling
- Using earliest available court minimizes total courts needed
- Min-heap ensures efficient court selection

### Event Sweep Algorithm (Part d)

**Key Insight:** Maximum concurrent bookings = minimum courts needed.

**Steps:**
1. Create START and END events for each booking
2. Sort events by time (END before START at same time)
3. Sweep through events:
   - START event: increment counter
   - END event: decrement counter
4. Track maximum counter value

**Example:**
```
Bookings: [0-60], [30-90], [45-105]

Events: START(0), START(30), START(45), END(60), END(90), END(105)

Counter: 0 -> 1 -> 2 -> 3 -> 2 -> 1 -> 0
                      ^
                   Max = 3 courts needed
```

## 📊 Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Basic Assignment | O(N log N) | O(K) |
| With Maintenance | O(N log N) | O(K) |
| Periodic Maintenance | O(N log N) | O(K) |
| Minimum Courts | O(N log N) | O(N) |
| Conflict Check | O(1) | O(1) |

Where:
- N = number of bookings
- K = number of courts needed

## 🧪 Testing

### Test Coverage
- **CourtAssignmentSystemTest**: 38 tests
  - Basic assignment scenarios
  - Maintenance handling
  - Periodic maintenance
  - Minimum courts calculation
  - Conflict detection
  - Edge cases

- **BookingRecordTest**: 15 tests
  - Validation
  - Conflict detection
  - Comparison and sorting

- **CourtTest**: 15 tests
  - Court state management
  - Booking assignment
  - Maintenance tracking

**Total: 68 comprehensive tests**

### Run Tests
```bash
mvn test -pl tennis-court-lld
```

## 🎮 Demo Application

Run the comprehensive demo:
```bash
mvn exec:java -Dexec.mainClass="com.tenniscourt.DriverApplication" -pl tennis-court-lld
```

**Demo Scenarios:**
1. Basic court assignment
2. Fixed maintenance after each booking
3. Periodic maintenance after X bookings
4. Minimum courts calculation
5. Conflict detection

## 🎓 Interview Tips

### Key Points to Mention

1. **Algorithm Choice:**
   - "I'll use a greedy approach with a min-heap for optimal court assignment"
   - "The key insight is that assigning to the earliest available court is optimal"

2. **Time Complexity:**
   - "Sorting takes O(N log N)"
   - "Each heap operation is O(log K)"
   - "Overall: O(N log N)"

3. **Edge Cases:**
   - Empty booking list
   - Single booking
   - All bookings overlap
   - Bookings at same time
   - Maintenance time = 0

4. **Follow-up Questions:**
   - "How would you handle booking cancellations?" → Remove from court, update availability
   - "What if courts have different costs?" → Use cost-aware priority queue
   - "How to handle booking priorities?" → Sort by priority first, then time

### Common Mistakes to Avoid

❌ Not sorting bookings by start time  
❌ Using O(N²) brute force approach  
❌ Not handling edge cases (empty list, null)  
❌ Forgetting to add maintenance time to availability  
❌ Incorrect conflict detection logic  

✅ Sort bookings first  
✅ Use min-heap for efficiency  
✅ Handle all edge cases  
✅ Track court state properly  
✅ Test with various scenarios  

## 🔧 Build and Run

```bash
# Build the module
mvn clean compile -pl tennis-court-lld

# Run tests
mvn test -pl tennis-court-lld

# Run demo
mvn exec:java -Dexec.mainClass="com.tenniscourt.DriverApplication" -pl tennis-court-lld
```

## 📚 Related Concepts

- **Interval Scheduling:** Classic greedy algorithm problem
- **Meeting Rooms:** Similar problem (minimum meeting rooms needed)
- **Activity Selection:** Choosing maximum non-overlapping activities
- **Priority Queue:** Efficient min/max tracking
- **Event Sweep:** Processing events in time order

## 🎯 Real-World Applications

- **Tennis/Sports Club Management**
- **Meeting Room Booking Systems**
- **Resource Allocation (servers, machines)**
- **Parking Lot Management**
- **Conference Room Scheduling**
- **Classroom Assignment**

---

**Status:** ✅ Complete - All 5 parts implemented and tested

[View Interview Strategy](./INTERVIEW_STRATEGY.md)

