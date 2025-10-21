# Tennis Court Booking System - Interview Strategy

Complete guide to solving the "Expanding Tennis Club" interview question in 45-60 minutes.

## 📋 Problem Overview

**Question:** Design a system that assigns tennis court bookings to courts using minimum number of courts.

**Parts:**
- (a) Basic assignment with minimum courts
- (b) Fixed maintenance time after each booking
- (c) Periodic maintenance after X bookings
- (d) Simplified: just count minimum courts
- (e) Check if two bookings conflict

## ⏱️ Time Management (45-60 minutes)

| Phase | Time | Activity |
|-------|------|----------|
| 1 | 5 min | Clarify requirements & examples |
| 2 | 10 min | Design data structures |
| 3 | 20 min | Implement core algorithm (Part a) |
| 4 | 5 min | Test with examples |
| 5 | 10 min | Handle follow-ups (b, c, d, e) |
| 6 | 5 min | Discuss optimizations & edge cases |

## 🎯 Phase 1: Clarify Requirements (5 min)

### Questions to Ask

**Q1:** "What format are the times in?"  
**A:** Integers representing minutes (or any time unit)

**Q2:** "Can bookings overlap?"  
**A:** Yes, that's why we need multiple courts

**Q3:** "Are bookings sorted by start time?"  
**A:** No, assume unsorted input

**Q4:** "What should we return?"  
**A:** List of courts with their assigned bookings

**Q5:** "Can a booking start exactly when another ends?"  
**A:** Yes, no conflict if booking1.finish == booking2.start

### Example to Discuss

```
Bookings:
1. [0-60]
2. [30-90]
3. [60-120]

Expected:
Court 1: Booking 1 [0-60], Booking 3 [60-120]
Court 2: Booking 2 [30-90]

Minimum courts: 2
```

**Say:** "Let me verify my understanding with an example..."

## 🏗️ Phase 2: Design Data Structures (10 min)

### Step 1: BookingRecord Class

**Say:** "I'll start with a BookingRecord class to represent each booking."

```java
class BookingRecord {
    int id;
    int startTime;
    int finishTime;
    
    // Constructor with validation
    public BookingRecord(int id, int start, int finish) {
        if (start >= finish) {
            throw new IllegalArgumentException("Invalid times");
        }
        this.id = id;
        this.startTime = start;
        this.finishTime = finish;
    }
}
```

**Key Points:**
- Immutable data
- Validation in constructor
- Implements Comparable for sorting

### Step 2: Court Class

**Say:** "I need a Court class to track bookings and availability."

```java
class Court {
    int courtId;
    List<BookingRecord> bookings;
    int availableFrom; // When court becomes free
    
    public Court(int id) {
        this.courtId = id;
        this.bookings = new ArrayList<>();
        this.availableFrom = 0;
    }
    
    public boolean canAccommodate(BookingRecord booking) {
        return booking.startTime >= availableFrom;
    }
    
    public void assignBooking(BookingRecord booking) {
        bookings.add(booking);
        availableFrom = booking.finishTime;
    }
}
```

**Key Points:**
- Tracks when court becomes available
- Simple assignment logic
- Maintains booking list

### Step 3: Main System

**Say:** "The main system will use a greedy algorithm with a priority queue."

```java
class CourtAssignmentSystem {
    public List<Court> assignCourts(List<BookingRecord> bookings) {
        // Implementation in next phase
    }
}
```

## 💻 Phase 3: Implement Core Algorithm (20 min)

### Algorithm Explanation

**Say:** "I'll use a greedy approach with a min-heap. The key insight is that we should always assign a booking to the earliest available court."

**Steps:**
1. Sort bookings by start time
2. Use min-heap to track courts by availability
3. For each booking, assign to earliest available court
4. If no court available, create new court

### Implementation

