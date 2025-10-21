package com.snakegame;

import org.junit.Test;

import static org.junit.Assert.*;

public class DirectionTest {
    
    @Test
    public void testDirectionDeltas() {
        assertEquals(0, Direction.UP.getDx());
        assertEquals(-1, Direction.UP.getDy());
        
        assertEquals(0, Direction.DOWN.getDx());
        assertEquals(1, Direction.DOWN.getDy());
        
        assertEquals(-1, Direction.LEFT.getDx());
        assertEquals(0, Direction.LEFT.getDy());
        
        assertEquals(1, Direction.RIGHT.getDx());
        assertEquals(0, Direction.RIGHT.getDy());
    }
    
    @Test
    public void testOppositeDirections() {
        assertEquals(Direction.DOWN, Direction.UP.opposite());
        assertEquals(Direction.UP, Direction.DOWN.opposite());
        assertEquals(Direction.RIGHT, Direction.LEFT.opposite());
        assertEquals(Direction.LEFT, Direction.RIGHT.opposite());
    }
    
    @Test
    public void testOppositeIsSymmetric() {
        for (Direction dir : Direction.values()) {
            assertEquals(dir, dir.opposite().opposite());
        }
    }
}

