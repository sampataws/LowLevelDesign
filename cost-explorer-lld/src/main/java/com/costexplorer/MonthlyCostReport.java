package com.costexplorer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a monthly cost report with breakdown by plan.
 */
public class MonthlyCostReport {
    private final int year;
    private final int month;
    private final double totalCost;
    private final List<PlanCostDetail> planDetails;
    private final boolean isFuture;

    public MonthlyCostReport(int year, int month, double totalCost, List<PlanCostDetail> planDetails, boolean isFuture) {
        this.year = year;
        this.month = month;
        this.totalCost = totalCost;
        this.planDetails = new ArrayList<>(planDetails);
        this.isFuture = isFuture;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public List<PlanCostDetail> getPlanDetails() {
        return Collections.unmodifiableList(planDetails);
    }

    public boolean isFuture() {
        return isFuture;
    }

    public String getMonthName() {
        return LocalDate.of(year, month, 1).getMonth().toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s %d%s: $%.2f\n", 
            getMonthName(), year, isFuture ? " (Estimated)" : "", totalCost));
        
        for (PlanCostDetail detail : planDetails) {
            sb.append(String.format("  - %s (%s): $%.2f\n", 
                detail.getProductName(), detail.getPlanId(), detail.getCost()));
        }
        
        return sb.toString();
    }

    /**
     * Represents cost details for a specific plan in a month.
     */
    public static class PlanCostDetail {
        private final String planId;
        private final String productName;
        private final double cost;

        public PlanCostDetail(String planId, String productName, double cost) {
            this.planId = planId;
            this.productName = productName;
            this.cost = cost;
        }

        public String getPlanId() {
            return planId;
        }

        public String getProductName() {
            return productName;
        }

        public double getCost() {
            return cost;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PlanCostDetail)) return false;
            PlanCostDetail that = (PlanCostDetail) o;
            return Double.compare(that.cost, cost) == 0 &&
                    planId.equals(that.planId) &&
                    productName.equals(that.productName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(planId, productName, cost);
        }
    }
}

