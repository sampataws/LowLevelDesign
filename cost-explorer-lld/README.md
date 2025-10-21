# Cost Explorer

A comprehensive subscription billing and cost estimation system for SaaS products like Atlassian's Jira, Confluence, and Bitbucket.

## 📋 Problem Statement

Imagine you are working on the payments team at Atlassian. A customer subscribes to Jira and is interested in exploring how much it will cost them to keep using the product for the rest of the year.

Your task is to develop a **CostExplorer** that calculates the total cost a customer has to pay in a unit year. This means that at any day of the year they should be able to get a provisional report giving monthly/yearly cost estimates.

### Requirements

**CostExplorer should be able to provide a report of:**
- **Monthly cost**: Generate a bill for each month, including bill amount for future months for the unit year
- **Yearly cost estimated**: Total cost for the unit year
- **Level 0**: Multiple Plans for a Product

## 🏗️ Architecture

### Core Components

```
CostExplorer
├── Plan                    # Subscription plan with pricing
├── MonthlyCostReport       # Monthly cost breakdown
├── YearlyCostReport        # Yearly cost summary
└── CostExplorer            # Main cost calculation engine
```

### Data Structures

```java
class Plan {
    String planId;              // Unique plan identifier
    String productName;         // Product name (Jira, Confluence, etc.)
    double monthlyCost;         // Monthly subscription cost
    LocalDate startDate;        // Plan start date
    LocalDate endDate;          // Plan end date (null = ongoing)
}

class CostExplorer {
    List<Plan> plans;           // All subscription plans
    LocalDate currentDate;      // Current date for calculations
}
```

## 🚀 Usage

### Basic Subscription

```java
// Customer subscribes to Jira on January 1, 2024
LocalDate currentDate = LocalDate.of(2024, 6, 15);
CostExplorer explorer = new CostExplorer(currentDate);

// Add Jira Standard plan - $10/month
Plan jiraPlan = new Plan("JIRA-STD-001", "Jira", 10.0, 
    LocalDate.of(2024, 1, 1));
explorer.addPlan(jiraPlan);

// Get yearly cost
double yearlyCost = explorer.getYearlyCost();
System.out.println("Total yearly cost: $" + yearlyCost); // $120.00
```

### Multiple Plans for Different Products

```java
CostExplorer explorer = new CostExplorer(LocalDate.of(2024, 3, 20));

// Customer has multiple Atlassian products
explorer.addPlan(new Plan("JIRA-STD-001", "Jira", 10.0, 
    LocalDate.of(2024, 1, 1)));
explorer.addPlan(new Plan("CONF-STD-001", "Confluence", 15.0, 
    LocalDate.of(2024, 1, 1)));
explorer.addPlan(new Plan("BB-STD-001", "Bitbucket", 8.0, 
    LocalDate.of(2024, 2, 1)));

// Get monthly report
MonthlyCostReport marchReport = explorer.getMonthlyCostReport(2024, 3);
System.out.println(marchReport);
// Output:
// MARCH 2024: $33.00
//   - Jira (JIRA-STD-001): $10.00
//   - Confluence (CONF-STD-001): $15.00
//   - Bitbucket (BB-STD-001): $8.00
```

### Mid-Year Subscription (Prorated Billing)

```java
CostExplorer explorer = new CostExplorer(LocalDate.of(2024, 6, 15));

// Customer subscribes on June 15, 2024
Plan jiraPlan = new Plan("JIRA-PRO-001", "Jira", 30.0, 
    LocalDate.of(2024, 6, 15));
explorer.addPlan(jiraPlan);

// Get June cost (prorated for 16 days)
double juneCost = explorer.getMonthlyCost(2024, 6);
System.out.println("June cost: $" + juneCost); // $16.00

// Get July cost (full month)
double julyCost = explorer.getMonthlyCost(2024, 7);
System.out.println("July cost: $" + julyCost); // $30.00
```

### Plan Changes (Upgrades/Downgrades)

