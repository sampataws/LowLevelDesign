package com.costexplorer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CostExplorer calculates the total cost a customer has to pay in a unit year.
 * Provides monthly and yearly cost estimates for subscription plans.
 */
public class CostExplorer {
    private final List<Plan> plans;
    private final LocalDate currentDate;

    public CostExplorer(LocalDate currentDate) {
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }
        this.plans = new ArrayList<>();
        this.currentDate = currentDate;
    }

    public CostExplorer() {
        this(LocalDate.now());
    }

    /**
     * Add a subscription plan to the cost explorer.
     */
    public void addPlan(Plan plan) {
        if (plan == null) {
            throw new IllegalArgumentException("Plan cannot be null");
        }
        plans.add(plan);
    }

    /**
     * Remove a plan by plan ID.
     */
    public boolean removePlan(String planId) {
        return plans.removeIf(plan -> plan.getPlanId().equals(planId));
    }

    /**
     * Get all active plans on a specific date.
     */
    public List<Plan> getActivePlans(LocalDate date) {
        List<Plan> activePlans = new ArrayList<>();
        for (Plan plan : plans) {
            if (plan.isActiveOn(date)) {
                activePlans.add(plan);
            }
        }
        return activePlans;
    }

    /**
     * Generate a monthly cost report for a specific month.
     * 
     * @param year The year
     * @param month The month (1-12)
     * @return Monthly cost report with breakdown by plan
     */
    public MonthlyCostReport getMonthlyCostReport(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        List<MonthlyCostReport.PlanCostDetail> planDetails = new ArrayList<>();
        double totalCost = 0.0;

        for (Plan plan : plans) {
            double cost = plan.getCostForMonth(year, month);
            if (cost > 0) {
                planDetails.add(new MonthlyCostReport.PlanCostDetail(
                    plan.getPlanId(),
                    plan.getProductName(),
                    cost
                ));
                totalCost += cost;
            }
        }

        // Check if this month is in the future
        LocalDate monthDate = LocalDate.of(year, month, 1);
        boolean isFuture = monthDate.isAfter(LocalDate.of(currentDate.getYear(), currentDate.getMonthValue(), 1));

        return new MonthlyCostReport(year, month, totalCost, planDetails, isFuture);
    }

    /**
     * Generate a yearly cost report for the unit year.
     * The unit year is the calendar year of the current date.
     * 
     * @return Yearly cost report with monthly breakdown
     */
    public YearlyCostReport getYearlyCostReport() {
        return getYearlyCostReport(currentDate.getYear());
    }

    /**
     * Generate a yearly cost report for a specific year.
     * 
     * @param year The year to generate the report for
     * @return Yearly cost report with monthly breakdown
     */
    public YearlyCostReport getYearlyCostReport(int year) {
        List<MonthlyCostReport> monthlyReports = new ArrayList<>();
        double totalYearlyCost = 0.0;

        for (int month = 1; month <= 12; month++) {
            MonthlyCostReport monthlyReport = getMonthlyCostReport(year, month);
            monthlyReports.add(monthlyReport);
            totalYearlyCost += monthlyReport.getTotalCost();
        }

        return new YearlyCostReport(year, totalYearlyCost, monthlyReports);
    }

    /**
     * Get the total cost for a specific month.
     */
    public double getMonthlyCost(int year, int month) {
        return getMonthlyCostReport(year, month).getTotalCost();
    }

    /**
     * Get the total yearly cost for the unit year.
     */
    public double getYearlyCost() {
        return getYearlyCost(currentDate.getYear());
    }

    /**
     * Get the total yearly cost for a specific year.
     */
    public double getYearlyCost(int year) {
        double total = 0.0;
        for (int month = 1; month <= 12; month++) {
            total += getMonthlyCost(year, month);
        }
        return total;
    }

    /**
     * Get cost breakdown by product for a specific month.
     */
    public Map<String, Double> getCostByProduct(int year, int month) {
        Map<String, Double> costByProduct = new HashMap<>();
        
        for (Plan plan : plans) {
            double cost = plan.getCostForMonth(year, month);
            if (cost > 0) {
                costByProduct.merge(plan.getProductName(), cost, Double::sum);
            }
        }
        
        return costByProduct;
    }

    /**
     * Get cost breakdown by product for the entire year.
     */
    public Map<String, Double> getYearlyCostByProduct(int year) {
        Map<String, Double> costByProduct = new HashMap<>();
        
        for (int month = 1; month <= 12; month++) {
            Map<String, Double> monthlyCostByProduct = getCostByProduct(year, month);
            for (Map.Entry<String, Double> entry : monthlyCostByProduct.entrySet()) {
                costByProduct.merge(entry.getKey(), entry.getValue(), Double::sum);
            }
        }
        
        return costByProduct;
    }

    /**
     * Get the current date used for calculations.
     */
    public LocalDate getCurrentDate() {
        return currentDate;
    }

    /**
     * Get all plans.
     */
    public List<Plan> getAllPlans() {
        return new ArrayList<>(plans);
    }
}