```java
public List<Court> assignCourts(List<BookingRecord> bookingRecords) {
    if (bookingRecords == null || bookingRecords.isEmpty()) {
        return new ArrayList<>();
    }
    
    // Step 1: Sort by start time
    List<BookingRecord> sorted = new ArrayList<>(bookingRecords);
    Collections.sort(sorted, (a, b) -> Integer.compare(a.startTime, b.startTime));
    
    // Step 2: Min-heap ordered by availableFrom
    PriorityQueue<Court> availableCourts = new PriorityQueue<>(
        Comparator.comparingInt(Court::getAvailableFrom)
    );
    
    List<Court> allCourts = new ArrayList<>();
    int courtIdCounter = 1;
    
    // Step 3: Process each booking
    for (BookingRecord booking : sorted) {
        Court assignedCourt = null;
        
        // Check if earliest available court can accommodate
        if (!availableCourts.isEmpty() && 
            availableCourts.peek().canAccommodate(booking)) {
            assignedCourt = availableCourts.poll();
        } else {
            // Need new court
            assignedCourt = new Court(courtIdCounter++);
            allCourts.add(assignedCourt);
        }
        
        // Assign booking and update heap
        assignedCourt.assignBooking(booking);
        availableCourts.offer(assignedCourt);
    }
    
    return allCourts;
}
```

### Complexity Analysis

**Say:** "Let me analyze the complexity:"

- **Time:** O(N log N) for sorting + O(N log K) for heap operations = **O(N log N)**
- **Space:** O(K) for courts + O(N) for sorted list = **O(N)**

Where N = bookings, K = courts needed

## 🧪 Phase 4: Test with Examples (5 min)

### Test Case 1: Non-overlapping

```java
Bookings: [0-60], [60-120], [120-180]
Expected: 1 court
```

**Walk through:**
- Booking 1 → Court 1 (available from 60)
- Booking 2 → Court 1 (60 >= 60, OK) (available from 120)
- Booking 3 → Court 1 (120 >= 120, OK)

### Test Case 2: All overlapping

```java
Bookings: [0-100], [10-90], [20-80]
Expected: 3 courts
```

**Walk through:**
- Booking 1 → Court 1 (available from 100)
- Booking 2 → Court 2 (10 < 100, need new) (available from 90)
- Booking 3 → Court 3 (20 < 90, need new)

**Say:** "The algorithm correctly handles both extremes."

## 🔄 Phase 5: Handle Follow-ups (10 min)

### Part (b): Fixed Maintenance Time

**Interviewer:** "What if we need X minutes maintenance after each booking?"

**Say:** "I'll modify the assignBooking method to add maintenance time."

```java
public void assignBookingWithMaintenance(BookingRecord booking, int maintenanceTime) {
    bookings.add(booking);
    availableFrom = booking.finishTime + maintenanceTime;
}
```

**Time Complexity:** Still O(N log N) - same algorithm

### Part (c): Periodic Maintenance

**Interviewer:** "What if maintenance is needed after every X bookings?"

**Say:** "I'll track usage count and schedule maintenance when needed."

```java
class Court {
    int usageCount;
    int nextMaintenanceAt;
    
    public boolean needsMaintenance(int durability) {
        return nextMaintenanceAt <= 0;
    }
    
    public void performMaintenance(int time, int durability) {
        availableFrom += time;
        nextMaintenanceAt = durability;
    }
}
```

### Part (d): Minimum Courts (Simplified)

**Interviewer:** "What if we just need the count, not the assignments?"

**Say:** "I can use an event sweep algorithm - more efficient!"

```java
public int findMinimumCourtsNeeded(List<BookingRecord> bookings) {
    List<Event> events = new ArrayList<>();
    
    // Create START and END events
    for (BookingRecord b : bookings) {
        events.add(new Event(b.startTime, START));
        events.add(new Event(b.finishTime, END));
    }
    
    // Sort by time (END before START at same time)
    Collections.sort(events);
    
    int current = 0, max = 0;
    for (Event e : events) {
        if (e.type == START) {
            current++;
            max = Math.max(max, current);
        } else {
            current--;
        }
    }
    
    return max;
}
```

**Time Complexity:** O(N log N)  
**Space Complexity:** O(N)

### Part (e): Conflict Detection

**Interviewer:** "How do you check if two bookings conflict?"

**Say:** "Two bookings conflict if they overlap in time."

