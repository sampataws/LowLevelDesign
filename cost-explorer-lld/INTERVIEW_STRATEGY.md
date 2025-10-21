# Cost Explorer - Interview Strategy Guide

## 📝 Overview

This guide helps you solve the "Cost Explorer" interview question in a 45-60 minute coding interview. The problem involves building a subscription billing system that calculates monthly and yearly costs for SaaS products.

## ⏱️ Time Management (60 minutes)

| Phase | Time | Activity |
|-------|------|----------|
| 1. Clarification | 5 min | Understand requirements, ask questions |
| 2. Design | 10 min | Design data structures and API |
| 3. Plan Class | 10 min | Implement subscription plan |
| 4. Basic Cost Calculation | 10 min | Monthly and yearly costs |
| 5. Proration Logic | 10 min | Handle mid-month subscriptions |
| 6. Reports | 10 min | Monthly and yearly reports |
| 7. Testing | 5 min | Quick validation |

## 📋 Phase 1: Clarification (5 minutes)

### Questions to Ask

1. **Billing Cycle**:
   - "What's the billing cycle?" → Monthly
   - "Do we need to support annual plans?" → Start with monthly
   - "When is billing calculated?" → Any day of the year

2. **Proration**:
   - "What if customer subscribes mid-month?" → Prorate by days
   - "How to handle plan changes?" → End old plan, start new plan
   - "What about cancellations?" → Set end date on plan

3. **Multiple Plans**:
   - "Can a customer have multiple products?" → Yes (Jira + Confluence)
   - "Can a product have multiple plans?" → Yes (Standard + Add-ons)
   - "How to handle upgrades?" → New plan with different cost

4. **Time Period**:
   - "What is a 'unit year'?" → Calendar year of current date
   - "Do we show past and future months?" → Yes, mark future as estimated
   - "What about leap years?" → Handle correctly

5. **Output**:
   - "What format for reports?" → Monthly breakdown + yearly total
   - "Need to show plan details?" → Yes, breakdown by plan
   - "Need cost by product?" → Nice to have

### What to Say

> "Let me clarify the requirements:
> - Customer can have multiple subscription plans
> - Each plan has a monthly cost and date range
> - Need to calculate monthly cost (with proration)
> - Need to calculate yearly cost for the unit year
> - Generate reports with breakdown by plan
> 
> I'll design a system with:
> 1. Plan class for subscription details
> 2. CostExplorer for cost calculations
> 3. Report classes for monthly/yearly summaries
> 4. Proration logic for partial months
> 
> Does this align with your expectations?"

## 🏗️ Phase 2: Design (10 minutes)

### Data Structures

```java
// Plan - represents a subscription plan
class Plan {
    String planId;              // Unique identifier
    String productName;         // Product (Jira, Confluence)
    double monthlyCost;         // Monthly subscription cost
    LocalDate startDate;        // When plan starts
    LocalDate endDate;          // When plan ends (null = ongoing)
}

// CostExplorer - main calculation engine
class CostExplorer {
    List<Plan> plans;           // All active plans
    LocalDate currentDate;      // Current date for calculations
}

// Reports
class MonthlyCostReport {
    int year, month;
    double totalCost;
    List<PlanCostDetail> planDetails;
    boolean isFuture;
}

class YearlyCostReport {
    int year;
    double totalCost;
    List<MonthlyCostReport> monthlyReports;
}
```

### API Design

```java
// Core operations
void addPlan(Plan plan)
double getMonthlyCost(int year, int month)
double getYearlyCost()
MonthlyCostReport getMonthlyCostReport(int year, int month)
YearlyCostReport getYearlyCostReport()

// Plan operations
boolean isActiveInMonth(int year, int month)
double getCostForMonth(int year, int month)
```

### What to Say

