package com.costexplorer;

import java.time.LocalDate;
import java.util.Map;

/**
 * Driver application demonstrating the Cost Explorer functionality.
 */
public class DriverApplication {
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("COST EXPLORER - SUBSCRIPTION BILLING SYSTEM");
        System.out.println("=".repeat(80));
        System.out.println();

        // Demo 1: Basic subscription with single plan
        demo1_BasicSubscription();

        // Demo 2: Multiple plans for different products
        demo2_MultiplePlans();

        // Demo 3: Mid-year subscription (prorated billing)
        demo3_MidYearSubscription();

        // Demo 4: Plan changes during the year
        demo4_PlanChanges();

        // Demo 5: Cost breakdown by product
        demo5_CostBreakdown();
    }

    private static void demo1_BasicSubscription() {
        System.out.println("📋 Demo 1: Basic Subscription with Single Plan");
        System.out.println("-".repeat(80));

        // Customer subscribes to Jira on January 1, 2024
        LocalDate currentDate = LocalDate.of(2024, 6, 15); // Mid-year check
        CostExplorer explorer = new CostExplorer(currentDate);

        // Add Jira Standard plan - $10/month
        Plan jiraPlan = new Plan("JIRA-STD-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        explorer.addPlan(jiraPlan);

        // Get yearly report
        YearlyCostReport yearlyReport = explorer.getYearlyCostReport();
        System.out.println(yearlyReport);

        System.out.println("Total yearly cost: $" + String.format("%.2f", explorer.getYearlyCost()));
        System.out.println();
    }

    private static void demo2_MultiplePlans() {
        System.out.println("📋 Demo 2: Multiple Plans for Different Products");
        System.out.println("-".repeat(80));

        LocalDate currentDate = LocalDate.of(2024, 3, 20);
        CostExplorer explorer = new CostExplorer(currentDate);

        // Customer has multiple Atlassian products
        Plan jiraPlan = new Plan("JIRA-STD-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        Plan confluencePlan = new Plan("CONF-STD-001", "Confluence", 15.0, LocalDate.of(2024, 1, 1));
        Plan bitbucketPlan = new Plan("BB-STD-001", "Bitbucket", 8.0, LocalDate.of(2024, 2, 1));

        explorer.addPlan(jiraPlan);
        explorer.addPlan(confluencePlan);
        explorer.addPlan(bitbucketPlan);

        // Get monthly report for March
        MonthlyCostReport marchReport = explorer.getMonthlyCostReport(2024, 3);
        System.out.println("March 2024 Cost Report:");
        System.out.println(marchReport);

        // Get yearly report
        System.out.println("Total yearly cost: $" + String.format("%.2f", explorer.getYearlyCost()));
        System.out.println();
    }

    private static void demo3_MidYearSubscription() {
        System.out.println("📋 Demo 3: Mid-Year Subscription (Prorated Billing)");
        System.out.println("-".repeat(80));

        LocalDate currentDate = LocalDate.of(2024, 6, 15);
        CostExplorer explorer = new CostExplorer(currentDate);

        // Customer subscribes on June 15, 2024 - should be prorated
        Plan jiraPlan = new Plan("JIRA-PRO-001", "Jira", 30.0, LocalDate.of(2024, 6, 15));
        explorer.addPlan(jiraPlan);

        // Get June report (prorated)
        MonthlyCostReport juneReport = explorer.getMonthlyCostReport(2024, 6);
        System.out.println("June 2024 Cost Report (Prorated):");
        System.out.println(juneReport);
        System.out.println("Note: Customer subscribed on June 15, so cost is prorated for 16 days.");
        System.out.println();

        // Get July report (full month)
        MonthlyCostReport julyReport = explorer.getMonthlyCostReport(2024, 7);
        System.out.println("July 2024 Cost Report (Full Month):");
        System.out.println(julyReport);

        System.out.println("Total yearly cost: $" + String.format("%.2f", explorer.getYearlyCost()));
        System.out.println();
    }

    private static void demo4_PlanChanges() {
        System.out.println("📋 Demo 4: Plan Changes During the Year");
        System.out.println("-".repeat(80));

        LocalDate currentDate = LocalDate.of(2024, 8, 10);
        CostExplorer explorer = new CostExplorer(currentDate);

        // Customer starts with Standard plan, upgrades to Premium in June
        Plan jiraStandard = new Plan("JIRA-STD-001", "Jira", 10.0, 
            LocalDate.of(2024, 1, 1), LocalDate.of(2024, 5, 31));
        Plan jiraPremium = new Plan("JIRA-PRE-001", "Jira", 25.0, 
            LocalDate.of(2024, 6, 1));

        explorer.addPlan(jiraStandard);
        explorer.addPlan(jiraPremium);

        // Show costs for May, June, July
        System.out.println("May 2024 (Standard Plan):");
        System.out.println(explorer.getMonthlyCostReport(2024, 5));

        System.out.println("June 2024 (Upgraded to Premium):");
        System.out.println(explorer.getMonthlyCostReport(2024, 6));

        System.out.println("July 2024 (Premium Plan):");
        System.out.println(explorer.getMonthlyCostReport(2024, 7));

        System.out.println("Total yearly cost: $" + String.format("%.2f", explorer.getYearlyCost()));
        System.out.println();
    }

    private static void demo5_CostBreakdown() {
        System.out.println("📋 Demo 5: Cost Breakdown by Product");
        System.out.println("-".repeat(80));

        LocalDate currentDate = LocalDate.of(2024, 9, 1);
        CostExplorer explorer = new CostExplorer(currentDate);

        // Customer has multiple products with multiple plans
        explorer.addPlan(new Plan("JIRA-STD-001", "Jira", 10.0, LocalDate.of(2024, 1, 1)));
        explorer.addPlan(new Plan("JIRA-ADD-001", "Jira", 5.0, LocalDate.of(2024, 3, 1))); // Add-on
        explorer.addPlan(new Plan("CONF-STD-001", "Confluence", 15.0, LocalDate.of(2024, 1, 1)));
        explorer.addPlan(new Plan("BB-STD-001", "Bitbucket", 8.0, LocalDate.of(2024, 2, 1)));

        // Get cost breakdown by product for the year
        Map<String, Double> costByProduct = explorer.getYearlyCostByProduct(2024);

        System.out.println("Yearly Cost Breakdown by Product:");
        for (Map.Entry<String, Double> entry : costByProduct.entrySet()) {
            System.out.println(String.format("  %s: $%.2f", entry.getKey(), entry.getValue()));
        }

        System.out.println();
        System.out.println("Total yearly cost: $" + String.format("%.2f", explorer.getYearlyCost()));
        System.out.println();

        // Show detailed yearly report
        YearlyCostReport yearlyReport = explorer.getYearlyCostReport();
        System.out.println(yearlyReport);
    }
}

