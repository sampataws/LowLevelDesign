package com.snakegame;

/**
 * Growth strategy where the snake grows every N moves.
 * Default implementation: grows every 5 moves.
 */
public class FixedIntervalGrowthStrategy implements GrowthStrategy {
    
    private final int interval;
    
    /**
     * Creates a growth strategy with the default interval of 5 moves.
     */
    public FixedIntervalGrowthStrategy() {
        this(5);
    }
    
    /**
     * Creates a growth strategy with a custom interval.
     * 
     * @param interval the number of moves between growth
     */
    public FixedIntervalGrowthStrategy(int interval) {
        if (interval <= 0) {
            throw new IllegalArgumentException("Interval must be positive");
        }
        this.interval = interval;
    }
    
    @Override
    public boolean shouldGrow(int moveCount, boolean ateFoodThisMove) {
        return moveCount > 0 && moveCount % interval == 0;
    }
    
    @Override
    public void reset() {
        // No state to reset
    }
    
    public int getInterval() {
        return interval;
    }
}