> "I'll use these data structures:
> 
> 1. **Plan class**: Encapsulates subscription details
>    - Immutable dates for thread safety
>    - Null end date means ongoing subscription
> 
> 2. **CostExplorer**: Main calculation engine
>    - Stores all plans
>    - Calculates costs for any month/year
> 
> 3. **Report classes**: Structured output
>    - Monthly breakdown with plan details
>    - Yearly summary with all months
> 
> Key algorithm: Proration by days for partial months"

## 💻 Phase 3: Plan Class (10 minutes)

### Implementation Order

1. **Basic Plan class** (5 min):
```java
public class Plan {
    private final String planId;
    private final String productName;
    private final double monthlyCost;
    private final LocalDate startDate;
    private final LocalDate endDate;
    
    public Plan(String planId, String productName, double monthlyCost,
                LocalDate startDate, LocalDate endDate) {
        // Validation
        if (planId == null || planId.trim().isEmpty()) {
            throw new IllegalArgumentException("Plan ID required");
        }
        if (monthlyCost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date before start date");
        }
        
        this.planId = planId;
        this.productName = productName;
        this.monthlyCost = monthlyCost;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Constructor for ongoing plans
    public Plan(String planId, String productName, double monthlyCost,
                LocalDate startDate) {
        this(planId, productName, monthlyCost, startDate, null);
    }
}
```

2. **Active checking** (2 min):
```java
public boolean isActiveInMonth(int year, int month) {
    LocalDate monthStart = LocalDate.of(year, month, 1);
    LocalDate monthEnd = monthStart.withDayOfMonth(
        monthStart.lengthOfMonth());
    
    // Plan overlaps with month if:
    // - Starts before or during month
    // - Ends after or during month (or ongoing)
    if (endDate != null && endDate.isBefore(monthStart)) {
        return false;
    }
    if (startDate.isAfter(monthEnd)) {
        return false;
    }
    return true;
}
```

3. **Cost calculation with proration** (3 min):
```java
public double getCostForMonth(int year, int month) {
    if (!isActiveInMonth(year, month)) {
        return 0.0;
    }
    
    LocalDate monthStart = LocalDate.of(year, month, 1);
    LocalDate monthEnd = monthStart.withDayOfMonth(
        monthStart.lengthOfMonth());
    
    // Determine actual billing period
    LocalDate billingStart = startDate.isAfter(monthStart) 
        ? startDate : monthStart;
    LocalDate billingEnd = (endDate != null && endDate.isBefore(monthEnd)) 
        ? endDate : monthEnd;
    
    // Calculate days active
    int totalDaysInMonth = monthStart.lengthOfMonth();
    int daysActive = (int) (billingEnd.toEpochDay() 
        - billingStart.toEpochDay() + 1);
    
    // Prorate the cost
    return (monthlyCost * daysActive) / totalDaysInMonth;
}
```

### Test

```java
Plan plan = new Plan("JIRA-001", "Jira", 30.0, 
    LocalDate.of(2024, 6, 15));

// June has 30 days, plan starts on day 15, so 16 days active
double juneCost = plan.getCostForMonth(2024, 6);
assert juneCost == (30.0 * 16) / 30; // $16.00
```

### What to Say

> "For the Plan class:
> 
> 1. **Immutable fields**: Thread-safe, prevents bugs
> 2. **Validation**: Ensure data integrity
> 3. **Proration logic**: Fair billing for partial months
>    - Calculate days active in month
>    - Prorate: (cost × days) / total days
> 4. **Edge cases**: Handle month boundaries, leap years
> 
> This gives us accurate cost calculation for any date range."

## 💰 Phase 4: Basic Cost Calculation (10 minutes)

### Implementation

```java
public class CostExplorer {
    private final List<Plan> plans;
    private final LocalDate currentDate;
    
    public CostExplorer(LocalDate currentDate) {
        this.plans = new ArrayList<>();
        this.currentDate = currentDate;
    }
    
    public void addPlan(Plan plan) {
        if (plan == null) {
            throw new IllegalArgumentException("Plan cannot be null");
        }
        plans.add(plan);
    }
    
    public double getMonthlyCost(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month");
        }
        
        double total = 0.0;
        for (Plan plan : plans) {
            total += plan.getCostForMonth(year, month);
        }
        return total;
    }
    
    public double getYearlyCost(int year) {
        double total = 0.0;
        for (int month = 1; month <= 12; month++) {
            total += getMonthlyCost(year, month);
        }
        return total;
    }
    
    public double getYearlyCost() {
        return getYearlyCost(currentDate.getYear());
    }
}
```

