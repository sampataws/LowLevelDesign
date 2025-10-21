package com.customersatisfaction;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.junit.Assert.*;

public class RatingTest {
    
    @Test
    public void testValidRating() {
        Rating rating = new Rating("agent1", 5);
        assertEquals("agent1", rating.getAgentId());
        assertEquals(5, rating.getScore());
        assertNotNull(rating.getTimestamp());
    }
    
    @Test
    public void testValidRatingWithTimestamp() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 10, 30);
        Rating rating = new Rating("agent1", 3, timestamp);
        assertEquals("agent1", rating.getAgentId());
        assertEquals(3, rating.getScore());
        assertEquals(timestamp, rating.getTimestamp());
    }
    
    @Test
    public void testAllValidScores() {
        for (int score = 1; score <= 5; score++) {
            Rating rating = new Rating("agent1", score);
            assertEquals(score, rating.getScore());
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testScoreTooLow() {
        new Rating("agent1", 0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testScoreTooHigh() {
        new Rating("agent1", 6);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeScore() {
        new Rating("agent1", -1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullAgentId() {
        new Rating(null, 5);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyAgentId() {
        new Rating("", 5);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWhitespaceAgentId() {
        new Rating("   ", 5);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullTimestamp() {
        new Rating("agent1", 5, null);
    }
    
    @Test
    public void testGetYearMonth() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 3, 15, 10, 30);
        Rating rating = new Rating("agent1", 5, timestamp);
        assertEquals(YearMonth.of(2024, 3), rating.getYearMonth());
    }
    
    @Test
    public void testToString() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 15, 10, 30);
        Rating rating = new Rating("agent1", 5, timestamp);
        String str = rating.toString();
        assertTrue(str.contains("agent1"));
        assertTrue(str.contains("5"));
    }
}

