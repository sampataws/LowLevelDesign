package com.commodityprices;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for PriceDataPoint class.
 */
public class PriceDataPointTest {
    
    @Test
    public void testCreation_ValidData() {
        PriceDataPoint point = new PriceDataPoint(1000, 100.50);
        
        assertEquals(1000, point.getTimestamp());
        assertEquals(100.50, point.getPrice(), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreation_NegativeTimestamp() {
        new PriceDataPoint(-1, 100.00);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreation_NegativePrice() {
        new PriceDataPoint(1000, -100.00);
    }
    
    @Test
    public void testCompareTo_DifferentPrices() {
        PriceDataPoint point1 = new PriceDataPoint(1000, 100.00);
        PriceDataPoint point2 = new PriceDataPoint(2000, 110.00);
        
        // point2 has higher price, so it should come first (negative comparison)
        assertTrue(point1.compareTo(point2) > 0);
        assertTrue(point2.compareTo(point1) < 0);
    }
    
    @Test
    public void testCompareTo_SamePrice_DifferentTimestamps() {
        PriceDataPoint point1 = new PriceDataPoint(1000, 100.00);
        PriceDataPoint point2 = new PriceDataPoint(2000, 100.00);
        
        // Same price, so compare by timestamp (ascending)
        assertTrue(point1.compareTo(point2) < 0);
        assertTrue(point2.compareTo(point1) > 0);
    }
    
    @Test
    public void testCompareTo_SamePriceAndTimestamp() {
        PriceDataPoint point1 = new PriceDataPoint(1000, 100.00);
        PriceDataPoint point2 = new PriceDataPoint(1000, 100.00);
        
        assertEquals(0, point1.compareTo(point2));
    }
    
    @Test
    public void testSorting_ByPriceDescending() {
        List<PriceDataPoint> points = new ArrayList<>();
        points.add(new PriceDataPoint(1000, 100.00));
        points.add(new PriceDataPoint(2000, 110.00));
        points.add(new PriceDataPoint(3000, 95.00));
        points.add(new PriceDataPoint(4000, 105.00));
        
        Collections.sort(points);
        
        // Should be sorted by price descending
        assertEquals(110.00, points.get(0).getPrice(), 0.001);
        assertEquals(105.00, points.get(1).getPrice(), 0.001);
        assertEquals(100.00, points.get(2).getPrice(), 0.001);
        assertEquals(95.00, points.get(3).getPrice(), 0.001);
    }
    
    @Test
    public void testEquals_SameData() {
        PriceDataPoint point1 = new PriceDataPoint(1000, 100.00);
        PriceDataPoint point2 = new PriceDataPoint(1000, 100.00);
        
        assertEquals(point1, point2);
    }
    
    @Test
    public void testEquals_DifferentTimestamp() {
        PriceDataPoint point1 = new PriceDataPoint(1000, 100.00);
        PriceDataPoint point2 = new PriceDataPoint(2000, 100.00);
        
        assertNotEquals(point1, point2);
    }
    
    @Test
    public void testEquals_DifferentPrice() {
        PriceDataPoint point1 = new PriceDataPoint(1000, 100.00);
        PriceDataPoint point2 = new PriceDataPoint(1000, 110.00);
        
        assertNotEquals(point1, point2);
    }
    
    @Test
    public void testHashCode_SameData() {
        PriceDataPoint point1 = new PriceDataPoint(1000, 100.00);
        PriceDataPoint point2 = new PriceDataPoint(1000, 100.00);
        
        assertEquals(point1.hashCode(), point2.hashCode());
    }
    
    @Test
    public void testToString_ContainsData() {
        PriceDataPoint point = new PriceDataPoint(1000, 100.50);
        String str = point.toString();
        
        assertTrue(str.contains("1000"));
        assertTrue(str.contains("100.5"));
    }
}