```java
CostExplorer explorer = new CostExplorer(LocalDate.of(2024, 8, 10));

// Customer starts with Standard, upgrades to Premium in June
Plan jiraStandard = new Plan("JIRA-STD-001", "Jira", 10.0, 
    LocalDate.of(2024, 1, 1), LocalDate.of(2024, 5, 31));
Plan jiraPremium = new Plan("JIRA-PRE-001", "Jira", 25.0, 
    LocalDate.of(2024, 6, 1));

explorer.addPlan(jiraStandard);
explorer.addPlan(jiraPremium);

// May: $10 (Standard), June: $25 (Premium)
System.out.println("May: $" + explorer.getMonthlyCost(2024, 5));   // $10.00
System.out.println("June: $" + explorer.getMonthlyCost(2024, 6));  // $25.00

// Yearly: 5 months Standard + 7 months Premium = $225
System.out.println("Yearly: $" + explorer.getYearlyCost()); // $225.00
```

### Yearly Cost Report

```java
CostExplorer explorer = new CostExplorer(LocalDate.of(2024, 6, 15));
explorer.addPlan(new Plan("JIRA-STD-001", "Jira", 10.0, 
    LocalDate.of(2024, 1, 1)));

YearlyCostReport report = explorer.getYearlyCostReport();
System.out.println(report);

// Output:
// === Yearly Cost Report for 2024 ===
// Total Estimated Cost: $120.00
//
// Monthly Breakdown:
// JANUARY 2024: $10.00
//   - Jira (JIRA-STD-001): $10.00
// FEBRUARY 2024: $10.00
//   - Jira (JIRA-STD-001): $10.00
// ...
// JULY 2024 (Estimated): $10.00
//   - Jira (JIRA-STD-001): $10.00
// ...
```

### Cost Breakdown by Product

```java
CostExplorer explorer = new CostExplorer(LocalDate.of(2024, 9, 1));

// Multiple products with multiple plans
explorer.addPlan(new Plan("JIRA-STD-001", "Jira", 10.0, 
    LocalDate.of(2024, 1, 1)));
explorer.addPlan(new Plan("JIRA-ADD-001", "Jira", 5.0, 
    LocalDate.of(2024, 3, 1))); // Add-on
explorer.addPlan(new Plan("CONF-STD-001", "Confluence", 15.0, 
    LocalDate.of(2024, 1, 1)));

// Get cost breakdown by product
Map<String, Double> costByProduct = explorer.getYearlyCostByProduct(2024);
for (Map.Entry<String, Double> entry : costByProduct.entrySet()) {
    System.out.println(entry.getKey() + ": $" + entry.getValue());
}
// Output:
// Jira: $170.00 (10*12 + 5*10)
// Confluence: $180.00 (15*12)
```

## 📊 Algorithm Details

### Proration Algorithm

When a plan starts or ends mid-month, the cost is prorated based on the number of days active:

```
Prorated Cost = (Monthly Cost × Days Active) / Total Days in Month
```

**Example:**
- Monthly cost: $30
- Plan starts: June 15, 2024
- Days active: 16 (June 15-30)
- Total days in June: 30
- Prorated cost: ($30 × 16) / 30 = $16.00

### Cost Calculation

**Monthly Cost:**
1. For each plan, check if active in the month
2. Calculate prorated cost if partial month
3. Sum all plan costs

**Yearly Cost:**
1. Calculate monthly cost for each month (1-12)
2. Sum all monthly costs

**Time Complexity:**
- Monthly cost: O(P) where P = number of plans
- Yearly cost: O(12 × P) = O(P)

**Space Complexity:** O(P) for storing plans

## 🎯 Design Decisions

### 1. Immutable Plan Dates
- **Pros**: Thread-safe, prevents accidental modification
- **Cons**: Need to create new plan for changes
- **Decision**: Use immutable dates for safety

### 2. Proration by Days
- **Pros**: Fair billing, accurate cost calculation
- **Cons**: More complex than full-month billing
- **Decision**: Implement proration for better customer experience