```java
public boolean checkConflict(BookingRecord b1, BookingRecord b2) {
    // No conflict if one ends before other starts
    return !(b1.finishTime <= b2.startTime || b2.finishTime <= b1.startTime);
}
```

**Time Complexity:** O(1)

## 🎯 Phase 6: Optimizations & Edge Cases (5 min)

### Edge Cases to Mention

**Say:** "Let me consider edge cases:"

1. **Empty list:** Return empty list
2. **Single booking:** Return 1 court
3. **All same start time:** Need N courts
4. **Null input:** Return empty or throw exception
5. **Invalid times:** Validate in constructor

### Optimizations

**Say:** "Possible optimizations:"

1. **Custom comparator:** Avoid creating sorted copy
2. **Initial heap capacity:** Set to expected court count
3. **Reuse courts:** Pool of court objects
4. **Parallel processing:** For very large datasets

### Alternative Approaches

**Interviewer:** "Are there other approaches?"

**Say:** "Yes, a few alternatives:"

1. **Interval Tree:** O(N log N) but more complex
2. **Brute Force:** O(N²) - check each booking against all courts
3. **Dynamic Programming:** Overkill for this problem

**Why greedy is best:**
- Optimal solution
- Simple to implement
- Efficient O(N log N)
- Easy to extend

## 💡 Common Interview Questions

### Q1: "Why use a min-heap?"

**A:** "The min-heap gives us O(log K) access to the earliest available court. Without it, we'd need O(K) to find the best court for each booking, making the algorithm O(N×K)."

### Q2: "What if courts have different costs?"

**A:** "I'd modify the Court class to include cost, and use a custom comparator in the heap that considers both availability and cost. We might want to prefer cheaper courts when multiple are available."

### Q3: "How would you handle booking cancellations?"

**A:** "I'd add a `removeBooking()` method that:
1. Removes booking from court's list
2. Recalculates availableFrom based on remaining bookings
3. Updates the heap"

### Q4: "What about booking priorities?"

**A:** "I'd add a priority field to BookingRecord and sort by priority first, then start time. High-priority bookings would get assigned first."

### Q5: "How to handle real-time bookings?"

**A:** "For real-time, I'd maintain the heap continuously and use a `findAvailableCourt(startTime, duration)` method that queries the heap without full reassignment."

## ✅ Interview Checklist

Before saying "I'm done":

- [ ] Clarified all requirements
- [ ] Designed clean data structures
- [ ] Implemented core algorithm
- [ ] Analyzed time/space complexity
- [ ] Tested with examples
- [ ] Handled all follow-up parts (a-e)
- [ ] Discussed edge cases
- [ ] Mentioned optimizations
- [ ] Code is clean and readable
- [ ] Added validation and error handling

## 🎓 Key Takeaways

### What Interviewers Look For

✅ **Problem Understanding:** Ask clarifying questions  
✅ **Algorithm Choice:** Explain why greedy works  
✅ **Code Quality:** Clean, readable, validated  
✅ **Complexity Analysis:** Accurate time/space analysis  
✅ **Testing:** Walk through examples  
✅ **Extensibility:** Handle follow-ups smoothly  

### Common Mistakes

❌ Jumping to code without clarifying  
❌ Using brute force O(N²) approach  
❌ Not sorting bookings first  
❌ Incorrect conflict detection logic  
❌ Forgetting edge cases  
❌ Not explaining thought process  

### Success Tips

💡 **Think out loud:** Explain your reasoning  
💡 **Start simple:** Get basic version working first  
💡 **Test as you go:** Verify with small examples  
💡 **Ask for hints:** If stuck, ask for guidance  
💡 **Stay calm:** This is a well-known problem  

## 📚 Related Problems to Practice

1. **Meeting Rooms II** (LeetCode 253)
2. **Merge Intervals** (LeetCode 56)
3. **Insert Interval** (LeetCode 57)
4. **Non-overlapping Intervals** (LeetCode 435)
5. **My Calendar I/II/III** (LeetCode 729/731/732)

---

**Good luck with your interview! 🚀**

Remember: This is a classic problem. Focus on clear communication and clean code.

