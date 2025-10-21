package com.costexplorer;

import org.junit.Test;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class PlanTest {

    @Test
    public void testPlanCreation() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        
        assertEquals("JIRA-001", plan.getPlanId());
        assertEquals("Jira", plan.getProductName());
        assertEquals(10.0, plan.getMonthlyCost(), 0.01);
        assertEquals(LocalDate.of(2024, 1, 1), plan.getStartDate());
        assertNull(plan.getEndDate());
    }

    @Test
    public void testPlanCreationWithEndDate() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, 
            LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        
        assertEquals(LocalDate.of(2024, 12, 31), plan.getEndDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlanCreation_NullPlanId() {
        new Plan(null, "Jira", 10.0, LocalDate.of(2024, 1, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlanCreation_EmptyPlanId() {
        new Plan("", "Jira", 10.0, LocalDate.of(2024, 1, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlanCreation_NullProductName() {
        new Plan("JIRA-001", null, 10.0, LocalDate.of(2024, 1, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlanCreation_NegativeCost() {
        new Plan("JIRA-001", "Jira", -10.0, LocalDate.of(2024, 1, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlanCreation_NullStartDate() {
        new Plan("JIRA-001", "Jira", 10.0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlanCreation_EndDateBeforeStartDate() {
        new Plan("JIRA-001", "Jira", 10.0, 
            LocalDate.of(2024, 6, 1), LocalDate.of(2024, 1, 1));
    }

    @Test
    public void testIsActiveOn_OngoingPlan() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        
        assertFalse(plan.isActiveOn(LocalDate.of(2023, 12, 31)));
        assertTrue(plan.isActiveOn(LocalDate.of(2024, 1, 1)));
        assertTrue(plan.isActiveOn(LocalDate.of(2024, 6, 15)));
        assertTrue(plan.isActiveOn(LocalDate.of(2025, 1, 1)));
    }

    @Test
    public void testIsActiveOn_PlanWithEndDate() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, 
            LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        
        assertFalse(plan.isActiveOn(LocalDate.of(2023, 12, 31)));
        assertTrue(plan.isActiveOn(LocalDate.of(2024, 1, 1)));
        assertTrue(plan.isActiveOn(LocalDate.of(2024, 6, 15)));
        assertTrue(plan.isActiveOn(LocalDate.of(2024, 12, 31)));
        assertFalse(plan.isActiveOn(LocalDate.of(2025, 1, 1)));
    }

    @Test
    public void testIsActiveInMonth_FullMonth() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        
        assertTrue(plan.isActiveInMonth(2024, 1));
        assertTrue(plan.isActiveInMonth(2024, 6));
        assertTrue(plan.isActiveInMonth(2024, 12));
    }

    @Test
    public void testIsActiveInMonth_PartialMonth() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 6, 15));
        
        assertFalse(plan.isActiveInMonth(2024, 5));
        assertTrue(plan.isActiveInMonth(2024, 6));
        assertTrue(plan.isActiveInMonth(2024, 7));
    }

    @Test
    public void testIsActiveInMonth_WithEndDate() {
        Plan plan = new Plan("JIRA-001", "Jira", 10.0, 
            LocalDate.of(2024, 3, 1), LocalDate.of(2024, 8, 31));
        
        assertFalse(plan.isActiveInMonth(2024, 2));
        assertTrue(plan.isActiveInMonth(2024, 3));
        assertTrue(plan.isActiveInMonth(2024, 6));
        assertTrue(plan.isActiveInMonth(2024, 8));
        assertFalse(plan.isActiveInMonth(2024, 9));
    }

    @Test
    public void testGetCostForMonth_FullMonth() {
        Plan plan = new Plan("JIRA-001", "Jira", 30.0, LocalDate.of(2024, 1, 1));
        
        assertEquals(30.0, plan.getCostForMonth(2024, 2), 0.01);
        assertEquals(30.0, plan.getCostForMonth(2024, 6), 0.01);
    }

    @Test
    public void testGetCostForMonth_PartialMonth_StartMidMonth() {
        Plan plan = new Plan("JIRA-001", "Jira", 30.0, LocalDate.of(2024, 6, 15));
        
        // June has 30 days, plan starts on day 15, so 16 days active (15-30)
        double expected = (30.0 * 16) / 30;
        assertEquals(expected, plan.getCostForMonth(2024, 6), 0.01);
    }

    @Test
    public void testGetCostForMonth_PartialMonth_EndMidMonth() {
        Plan plan = new Plan("JIRA-001", "Jira", 30.0, 
            LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 15));
        
        // June has 30 days, plan active for 15 days (1-15)
        double expected = (30.0 * 15) / 30;
        assertEquals(expected, plan.getCostForMonth(2024, 6), 0.01);
    }

    @Test
    public void testGetCostForMonth_NotActiveInMonth() {
        Plan plan = new Plan("JIRA-001", "Jira", 30.0, 
            LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31));
        
        assertEquals(0.0, plan.getCostForMonth(2024, 5), 0.01);
        assertEquals(0.0, plan.getCostForMonth(2024, 9), 0.01);
    }

    @Test
    public void testGetCostForMonth_LeapYear() {
        Plan plan = new Plan("JIRA-001", "Jira", 29.0, LocalDate.of(2024, 2, 1));
        
        // 2024 is a leap year, February has 29 days
        assertEquals(29.0, plan.getCostForMonth(2024, 2), 0.01);
    }

    @Test
    public void testGetCostForMonth_ProrationAccuracy() {
        Plan plan = new Plan("JIRA-001", "Jira", 31.0, LocalDate.of(2024, 1, 16));
        
        // January has 31 days, plan starts on day 16, so 16 days active (16-31)
        double expected = (31.0 * 16) / 31;
        assertEquals(expected, plan.getCostForMonth(2024, 1), 0.01);
    }

    @Test
    public void testEquals() {
        Plan plan1 = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        Plan plan2 = new Plan("JIRA-001", "Jira", 20.0, LocalDate.of(2024, 2, 1));
        Plan plan3 = new Plan("JIRA-002", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        
        assertEquals(plan1, plan2); // Same plan ID
        assertNotEquals(plan1, plan3); // Different plan ID
    }

    @Test
    public void testHashCode() {
        Plan plan1 = new Plan("JIRA-001", "Jira", 10.0, LocalDate.of(2024, 1, 1));
        Plan plan2 = new Plan("JIRA-001", "Jira", 20.0, LocalDate.of(2024, 2, 1));
        
        assertEquals(plan1.hashCode(), plan2.hashCode());
    }
}

