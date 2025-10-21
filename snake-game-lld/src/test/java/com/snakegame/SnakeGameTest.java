package com.snakegame;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SnakeGameTest {
    
    private SnakeGame game;
    
    @Before
    public void setUp() {
        game = new SnakeGame(20, 20);
    }
    
    // ========== Initialization Tests ==========
    
    @Test
    public void testInitialState() {
        assertEquals(3, game.getSnakeLength());
        assertEquals(Direction.RIGHT, game.getDirection());
        assertFalse(game.isGameOver());
        assertEquals(0, game.getMoveCount());
        assertNotNull(game.getHead());
        assertNotNull(game.getTail());
    }
    
    @Test
    public void testInitialSnakePosition() {
        // Snake should be at center, size 3, facing right
        Position head = game.getHead();
        assertEquals(10, head.getX());  // Center of 20x20 board
        assertEquals(10, head.getY());
        
        assertEquals(3, game.getSnake().size());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBoardSize() {
        new SnakeGame(0, 10);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBoardTooSmall() {
        new SnakeGame(2, 2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullGrowthStrategy() {
        new SnakeGame(10, 10, null, false);
    }
    
    // ========== Movement Tests ==========
    
    @Test
    public void testMoveRight() {
        Position initialHead = game.getHead();
        game.moveSnake(Direction.RIGHT);
        
        Position newHead = game.getHead();
        assertEquals(initialHead.getX() + 1, newHead.getX());
        assertEquals(initialHead.getY(), newHead.getY());
        assertEquals(1, game.getMoveCount());
    }
    
    @Test
    public void testMoveLeft() {
        // First move right to avoid immediate opposite direction
        game.moveSnake(Direction.RIGHT);
        game.moveSnake(Direction.UP);
        
        Position beforeLeft = game.getHead();
        game.moveSnake(Direction.LEFT);
        
        Position afterLeft = game.getHead();
        assertEquals(beforeLeft.getX() - 1, afterLeft.getX());
        assertEquals(beforeLeft.getY(), afterLeft.getY());
    }
    
    @Test
    public void testMoveUp() {
        Position initialHead = game.getHead();
        game.moveSnake(Direction.UP);
        
        Position newHead = game.getHead();
        assertEquals(initialHead.getX(), newHead.getX());
        assertEquals(initialHead.getY() - 1, newHead.getY());
    }
    
    @Test
    public void testMoveDown() {
        Position initialHead = game.getHead();
        game.moveSnake(Direction.DOWN);
        
        Position newHead = game.getHead();
        assertEquals(initialHead.getX(), newHead.getX());
        assertEquals(initialHead.getY() + 1, newHead.getY());
    }
    
    @Test
    public void testMultipleMoves() {
        for (int i = 0; i < 5; i++) {
            assertTrue(game.moveSnake(Direction.RIGHT));
        }
        
        assertEquals(5, game.getMoveCount());
        assertFalse(game.isGameOver());
    }
    
    @Test
    public void testOppositeDirectionPrevented() {
        // Snake starts facing RIGHT
        // Trying to move LEFT should be ignored
        Position head1 = game.getHead();
        game.moveSnake(Direction.LEFT);
        Position head2 = game.getHead();
        
        // Should have moved RIGHT instead
        assertEquals(head1.getX() + 1, head2.getX());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNullDirection() {
        game.moveSnake(null);
    }
    
    // ========== Growth Tests ==========
    
    @Test
    public void testNoGrowthBeforeInterval() {
        // Default growth is every 5 moves
        for (int i = 0; i < 4; i++) {
            game.moveSnake(Direction.RIGHT);
        }
        
        assertEquals(3, game.getSnakeLength());
    }
    
    @Test
    public void testGrowthAfter5Moves() {
        // Move 5 times
        for (int i = 0; i < 5; i++) {
            game.moveSnake(Direction.RIGHT);
        }
        
        assertEquals(4, game.getSnakeLength());
    }
    
    @Test
    public void testGrowthAfter10Moves() {
        // Move 10 times - alternate directions to avoid hitting boundary
        for (int i = 0; i < 10; i++) {
            Direction dir = (i % 4 < 2) ? Direction.RIGHT : Direction.DOWN;
            game.moveSnake(dir);
        }

        assertEquals(5, game.getSnakeLength());
    }

    @Test
    public void testGrowthAfter15Moves() {
        // Move 15 times - alternate directions to avoid hitting boundary
        for (int i = 0; i < 15; i++) {
            Direction dir = (i % 4 < 2) ? Direction.RIGHT : Direction.DOWN;
            game.moveSnake(dir);
        }

        assertEquals(6, game.getSnakeLength());
    }
    
    @Test
    public void testCustomGrowthInterval() {
        SnakeGame customGame = new SnakeGame(20, 20, new FixedIntervalGrowthStrategy(3), false);
        
        // Should grow every 3 moves
        for (int i = 0; i < 3; i++) {
            customGame.moveSnake(Direction.RIGHT);
        }
        
        assertEquals(4, customGame.getSnakeLength());
    }
    
    // ========== Collision Tests ==========
    
    @Test
    public void testBoundaryCollisionRight() {
        SnakeGame smallGame = new SnakeGame(10, 10);
        
        // Move right until hitting boundary
        for (int i = 0; i < 20; i++) {
            if (!smallGame.moveSnake(Direction.RIGHT)) {
                break;
            }
        }
        
        assertTrue(smallGame.isGameOver());
    }
    
    @Test
    public void testBoundaryCollisionLeft() {
        SnakeGame smallGame = new SnakeGame(10, 10);
        
        // Move left until hitting boundary
        for (int i = 0; i < 20; i++) {
            if (!smallGame.moveSnake(Direction.LEFT)) {
                break;
            }
        }
        
        assertTrue(smallGame.isGameOver());
    }
    
    @Test
    public void testBoundaryCollisionUp() {
        SnakeGame smallGame = new SnakeGame(10, 10);
        
        // Move up until hitting boundary
        for (int i = 0; i < 20; i++) {
            if (!smallGame.moveSnake(Direction.UP)) {
                break;
            }
        }
        
        assertTrue(smallGame.isGameOver());
    }
    
    @Test
    public void testBoundaryCollisionDown() {
        SnakeGame smallGame = new SnakeGame(10, 10);
        
        // Move down until hitting boundary
        for (int i = 0; i < 20; i++) {
            if (!smallGame.moveSnake(Direction.DOWN)) {
                break;
            }
        }
        
        assertTrue(smallGame.isGameOver());
    }
    
    @Test
    public void testSelfCollision() {
        // Create a path that causes self-collision
        // Grow the snake to length 6 (moves 1-5 grow at move 5, moves 6-10 grow at move 10)
        for (int i = 0; i < 10; i++) {
            game.moveSnake(Direction.RIGHT);
        }
        // Snake should be length 5 now

        // Create a box pattern that will cause collision
        game.moveSnake(Direction.DOWN);
        game.moveSnake(Direction.DOWN);
        game.moveSnake(Direction.DOWN);
        game.moveSnake(Direction.LEFT);
        game.moveSnake(Direction.LEFT);
        game.moveSnake(Direction.LEFT);
        game.moveSnake(Direction.LEFT);
        game.moveSnake(Direction.LEFT);
        game.moveSnake(Direction.LEFT);
        game.moveSnake(Direction.UP);
        game.moveSnake(Direction.UP);
        boolean result = game.moveSnake(Direction.UP);  // This should hit the snake body

        assertFalse(result);
        assertTrue(game.isGameOver());
    }
    
    @Test
    public void testCannotMoveAfterGameOver() {
        // Force game over
        for (int i = 0; i < 20; i++) {
            if (!game.moveSnake(Direction.LEFT)) {
                break;
            }
        }
        
        assertTrue(game.isGameOver());
        
        // Try to move again
        assertFalse(game.moveSnake(Direction.RIGHT));
    }
    
    // ========== Food System Tests ==========
    
    @Test
    public void testFoodSystemInitialization() {
        SnakeGame foodGame = new SnakeGame(20, 20, new FoodGrowthStrategy(), true);
        
        assertNotNull(foodGame.getFood());
        assertFalse(foodGame.isSnakeAt(foodGame.getFood()));
    }
    
    @Test
    public void testNoFoodWithoutFoodSystem() {
        assertNull(game.getFood());
    }
    
    @Test
    public void testFoodGrowthStrategy() {
        SnakeGame foodGame = new SnakeGame(20, 20, new FoodGrowthStrategy(), true);
        
        int initialLength = foodGame.getSnakeLength();
        
        // Move without eating food - should not grow
        for (int i = 0; i < 10; i++) {
            Position food = foodGame.getFood();
            Position head = foodGame.getHead();
            
            // Move away from food
            Direction dir = (food.getX() > head.getX()) ? Direction.LEFT : Direction.RIGHT;
            foodGame.moveSnake(dir);
        }
        
        // Should not have grown (unless accidentally ate food)
        assertTrue(foodGame.getSnakeLength() <= initialLength + 1);
    }
    
    // ========== Reset Tests ==========
    
    @Test
    public void testReset() {
        // Make some moves
        for (int i = 0; i < 10; i++) {
            game.moveSnake(Direction.RIGHT);
        }
        
        // Reset
        game.reset();
        
        assertEquals(3, game.getSnakeLength());
        assertEquals(0, game.getMoveCount());
        assertFalse(game.isGameOver());
        assertEquals(Direction.RIGHT, game.getDirection());
    }
    
    @Test
    public void testResetAfterGameOver() {
        // Force game over
        for (int i = 0; i < 20; i++) {
            if (!game.moveSnake(Direction.LEFT)) {
                break;
            }
        }
        
        assertTrue(game.isGameOver());
        
        // Reset
        game.reset();
        
        assertFalse(game.isGameOver());
        assertEquals(3, game.getSnakeLength());
        assertTrue(game.moveSnake(Direction.RIGHT));
    }
    
    // ========== Utility Tests ==========
    
    @Test
    public void testGetBoardString() {
        String board = game.getBoardString();
        assertNotNull(board);
        assertTrue(board.contains("H"));  // Head
        assertTrue(board.contains("S"));  // Snake body
        assertTrue(board.contains("."));  // Empty space
    }
    
    @Test
    public void testIsSnakeAt() {
        Position head = game.getHead();
        assertTrue(game.isSnakeAt(head));
        
        Position empty = new Position(0, 0);
        if (!empty.equals(head)) {
            assertFalse(game.isSnakeAt(empty));
        }
    }
    
    @Test
    public void testGetSnakeReturnsDefensiveCopy() {
        var snake1 = game.getSnake();
        var snake2 = game.getSnake();
        
        assertNotSame(snake1, snake2);
        assertEquals(snake1.size(), snake2.size());
    }
}

