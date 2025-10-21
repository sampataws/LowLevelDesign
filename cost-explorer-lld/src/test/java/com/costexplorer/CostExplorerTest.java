package com.costexplorer;

import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CostExplorerTest {

    private CostExplorer explorer;
    private LocalDate currentDate;

    @Before
    public void setUp() {
        currentDate = LocalDate.of(2024, 6, 15);
        explorer = new CostExplorer(currentDate);
    }

    @Test
    public void testCostExplorerCreation() {
        assertNotNull(explorer);
        assertEquals(currentDate, explorer.getCurrentDate());
    }

    @Test
    public void testCostExplorerCreation_DefaultDate() {
        CostExplorer defaultExplorer = new CostExplorer();
        assertNotNull(defaultExplorer.getCurrentDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCostExplorerCreation_NullDate() {
        new CostExplorer(null);
    }

    @Test
    public void testAddPlan() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        explorer.addPlan(plan);
        
        List<Plan> plans = explorer.getAllPlans();
        assertEquals(1, plans.size());
        assertEquals(plan, plans.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPlan_Null() {
        explorer.addPlan(null);
    }

    @Test
    public void testAddMultiplePlans() {
        Plan plan1 = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        Plan plan2 = new Plan("CONF-001", "Confluence", 15.0, LocalDate.of(2024, 1, 1));
        
        explorer.addPlan(plan1);
        explorer.addPlan(plan2);
        
        assertEquals(2, explorer.getAllPlans().size());
    }

    @Test
    public void testRemovePlan() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        explorer.addPlan(plan);
        
        assertTrue(explorer.removePlan("JIRA-001"));
        assertEquals(0, explorer.getAllPlans().size());
    }

    @Test
    public void testRemovePlan_NonExistent() {
        assertFalse(explorer.removePlan("NON-EXISTENT"));
    }

    @Test
    public void testGetActivePlans() {
        Plan plan1 = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        Plan plan2 = new Plan("CONF-001", "Confluence", 15.0, 
            LocalDate.of(2024, 3, 1), LocalDate.of(2024, 5, 31));
        Plan plan3 = new Plan("BB-001", "Bitbucket", 8.0, LocalDate.of(2024, 7, 1));
        
        explorer.addPlan(plan1);
        explorer.addPlan(plan2);
        explorer.addPlan(plan3);
        
        // Check active plans on June 15
        List<Plan> activePlans = explorer.getActivePlans(LocalDate.of(2024, 6, 15));
        assertEquals(1, activePlans.size());
        assertEquals(plan1, activePlans.get(0));
        
        // Check active plans on April 15
        activePlans = explorer.getActivePlans(LocalDate.of(2024, 4, 15));
        assertEquals(2, activePlans.size());
    }

    @Test
    public void testGetMonthlyCost_SinglePlan() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        explorer.addPlan(plan);
        
        assertEquals(10.0, explorer.getMonthlyCost(2024, 1), 0.01);
        assertEquals(10.0, explorer.getMonthlyCost(2024, 6), 0.01);
    }

    @Test
    public void testGetMonthlyCost_MultiplePlans() {
        Plan plan1 = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        Plan plan2 = new Plan("CONF-001", "Confluence", 15.0, LocalDate.of(2024, 1, 1));
        
        explorer.addPlan(plan1);
        explorer.addPlan(plan2);
        
        assertEquals(25.0, explorer.getMonthlyCost(2024, 1), 0.01);
    }

    @Test
    public void testGetMonthlyCost_ProRated() {
        Plan plan = new Plan("JIRA-001", "Jira", 30.0, LocalDate.of(2024, 6, 15));
        explorer.addPlan(plan);
        
        // June has 30 days, plan starts on day 15, so 16 days active
        double expected = (30.0 * 16) / 30;
        assertEquals(expected, explorer.getMonthlyCost(2024, 6), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMonthlyCost_InvalidMonth() {
        explorer.getMonthlyCost(2024, 13);
    }

    @Test
    public void testGetYearlyCost_SinglePlan() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        explorer.addPlan(plan);
        
        assertEquals(120.0, explorer.getYearlyCost(2024), 0.01);
    }

    @Test
    public void testGetYearlyCost_MultiplePlans() {
        Plan plan1 = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        Plan plan2 = new Plan("CONF-001", "Confluence", 15.0, LocalDate.of(2024, 1, 1));
        
        explorer.addPlan(plan1);
        explorer.addPlan(plan2);
        
        assertEquals(300.0, explorer.getYearlyCost(2024), 0.01);
    }

    @Test
    public void testGetYearlyCost_PartialYear() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 6, 1));
        explorer.addPlan(plan);
        
        // Active for 7 months (June - December)
        assertEquals(70.0, explorer.getYearlyCost(2024), 0.01);
    }

    @Test
    public void testGetYearlyCost_DefaultYear() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        explorer.addPlan(plan);
        
        assertEquals(120.0, explorer.getYearlyCost(), 0.01);
    }

    @Test
    public void testGetMonthlyCostReport() {
        Plan plan1 = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        Plan plan2 = new Plan("CONF-001", "Confluence", 15.0, LocalDate.of(2024, 1, 1));
        
        explorer.addPlan(plan1);
        explorer.addPlan(plan2);
        
        MonthlyCostReport report = explorer.getMonthlyCostReport(2024, 6);
        
        assertEquals(2024, report.getYear());
        assertEquals(6, report.getMonth());
        assertEquals(25.0, report.getTotalCost(), 0.01);
        assertEquals(2, report.getPlanDetails().size());
    }

    @Test
    public void testGetMonthlyCostReport_FutureMonth() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        explorer.addPlan(plan);
        
        MonthlyCostReport report = explorer.getMonthlyCostReport(2024, 7);
        assertTrue(report.isFuture());
    }

    @Test
    public void testGetMonthlyCostReport_PastMonth() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        explorer.addPlan(plan);
        
        MonthlyCostReport report = explorer.getMonthlyCostReport(2024, 5);
        assertFalse(report.isFuture());
    }

    @Test
    public void testGetYearlyCostReport() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        explorer.addPlan(plan);
        
        YearlyCostReport report = explorer.getYearlyCostReport(2024);
        
        assertEquals(2024, report.getYear());
        assertEquals(120.0, report.getTotalCost(), 0.01);
        assertEquals(12, report.getMonthlyReports().size());
    }

    @Test
    public void testGetCostByProduct_SingleProduct() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        explorer.addPlan(plan);
        
        Map<String, Double> costByProduct = explorer.getCostByProduct(2024, 6);
        
        assertEquals(1, costByProduct.size());
        assertEquals(10.0, costByProduct.get("Jira"), 0.01);
    }

    @Test
    public void testGetCostByProduct_MultipleProducts() {
        Plan plan1 = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        Plan plan2 = new Plan("CONF-001", "Confluence", 15.0, LocalDate.of(2024, 1, 1));
        
        explorer.addPlan(plan1);
        explorer.addPlan(plan2);
        
        Map<String, Double> costByProduct = explorer.getCostByProduct(2024, 6);
        
        assertEquals(2, costByProduct.size());
        assertEquals(10.0, costByProduct.get("Jira"), 0.01);
        assertEquals(15.0, costByProduct.get("Confluence"), 0.01);
    }

    @Test
    public void testGetCostByProduct_MultiplePlansForSameProduct() {
        Plan plan1 = new Plan("JIRA-STD-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        Plan plan2 = new Plan("JIRA-ADD-001", "Jira", 5.0, LocalDate.of(2024, 1, 1));
        
        explorer.addPlan(plan1);
        explorer.addPlan(plan2);
        
        Map<String, Double> costByProduct = explorer.getCostByProduct(2024, 6);
        
        assertEquals(1, costByProduct.size());
        assertEquals(15.0, costByProduct.get("Jira"), 0.01);
    }

    @Test
    public void testGetYearlyCostByProduct() {
        Plan plan1 = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        Plan plan2 = new Plan("CONF-001", "Confluence", 15.0, LocalDate.of(2024, 1, 1));
        
        explorer.addPlan(plan1);
        explorer.addPlan(plan2);
        
        Map<String, Double> costByProduct = explorer.getYearlyCostByProduct(2024);
        
        assertEquals(2, costByProduct.size());
        assertEquals(120.0, costByProduct.get("Jira"), 0.01);
        assertEquals(180.0, costByProduct.get("Confluence"), 0.01);
    }

    @Test
    public void testPlanUpgrade() {
        // Customer upgrades from Standard to Premium mid-year
        Plan standardPlan = new Plan("JIRA-STD-001", "Jira", 10.0, 
            LocalDate.of(2024, 1, 1), LocalDate.of(2024, 5, 31));
        Plan premiumPlan = new Plan("JIRA-PRE-001", "Jira", 25.0, 
            LocalDate.of(2024, 6, 1));
        
        explorer.addPlan(standardPlan);
        explorer.addPlan(premiumPlan);
        
        // May should have standard plan cost
        assertEquals(10.0, explorer.getMonthlyCost(2024, 5), 0.01);
        
        // June should have premium plan cost
        assertEquals(25.0, explorer.getMonthlyCost(2024, 6), 0.01);
        
        // Yearly cost should be 5 months standard + 7 months premium
        double expectedYearly = (10.0 * 5) + (25.0 * 7);
        assertEquals(expectedYearly, explorer.getYearlyCost(2024), 0.01);
    }

    @Test
    public void testComplexScenario() {
        // Customer has multiple products with different start dates and upgrades
        Plan jiraStd = new Plan("JIRA-STD-001", "Jira", 10.0, 
            LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30));
        Plan jiraPro = new Plan("JIRA-PRO-001", "Jira", 20.0, 
            LocalDate.of(2024, 7, 1));
        Plan confluence = new Plan("CONF-001", "Confluence", 15.0, 
            LocalDate.of(2024, 3, 1));
        Plan bitbucket = new Plan("BB-001", "Bitbucket", 8.0, 
            LocalDate.of(2024, 6, 15));
        
        explorer.addPlan(jiraStd);
        explorer.addPlan(jiraPro);
        explorer.addPlan(confluence);
        explorer.addPlan(bitbucket);
        
        // January: Only Jira Standard
        assertEquals(10.0, explorer.getMonthlyCost(2024, 1), 0.01);
        
        // March: Jira Standard + Confluence
        assertEquals(25.0, explorer.getMonthlyCost(2024, 3), 0.01);
        
        // June: Jira Standard + Confluence + Bitbucket (prorated)
        double bitbucketJuneCost = (8.0 * 16) / 30; // 16 days in June
        assertEquals(10.0 + 15.0 + bitbucketJuneCost, explorer.getMonthlyCost(2024, 6), 0.01);
        
        // July: Jira Pro + Confluence + Bitbucket
        assertEquals(20.0 + 15.0 + 8.0, explorer.getMonthlyCost(2024, 7), 0.01);
    }
}

