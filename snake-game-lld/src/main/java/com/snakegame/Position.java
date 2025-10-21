package com.snakegame;

import java.util.Objects;

/**
 * Represents a position on the game board.
 */
public class Position {
    private final int x;
    private final int y;
    
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    /**
     * Returns a new position by moving in the given direction.
     */
    public Position move(Direction direction) {
        return new Position(x + direction.getDx(), y + direction.getDy());
    }
    
    /**
     * Checks if this position is within the board bounds.
     */
    public boolean isWithinBounds(int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

