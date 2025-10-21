package com.snakegame;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

/**
 * Main game class implementing the Snake game logic.
 * 
 * Features:
 * - Snake moves in 4 directions (UP, DOWN, LEFT, RIGHT)
 * - Initial size is 3
 * - Configurable growth strategy (every N moves or when eating food)
 * - Game ends when snake hits itself or goes out of bounds
 * - Optional food system for growth
 */
public class SnakeGame {
    
    private final int width;
    private final int height;
    private final Deque<Position> snake;
    private final Set<Position> snakeBody;
    private Direction currentDirection;
    private boolean gameOver;
    private int moveCount;
    private final GrowthStrategy growthStrategy;
    private Position food;
    private final Random random;
    private final boolean useFoodSystem;
    
    /**
     * Creates a new snake game with default settings.
     * - Board size: 20x20
     * - Initial position: center
     * - Initial direction: RIGHT
     * - Growth: every 5 moves
     * - No food system
     */
    public SnakeGame() {
        this(20, 20, new FixedIntervalGrowthStrategy(), false);
    }
    
    /**
     * Creates a new snake game with custom board size.
     * 
     * @param width board width
     * @param height board height
     */
    public SnakeGame(int width, int height) {
        this(width, height, new FixedIntervalGrowthStrategy(), false);
    }
    
    /**
     * Creates a new snake game with custom growth strategy.
     * 
     * @param width board width
     * @param height board height
     * @param growthStrategy the growth strategy to use
     * @param useFoodSystem whether to use the food system
     */
    public SnakeGame(int width, int height, GrowthStrategy growthStrategy, boolean useFoodSystem) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Board dimensions must be positive");
        }
        if (width < 3 || height < 3) {
            throw new IllegalArgumentException("Board must be at least 3x3 for initial snake");
        }
        if (growthStrategy == null) {
            throw new IllegalArgumentException("Growth strategy cannot be null");
        }
        
        this.width = width;
        this.height = height;
        this.growthStrategy = growthStrategy;
        this.useFoodSystem = useFoodSystem;
        this.snake = new LinkedList<>();
        this.snakeBody = new HashSet<>();
        this.random = new Random();
        this.gameOver = false;
        this.moveCount = 0;
        this.currentDirection = Direction.RIGHT;
        
        initializeSnake();
        
        if (useFoodSystem) {
            spawnFood();
        }
    }
    
    /**
     * Initializes the snake with size 3 at the center of the board.
     */
    private void initializeSnake() {
        int centerX = width / 2;
        int centerY = height / 2;
        
        // Snake starts with 3 segments, moving right
        // Head at center, body extends to the left
        for (int i = 2; i >= 0; i--) {
            Position pos = new Position(centerX - i, centerY);
            snake.addLast(pos);
            snakeBody.add(pos);
        }
    }
    
    /**
     * Spawns food at a random empty position on the board.
     */
    private void spawnFood() {
        if (snakeBody.size() >= width * height) {
            // Board is full, no space for food
            food = null;
            return;
        }
        
        Position newFood;
        do {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            newFood = new Position(x, y);
        } while (snakeBody.contains(newFood));
        
        food = newFood;
    }
    
    /**
     * Moves the snake in the current direction.
     * 
     * @return true if the move was successful, false if game over
     */
    public boolean moveSnake() {
        return moveSnake(currentDirection);
    }
    
    /**
     * Moves the snake in the specified direction.
     * 
     * @param direction the direction to move
     * @return true if the move was successful, false if game over
     */
    public boolean moveSnake(Direction direction) {
        if (gameOver) {
            return false;
        }
        
        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }
        
        // Prevent moving in opposite direction (would immediately hit itself)
        if (snake.size() > 1 && direction == currentDirection.opposite()) {
            direction = currentDirection;
        }
        
        currentDirection = direction;
        
        // Calculate new head position
        Position currentHead = snake.getLast();
        Position newHead = currentHead.move(direction);
        
        // Check if out of bounds
        if (!newHead.isWithinBounds(width, height)) {
            gameOver = true;
            return false;
        }

        // Increment move count before checking growth
        moveCount++;

        // Check if ate food
        boolean ateFood = useFoodSystem && newHead.equals(food);

        // Determine if snake should grow
        boolean shouldGrow = growthStrategy.shouldGrow(moveCount, ateFood);

        // If not growing, remove tail first (so we can check collision properly)
        Position removedTail = null;
        if (!shouldGrow) {
            removedTail = snake.removeFirst();
            snakeBody.remove(removedTail);
        }

        // Check if hit itself
        if (snakeBody.contains(newHead)) {
            gameOver = true;
            // Restore tail if we removed it
            if (removedTail != null) {
                snake.addFirst(removedTail);
                snakeBody.add(removedTail);
            }
            return false;
        }

        // Move successful - add new head
        snake.addLast(newHead);
        snakeBody.add(newHead);

        // If ate food and growing, spawn new food
        if (shouldGrow && ateFood) {
            spawnFood();
        }
        
        return true;
    }
    
    /**
     * Changes the snake's direction.
     * 
     * @param direction the new direction
     */
    public void setDirection(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }
        this.currentDirection = direction;
    }
    
    /**
     * Returns the current direction.
     */
    public Direction getDirection() {
        return currentDirection;
    }
    
    /**
     * Returns whether the game is over.
     */
    public boolean isGameOver() {
        return gameOver;
    }
    
    /**
     * Returns the current snake length.
     */
    public int getSnakeLength() {
        return snake.size();
    }
    
    /**
     * Returns the current move count.
     */
    public int getMoveCount() {
        return moveCount;
    }
    
    /**
     * Returns the snake's head position.
     */
    public Position getHead() {
        return snake.isEmpty() ? null : snake.getLast();
    }
    
    /**
     * Returns the snake's tail position.
     */
    public Position getTail() {
        return snake.isEmpty() ? null : snake.getFirst();
    }
    
    /**
     * Returns a copy of the snake body.
     */
    public Deque<Position> getSnake() {
        return new LinkedList<>(snake);
    }
    
    /**
     * Returns the food position (null if no food system or no food).
     */
    public Position getFood() {
        return food;
    }
    
    /**
     * Returns the board width.
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Returns the board height.
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Checks if a position is occupied by the snake.
     */
    public boolean isSnakeAt(Position position) {
        return snakeBody.contains(position);
    }
    
    /**
     * Resets the game to initial state.
     */
    public void reset() {
        snake.clear();
        snakeBody.clear();
        gameOver = false;
        moveCount = 0;
        currentDirection = Direction.RIGHT;
        food = null;
        growthStrategy.reset();
        
        initializeSnake();
        
        if (useFoodSystem) {
            spawnFood();
        }
    }
    
    /**
     * Returns a string representation of the game board.
     */
    public String getBoardString() {
        StringBuilder sb = new StringBuilder();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                
                if (pos.equals(getHead())) {
                    sb.append("H ");  // Head
                } else if (snakeBody.contains(pos)) {
                    sb.append("S ");  // Snake body
                } else if (useFoodSystem && pos.equals(food)) {
                    sb.append("F ");  // Food
                } else {
                    sb.append(". ");  // Empty
                }
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
}

