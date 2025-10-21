package com.snakegame;

/**
 * Growth strategy where the snake grows when it eats food.
 * This is the optional scale-up feature.
 */
public class FoodGrowthStrategy implements GrowthStrategy {
    
    @Override
    public boolean shouldGrow(int moveCount, boolean ateFoodThisMove) {
        return ateFoodThisMove;
    }
    
    @Override
    public void reset() {
        // No state to reset
    }
}

