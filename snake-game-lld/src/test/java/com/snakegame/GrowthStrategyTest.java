package com.snakegame;

import org.junit.Test;

import static org.junit.Assert.*;

public class GrowthStrategyTest {
    
    // ========== FixedIntervalGrowthStrategy Tests ==========
    
    @Test
    public void testFixedIntervalDefaultInterval() {
        FixedIntervalGrowthStrategy strategy = new FixedIntervalGrowthStrategy();
        assertEquals(5, strategy.getInterval());
    }
    
    @Test
    public void testFixedIntervalCustomInterval() {
        FixedIntervalGrowthStrategy strategy = new FixedIntervalGrowthStrategy(3);
        assertEquals(3, strategy.getInterval());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFixedIntervalInvalidInterval() {
        new FixedIntervalGrowthStrategy(0);
    }
    
    @Test
    public void testFixedIntervalGrowthAt5() {
        FixedIntervalGrowthStrategy strategy = new FixedIntervalGrowthStrategy(5);
        
        assertFalse(strategy.shouldGrow(0, false));
        assertFalse(strategy.shouldGrow(1, false));
        assertFalse(strategy.shouldGrow(2, false));
        assertFalse(strategy.shouldGrow(3, false));
        assertFalse(strategy.shouldGrow(4, false));
        assertTrue(strategy.shouldGrow(5, false));
        assertFalse(strategy.shouldGrow(6, false));
        assertFalse(strategy.shouldGrow(9, false));
        assertTrue(strategy.shouldGrow(10, false));
    }
    
    @Test
    public void testFixedIntervalIgnoresFood() {
        FixedIntervalGrowthStrategy strategy = new FixedIntervalGrowthStrategy(5);
        
        // Food parameter should be ignored
        assertFalse(strategy.shouldGrow(1, true));
        assertFalse(strategy.shouldGrow(2, true));
        assertTrue(strategy.shouldGrow(5, true));
    }
    
    @Test
    public void testFixedIntervalCustomInterval3() {
        FixedIntervalGrowthStrategy strategy = new FixedIntervalGrowthStrategy(3);
        
        assertFalse(strategy.shouldGrow(1, false));
        assertFalse(strategy.shouldGrow(2, false));
        assertTrue(strategy.shouldGrow(3, false));
        assertFalse(strategy.shouldGrow(4, false));
        assertFalse(strategy.shouldGrow(5, false));
        assertTrue(strategy.shouldGrow(6, false));
    }
    
    // ========== FoodGrowthStrategy Tests ==========
    
    @Test
    public void testFoodGrowthOnlyWhenEating() {
        FoodGrowthStrategy strategy = new FoodGrowthStrategy();
        
        assertFalse(strategy.shouldGrow(1, false));
        assertFalse(strategy.shouldGrow(5, false));
        assertFalse(strategy.shouldGrow(10, false));
        
        assertTrue(strategy.shouldGrow(1, true));
        assertTrue(strategy.shouldGrow(5, true));
        assertTrue(strategy.shouldGrow(10, true));
    }
    
    @Test
    public void testFoodGrowthIgnoresMoveCount() {
        FoodGrowthStrategy strategy = new FoodGrowthStrategy();
        
        // Move count should be ignored
        assertFalse(strategy.shouldGrow(0, false));
        assertFalse(strategy.shouldGrow(100, false));
        
        assertTrue(strategy.shouldGrow(0, true));
        assertTrue(strategy.shouldGrow(100, true));
    }
    
    @Test
    public void testFoodGrowthReset() {
        FoodGrowthStrategy strategy = new FoodGrowthStrategy();
        
        strategy.reset();  // Should not throw
        
        // Behavior should be same after reset
        assertTrue(strategy.shouldGrow(1, true));
        assertFalse(strategy.shouldGrow(1, false));
    }
    
    @Test
    public void testFixedIntervalReset() {
        FixedIntervalGrowthStrategy strategy = new FixedIntervalGrowthStrategy(5);
        
        strategy.reset();  // Should not throw
        
        // Behavior should be same after reset
        assertTrue(strategy.shouldGrow(5, false));
        assertFalse(strategy.shouldGrow(1, false));
    }
}

