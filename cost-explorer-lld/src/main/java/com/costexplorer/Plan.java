package com.costexplorer;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a subscription plan for a product.
 * A plan has a monthly cost and can be active for a specific period.
 */
public class Plan {
    private final String planId;
    private final String productName;
    private final double monthlyCost;
    private final LocalDate startDate;
    private final LocalDate endDate; // null means ongoing

    public Plan(String planId, String productName, double monthlyCost, LocalDate startDate, LocalDate endDate) {
        if (planId == null || planId.trim().isEmpty()) {
            throw new IllegalArgumentException("Plan ID cannot be null or empty");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (monthlyCost < 0) {
            throw new IllegalArgumentException("Monthly cost cannot be negative");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        this.planId = planId;
        this.productName = productName;
        this.monthlyCost = monthlyCost;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Plan(String planId, String productName, double monthlyCost, LocalDate startDate) {
        this(planId, productName, monthlyCost, startDate, null);
    }

    public String getPlanId() {
        return planId;
    }

    public String getProductName() {
        return productName;
    }

    public double getMonthlyCost() {
        return monthlyCost;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Check if the plan is active on a given date.
     */
    public boolean isActiveOn(LocalDate date) {
        if (date.isBefore(startDate)) {
            return false;
        }
        if (endDate != null && date.isAfter(endDate)) {
            return false;
        }
        return true;
    }

    /**
     * Check if the plan is active during a given month.
     */
    public boolean isActiveInMonth(int year, int month) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        // Plan is active if it overlaps with the month
        if (endDate != null && endDate.isBefore(monthStart)) {
            return false;
        }
        if (startDate.isAfter(monthEnd)) {
            return false;
        }
        return true;
    }

    /**
     * Calculate the prorated cost for a given month.
     * If the plan is active for the entire month, returns full monthly cost.
     * Otherwise, prorates based on the number of days active.
     */
    public double getCostForMonth(int year, int month) {
        if (!isActiveInMonth(year, month)) {
            return 0.0;
        }

        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        // Determine the actual start and end dates for billing
        LocalDate billingStart = startDate.isAfter(monthStart) ? startDate : monthStart;
        LocalDate billingEnd = (endDate != null && endDate.isBefore(monthEnd)) ? endDate : monthEnd;

        // Calculate days active
        int totalDaysInMonth = monthStart.lengthOfMonth();
        int daysActive = (int) (billingEnd.toEpochDay() - billingStart.toEpochDay() + 1);

        // Prorate the cost
        return (monthlyCost * daysActive) / totalDaysInMonth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Plan)) return false;
        Plan plan = (Plan) o;
        return planId.equals(plan.planId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planId);
    }

    @Override
    public String toString() {
        return "Plan{" +
                "planId='" + planId + '\'' +
                ", productName='" + productName + '\'' +
                ", monthlyCost=" + monthlyCost +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}

