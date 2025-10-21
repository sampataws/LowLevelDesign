package com.snakegame;

import org.junit.Test;

import static org.junit.Assert.*;

public class PositionTest {
    
    @Test
    public void testPositionCreation() {
        Position pos = new Position(5, 10);
        assertEquals(5, pos.getX());
        assertEquals(10, pos.getY());
    }
    
    @Test
    public void testMoveUp() {
        Position pos = new Position(5, 5);
        Position newPos = pos.move(Direction.UP);
        
        assertEquals(5, newPos.getX());
        assertEquals(4, newPos.getY());
    }
    
    @Test
    public void testMoveDown() {
        Position pos = new Position(5, 5);
        Position newPos = pos.move(Direction.DOWN);
        
        assertEquals(5, newPos.getX());
        assertEquals(6, newPos.getY());
    }
    
    @Test
    public void testMoveLeft() {
        Position pos = new Position(5, 5);
        Position newPos = pos.move(Direction.LEFT);
        
        assertEquals(4, newPos.getX());
        assertEquals(5, newPos.getY());
    }
    
    @Test
    public void testMoveRight() {
        Position pos = new Position(5, 5);
        Position newPos = pos.move(Direction.RIGHT);
        
        assertEquals(6, newPos.getX());
        assertEquals(5, newPos.getY());
    }
    
    @Test
    public void testIsWithinBounds() {
        Position pos = new Position(5, 5);
        
        assertTrue(pos.isWithinBounds(10, 10));
        assertTrue(pos.isWithinBounds(6, 6));
        assertFalse(pos.isWithinBounds(5, 10));
        assertFalse(pos.isWithinBounds(10, 5));
    }
    
    @Test
    public void testBoundaryPositions() {
        Position topLeft = new Position(0, 0);
        assertTrue(topLeft.isWithinBounds(10, 10));
        
        Position bottomRight = new Position(9, 9);
        assertTrue(bottomRight.isWithinBounds(10, 10));
        
        Position outOfBounds = new Position(10, 10);
        assertFalse(outOfBounds.isWithinBounds(10, 10));
    }
    
    @Test
    public void testNegativePositions() {
        Position neg = new Position(-1, -1);
        assertFalse(neg.isWithinBounds(10, 10));
    }
    
    @Test
    public void testEquals() {
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(5, 5);
        Position pos3 = new Position(5, 6);
        
        assertEquals(pos1, pos2);
        assertNotEquals(pos1, pos3);
    }
    
    @Test
    public void testHashCode() {
        Position pos1 = new Position(5, 5);
        Position pos2 = new Position(5, 5);
        
        assertEquals(pos1.hashCode(), pos2.hashCode());
    }
    
    @Test
    public void testToString() {
        Position pos = new Position(5, 10);
        String str = pos.toString();
        
        assertTrue(str.contains("5"));
        assertTrue(str.contains("10"));
    }
    
    @Test
    public void testImmutability() {
        Position original = new Position(5, 5);
        Position moved = original.move(Direction.RIGHT);
        
        // Original should not change
        assertEquals(5, original.getX());
        assertEquals(5, original.getY());
        
        // New position should be different
        assertEquals(6, moved.getX());
        assertEquals(5, moved.getY());
    }
}