### 3. Separate Plan for Upgrades
- **Pros**: Clear audit trail, simple logic
- **Cons**: Multiple plan objects for same product
- **Decision**: Use separate plans with end dates for clarity

### 4. Current Date in Constructor
- **Pros**: Testable, can simulate different dates
- **Cons**: Extra parameter
- **Decision**: Required for determining future vs past months

### 5. Cost Breakdown by Product
- **Pros**: Useful for multi-product customers
- **Cons**: Additional computation
- **Decision**: Provide as optional feature

## 🧪 Testing

### Test Coverage

**PlanTest** (21 tests):
- Plan creation and validation
- Active date checking
- Proration calculations
- Leap year handling
- Equals/hashCode

**CostExplorerTest** (27 tests):
- Explorer creation and configuration
- Plan management (add, remove)
- Monthly cost calculations
- Yearly cost calculations
- Prorated billing
- Plan upgrades/downgrades
- Cost breakdown by product
- Complex scenarios

### Run Tests

```bash
# Run all tests
mvn test -pl cost-explorer-lld

# Run with coverage
mvn clean test jacoco:report -pl cost-explorer-lld
```

## 🎓 Interview Tips

### Key Points to Mention

1. **Proration Logic**:
   - Fair billing for partial months
   - Handles start/end dates correctly
   - Accounts for different month lengths

2. **Multiple Plans**:
   - Support for multiple products
   - Support for plan changes (upgrades)
   - Aggregate costs correctly

3. **Future Estimates**:
   - Distinguish between past and future months
   - Assume ongoing plans continue
   - Mark future months as "Estimated"

4. **Edge Cases**:
   - Leap years (February 29)
   - Plan starts on last day of month
   - Plan ends on first day of month
   - Multiple plans for same product

5. **Extensibility**:
   - Easy to add new products
   - Easy to add discounts/promotions
   - Easy to add usage-based billing

### Follow-up Questions

**Q: How to handle discounts or promotions?**
- Add `Discount` class with percentage or fixed amount
- Apply discounts in cost calculation
- Track discount validity period

**Q: How to handle usage-based billing?**
- Add `UsagePlan` subclass with usage tracking
- Calculate cost based on actual usage
- Combine with base subscription cost

**Q: How to handle annual plans?**
- Add `billingCycle` field (MONTHLY, ANNUAL)
- Calculate cost based on billing cycle
- Prorate annual plans differently

**Q: How to handle refunds or credits?**
- Add `Credit` class to track credits
- Deduct credits from monthly cost
- Track credit expiration

**Q: How to generate invoices?**
- Create `Invoice` class with line items
- Generate from monthly cost report
- Include tax calculations

**Q: How to handle multiple currencies?**
- Add `currency` field to Plan
- Convert to base currency for calculations
- Use exchange rate service

## 🚀 Running the Demo

```bash
# Compile
mvn clean compile -pl cost-explorer-lld

# Run demo
mvn exec:java -Dexec.mainClass="com.costexplorer.DriverApplication" \
  -pl cost-explorer-lld

# Run tests
mvn test -pl cost-explorer-lld
```

## 📈 Complexity Summary

| Operation | Time | Space |
|-----------|------|-------|
| addPlan() | O(1) | O(1) |
| getMonthlyCost() | O(P) | O(1) |
| getYearlyCost() | O(P) | O(1) |
| getMonthlyCostReport() | O(P) | O(P) |
| getYearlyCostReport() | O(P) | O(P) |
| getCostByProduct() | O(P) | O(K) |

Where:
- P = number of plans
- K = number of unique products

## 🎯 Real-World Applications

- **SaaS Billing**: Atlassian, Salesforce, Adobe
- **Cloud Services**: AWS, Azure, GCP cost estimation
- **Subscription Services**: Netflix, Spotify pricing
- **Telecom**: Mobile plan cost calculators
- **Insurance**: Premium calculators
- **Utilities**: Electricity/water bill estimation

---

**Status**: ✅ Production-ready implementation with comprehensive testing and documentation

**Test Results**: 48/48 tests passing

