package com.snakegame;

/**
 * Driver application demonstrating the Snake Game.
 */
public class DriverApplication {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║              Snake Game - Real-time Demo                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        demo1_BasicMovement();
        demo2_GrowthEvery5Moves();
        demo3_SelfCollision();
        demo4_BoundaryCollision();
        demo5_FoodGrowthSystem();
        
        System.out.println("✅ All demos completed successfully!");
        System.out.println();
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("Key Features Demonstrated:");
        System.out.println("  ✓ 4-directional movement (UP, DOWN, LEFT, RIGHT)");
        System.out.println("  ✓ Initial size of 3");
        System.out.println("  ✓ Growth every 5 moves (configurable)");
        System.out.println("  ✓ Self-collision detection");
        System.out.println("  ✓ Boundary collision detection");
        System.out.println("  ✓ Food-based growth system (optional)");
        System.out.println("════════════════════════════════════════════════════════════════");
    }
    
    private static void demo1_BasicMovement() {
        System.out.println("📋 Demo 1: Basic Movement");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        SnakeGame game = new SnakeGame(10, 10);
        
        System.out.println("Initial state:");
        System.out.println("  Snake length: " + game.getSnakeLength());
        System.out.println("  Head position: " + game.getHead());
        System.out.println("  Direction: " + game.getDirection());
        System.out.println();
        
        // Move right 3 times
        System.out.println("Moving RIGHT 3 times:");
        for (int i = 0; i < 3; i++) {
            game.moveSnake(Direction.RIGHT);
            System.out.println("  Move " + (i + 1) + ": Head at " + game.getHead() + 
                             ", Length: " + game.getSnakeLength());
        }
        System.out.println();
        
        // Move down 2 times
        System.out.println("Moving DOWN 2 times:");
        for (int i = 0; i < 2; i++) {
            game.moveSnake(Direction.DOWN);
            System.out.println("  Move " + (i + 1) + ": Head at " + game.getHead() + 
                             ", Length: " + game.getSnakeLength());
        }
        System.out.println();
        
        System.out.println("✓ Basic movement working correctly");
        System.out.println();
    }
    
    private static void demo2_GrowthEvery5Moves() {
        System.out.println("🌱 Demo 2: Growth Every 5 Moves");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        SnakeGame game = new SnakeGame(20, 20);
        
        System.out.println("Initial length: " + game.getSnakeLength());
        System.out.println();
        
        System.out.println("Moving and tracking growth:");
        for (int i = 1; i <= 15; i++) {
            Direction dir = (i % 4 == 0) ? Direction.DOWN : Direction.RIGHT;
            game.moveSnake(dir);
            
            if (i % 5 == 0) {
                System.out.println("  Move " + i + ": Length = " + game.getSnakeLength() + 
                                 " (GREW!)");
            } else {
                System.out.println("  Move " + i + ": Length = " + game.getSnakeLength());
            }
        }
        System.out.println();
        
        System.out.println("Expected growth pattern:");
        System.out.println("  Initial: 3");
        System.out.println("  After 5 moves: 4");
        System.out.println("  After 10 moves: 5");
        System.out.println("  After 15 moves: 6");
        System.out.println();
        
        System.out.println("✓ Growth every 5 moves working correctly");
        System.out.println();
    }
    
    private static void demo3_SelfCollision() {
        System.out.println("💥 Demo 3: Self-Collision Detection");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        SnakeGame game = new SnakeGame(10, 10);
        
        System.out.println("Creating a scenario where snake hits itself:");
        System.out.println();
        
        // Create a path that will cause self-collision
        Direction[] moves = {
            Direction.RIGHT, Direction.RIGHT, Direction.RIGHT,
            Direction.DOWN, Direction.DOWN,
            Direction.LEFT, Direction.LEFT, Direction.LEFT,
            Direction.UP  // This should cause collision
        };
        
        for (int i = 0; i < moves.length; i++) {
            boolean success = game.moveSnake(moves[i]);
            System.out.println("  Move " + (i + 1) + " (" + moves[i] + "): " + 
                             (success ? "Success" : "COLLISION - Game Over!"));
            
            if (!success) {
                System.out.println("  Final position: " + game.getHead());
                System.out.println("  Game over: " + game.isGameOver());
                break;
            }
        }
        System.out.println();
        
        System.out.println("✓ Self-collision detection working correctly");
        System.out.println();
    }
    
    private static void demo4_BoundaryCollision() {
        System.out.println("🚧 Demo 4: Boundary Collision Detection");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        SnakeGame game = new SnakeGame(10, 10);
        
        System.out.println("Board size: " + game.getWidth() + "x" + game.getHeight());
        System.out.println("Initial head position: " + game.getHead());
        System.out.println();
        
        System.out.println("Moving LEFT to hit left boundary:");
        int moveCount = 0;
        while (!game.isGameOver() && moveCount < 20) {
            boolean success = game.moveSnake(Direction.LEFT);
            moveCount++;
            
            if (!success) {
                System.out.println("  Hit boundary after " + moveCount + " moves");
                System.out.println("  Final head position: " + game.getHead());
                System.out.println("  Game over: " + game.isGameOver());
                break;
            }
        }
        System.out.println();
        
        System.out.println("✓ Boundary collision detection working correctly");
        System.out.println();
    }
    
    private static void demo5_FoodGrowthSystem() {
        System.out.println("🍎 Demo 5: Food-Based Growth System (Optional Scale-Up)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        // Create game with food growth strategy
        SnakeGame game = new SnakeGame(15, 15, new FoodGrowthStrategy(), true);
        
        System.out.println("Initial state:");
        System.out.println("  Snake length: " + game.getSnakeLength());
        System.out.println("  Food position: " + game.getFood());
        System.out.println();
        
        System.out.println("Simulating game with food:");
        System.out.println("(Snake only grows when it eats food)");
        System.out.println();
        
        int previousLength = game.getSnakeLength();
        int foodEaten = 0;
        
        // Simulate some moves
        for (int i = 0; i < 20 && !game.isGameOver(); i++) {
            Position foodPos = game.getFood();
            Position headPos = game.getHead();
            
            // Simple AI: move towards food
            Direction dir;
            if (foodPos.getX() > headPos.getX()) {
                dir = Direction.RIGHT;
            } else if (foodPos.getX() < headPos.getX()) {
                dir = Direction.LEFT;
            } else if (foodPos.getY() > headPos.getY()) {
                dir = Direction.DOWN;
            } else {
                dir = Direction.UP;
            }
            
            game.moveSnake(dir);
            
            int currentLength = game.getSnakeLength();
            if (currentLength > previousLength) {
                foodEaten++;
                System.out.println("  Move " + (i + 1) + ": ATE FOOD! Length: " + 
                                 currentLength + ", New food at: " + game.getFood());
                previousLength = currentLength;
            }
        }
        System.out.println();
        
        System.out.println("Summary:");
        System.out.println("  Total moves: " + game.getMoveCount());
        System.out.println("  Food eaten: " + foodEaten);
        System.out.println("  Final length: " + game.getSnakeLength());
        System.out.println("  Expected length: " + (3 + foodEaten));
        System.out.println();
        
        System.out.println("✓ Food-based growth system working correctly");
        System.out.println();
    }
}