### Test

```java
CostExplorer explorer = new CostExplorer(LocalDate.of(2024, 6, 15));

Plan jira = new Plan("JIRA-001", "Jira", 10.0, 
    LocalDate.of(2024, 1, 1));
Plan confluence = new Plan("CONF-001", "Confluence", 15.0, 
    LocalDate.of(2024, 1, 1));

explorer.addPlan(jira);
explorer.addPlan(confluence);

assert explorer.getMonthlyCost(2024, 6) == 25.0;
assert explorer.getYearlyCost(2024) == 300.0; // 25 * 12
```

### What to Say

> "For cost calculation:
> 
> 1. **Monthly cost**: Sum all plan costs for the month
>    - Each plan calculates its own cost
>    - Handles proration automatically
> 
> 2. **Yearly cost**: Sum all 12 months
>    - Simple iteration over months
>    - O(12 × P) = O(P) where P = plans
> 
> 3. **Current date**: Used to determine unit year
>    - Also useful for marking future months
> 
> This gives us the core functionality."

## 📊 Phase 5: Proration Logic (10 minutes)

### Edge Cases to Handle

```java
// Test 1: Mid-month start
Plan plan1 = new Plan("JIRA-001", "Jira", 30.0, 
    LocalDate.of(2024, 6, 15));
// June: 16 days active (15-30)
assert plan1.getCostForMonth(2024, 6) == 16.0;

// Test 2: Mid-month end
Plan plan2 = new Plan("JIRA-002", "Jira", 30.0, 
    LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 15));
// June: 15 days active (1-15)
assert plan2.getCostForMonth(2024, 6) == 15.0;

// Test 3: Leap year
Plan plan3 = new Plan("JIRA-003", "Jira", 29.0, 
    LocalDate.of(2024, 2, 1));
// February 2024 has 29 days
assert plan3.getCostForMonth(2024, 2) == 29.0;

// Test 4: Plan upgrade
Plan standard = new Plan("JIRA-STD", "Jira", 10.0, 
    LocalDate.of(2024, 1, 1), LocalDate.of(2024, 5, 31));
Plan premium = new Plan("JIRA-PRE", "Jira", 25.0, 
    LocalDate.of(2024, 6, 1));

explorer.addPlan(standard);
explorer.addPlan(premium);

assert explorer.getMonthlyCost(2024, 5) == 10.0;
assert explorer.getMonthlyCost(2024, 6) == 25.0;
```

### What to Say

> "Proration edge cases:
> 
> 1. **Mid-month start**: Count from start date to month end
> 2. **Mid-month end**: Count from month start to end date
> 3. **Leap years**: Use actual days in month
> 4. **Plan changes**: Separate plans with end/start dates
> 
> Formula: (Monthly Cost × Days Active) / Total Days in Month
> 
> This ensures fair billing for all scenarios."

## 📋 Phase 6: Reports (10 minutes)

### Implementation

