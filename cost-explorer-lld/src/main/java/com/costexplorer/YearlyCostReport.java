package com.costexplorer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a yearly cost report with monthly breakdown.
 */
public class YearlyCostReport {
    private final int year;
    private final double totalCost;
    private final List<MonthlyCostReport> monthlyReports;

    public YearlyCostReport(int year, double totalCost, List<MonthlyCostReport> monthlyReports) {
        this.year = year;
        this.totalCost = totalCost;
        this.monthlyReports = new ArrayList<>(monthlyReports);
    }

    public int getYear() {
        return year;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public List<MonthlyCostReport> getMonthlyReports() {
        return Collections.unmodifiableList(monthlyReports);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== Yearly Cost Report for %d ===\n", year));
        sb.append(String.format("Total Estimated Cost: $%.2f\n\n", totalCost));
        sb.append("Monthly Breakdown:\n");
        
        for (MonthlyCostReport report : monthlyReports) {
            sb.append(report.toString());
        }
        
        return sb.toString();
    }
}

