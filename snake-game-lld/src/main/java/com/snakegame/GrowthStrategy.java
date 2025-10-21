package com.snakegame;

/**
 * Strategy interface for determining when the snake should grow.
 */
public interface GrowthStrategy {
    
    /**
     * Determines if the snake should grow on this move.
     * 
     * @param moveCount the current move count
     * @param ateFoodThisMove whether the snake ate food this move
     * @return true if the snake should grow
     */
    boolean shouldGrow(int moveCount, boolean ateFoodThisMove);
    
    /**
     * Resets the growth strategy state.
     */
    void reset();
}