```java
public MonthlyCostReport getMonthlyCostReport(int year, int month) {
    List<PlanCostDetail> planDetails = new ArrayList<>();
    double totalCost = 0.0;
    
    for (Plan plan : plans) {
        double cost = plan.getCostForMonth(year, month);
        if (cost > 0) {
            planDetails.add(new PlanCostDetail(
                plan.getPlanId(),
                plan.getProductName(),
                cost
            ));
            totalCost += cost;
        }
    }
    
    // Check if future month
    LocalDate monthDate = LocalDate.of(year, month, 1);
    LocalDate currentMonth = LocalDate.of(
        currentDate.getYear(), 
        currentDate.getMonthValue(), 
        1
    );
    boolean isFuture = monthDate.isAfter(currentMonth);
    
    return new MonthlyCostReport(year, month, totalCost, 
        planDetails, isFuture);
}

public YearlyCostReport getYearlyCostReport(int year) {
    List<MonthlyCostReport> monthlyReports = new ArrayList<>();
    double totalYearlyCost = 0.0;
    
    for (int month = 1; month <= 12; month++) {
        MonthlyCostReport monthlyReport = 
            getMonthlyCostReport(year, month);
        monthlyReports.add(monthlyReport);
        totalYearlyCost += monthlyReport.getTotalCost();
    }
    
    return new YearlyCostReport(year, totalYearlyCost, monthlyReports);
}
```

### What to Say

> "For reports:
> 
> 1. **Monthly report**: Breakdown by plan
>    - Shows which plans contribute to cost
>    - Marks future months as 'Estimated'
> 
> 2. **Yearly report**: All 12 months
>    - Complete view of the year
>    - Total cost at the top
> 
> 3. **Future vs Past**: Based on current date
>    - Helps customer understand estimates
> 
> This provides clear, actionable information."

## ✅ Phase 7: Testing (5 minutes)

### Quick Validation

```java
// Test basic subscription
CostExplorer explorer = new CostExplorer(LocalDate.of(2024, 6, 15));
explorer.addPlan(new Plan("JIRA-001", "Jira", 10.0, 
    LocalDate.of(2024, 1, 1)));

assert explorer.getMonthlyCost(2024, 1) == 10.0;
assert explorer.getYearlyCost(2024) == 120.0;

// Test proration
explorer.addPlan(new Plan("CONF-001", "Confluence", 30.0, 
    LocalDate.of(2024, 6, 15)));
double juneCost = explorer.getMonthlyCost(2024, 6);
assert juneCost == 10.0 + 16.0; // Jira + prorated Confluence

// Test reports
YearlyCostReport report = explorer.getYearlyCostReport();
assert report.getMonthlyReports().size() == 12;
assert report.getTotalCost() == explorer.getYearlyCost();
```

## 🎯 Common Interview Questions

**Q: How to handle discounts?**
> "Add Discount class with percentage/amount. Apply in getCostForMonth(). Track validity period."

**Q: How to handle usage-based billing?**
> "Create UsagePlan subclass. Track usage metrics. Calculate cost = base + (usage × rate)."

**Q: How to handle annual plans?**
> "Add billingCycle field. For annual: divide yearly cost by 12 for monthly view. Or show lump sum in first month."

**Q: How to optimize for many plans?**
> "Current O(P) per month is optimal. Could cache yearly cost if plans don't change often."

**Q: How to handle refunds?**
> "Add Credit class. Deduct from monthly cost. Track credit balance and expiration."

**Q: Thread safety?**
> "Current implementation not thread-safe. Add synchronized methods or use ConcurrentHashMap for plans."

## 📝 Interview Checklist

- [ ] Clarified all requirements
- [ ] Designed Plan and CostExplorer classes
- [ ] Implemented proration logic
- [ ] Handled edge cases (leap year, mid-month)
- [ ] Implemented monthly cost calculation
- [ ] Implemented yearly cost calculation
- [ ] Created report classes
- [ ] Tested with examples
- [ ] Discussed optimizations

## 🎓 Success Tips

1. **Start simple**: Get basic monthly cost working first
2. **Validate inputs**: Check for null, negative costs, invalid dates
3. **Test incrementally**: Verify each feature before moving on
4. **Explain proration**: Walk through the calculation clearly
5. **Handle edge cases**: Leap years, month boundaries
6. **Clean code**: Use meaningful names, extract methods
7. **Think extensibility**: How to add discounts, usage billing
8. **Communicate**: Explain your thinking process

---

**Remember**: The interviewer wants to see your problem-solving approach and how you handle real-world billing scenarios. Focus on correctness first, then optimize!

