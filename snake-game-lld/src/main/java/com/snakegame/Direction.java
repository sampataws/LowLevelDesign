package com.snakegame;

/**
 * Represents the four possible directions the snake can move.
 */
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);
    
    private final int dx;
    private final int dy;
    
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
    
    public int getDx() {
        return dx;
    }
    
    public int getDy() {
        return dy;
    }
    
    /**
     * Returns the opposite direction.
     */
    public Direction opposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: throw new IllegalStateException("Unknown direction: " + this);
        }
    }
}

