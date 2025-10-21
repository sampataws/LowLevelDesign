# Snake Game - Interview Strategy Guide

## 📝 Overview

This guide helps you solve the "Snake Game" interview question in a 45-60 minute coding interview. The problem starts with basic movement and collision detection, then adds growth mechanics and optional food system.

## ⏱️ Time Management (60 minutes)

| Phase | Time | Activity |
|-------|------|----------|
| 1. Clarification | 5 min | Understand requirements, ask questions |
| 2. Design | 10 min | Design data structures and API |
| 3. Basic Movement | 10 min | Implement movement and direction |
| 4. Collision Detection | 10 min | Add boundary and self-collision |
| 5. Growth Mechanic | 10 min | Implement growth every N moves |
| 6. Food System (Optional) | 10 min | Add food-based growth |
| 7. Testing | 5 min | Quick validation |

## 📋 Phase 1: Clarification (5 minutes)

### Questions to Ask

1. **Board**:
   - "What are the board dimensions?" → Arbitrary, configurable
   - "Is the board bounded?" → Yes, snake dies at boundary
   - "0-indexed or 1-indexed?" → 0-indexed

2. **Movement**:
   - "How does the snake move?" → One cell per moveSnake() call
   - "Can snake move diagonally?" → No, only 4 directions
   - "Can snake reverse direction?" → Discuss (usually prevented)

3. **Growth**:
   - "When does snake grow?" → Every 5 moves initially
   - "By how much?" → 1 cell per growth
   - "Initial size?" → 3 cells

4. **Collision**:
   - "What happens on collision?" → Game over
   - "Collision with self?" → Yes, game over
   - "Collision with boundary?" → Yes, game over

5. **Food System**:
   - "Is food required?" → Optional scale-up
   - "How is food placed?" → Random empty position
   - "Multiple food items?" → Start with one

### What to Say

> "Let me clarify the requirements:
> - Snake starts with length 3 at center of board
> - Moves in 4 directions (UP, DOWN, LEFT, RIGHT)
> - Grows by 1 every 5 moves
> - Game ends on boundary or self-collision
> - Optional: Food system for growth
> 
> I'll design a system with:
> 1. Position class for coordinates
> 2. Deque for snake body (efficient add/remove)
> 3. HashSet for O(1) collision detection
> 4. Strategy pattern for growth mechanics
> 
> Does this align with your expectations?"

## 🏗️ Phase 2: Design (10 minutes)

### Data Structures

```java
// Position - immutable coordinate
class Position {
    int x, y;
    Position move(Direction dir);
    boolean isWithinBounds(int width, int height);
}

// Direction - enum for movement
enum Direction {
    UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);
    int dx, dy;
}

// Main game
class SnakeGame {
    Deque<Position> snake;        // Head at end, tail at front
    Set<Position> snakeBody;      // For O(1) collision check
    Direction currentDirection;
    int width, height;
    boolean gameOver;
    int moveCount;
}
```

### API Design

```java
// Core operations
boolean moveSnake(Direction direction)
boolean isGameOver()
int getSnakeLength()

// State access
Position getHead()
Position getTail()
Deque<Position> getSnake()

// Utilities
void reset()
```

### What to Say

> "I'll use these data structures:
> 
> 1. **Deque for snake**: Head at end, tail at front. O(1) add/remove.
> 2. **HashSet for body**: Fast O(1) collision detection.
> 3. **Position class**: Immutable, clean coordinate handling.
> 4. **Direction enum**: Type-safe, includes delta values.
> 
> Trade-off: 2x space (Deque + Set) for O(1) collision checks.
> Worth it because collision checks happen every move."

## 💻 Phase 3: Basic Movement (10 minutes)

### Implementation Order

1. **Position class** (3 min):
```java
public class Position {
    private final int x, y;
    
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Position move(Direction direction) {
        return new Position(x + direction.getDx(), 
                          y + direction.getDy());
    }
    
    public boolean isWithinBounds(int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return x == p.x && y == p.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
```

2. **Direction enum** (2 min):
```java
public enum Direction {
    UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);
    
    private final int dx, dy;
    
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
    
    public int getDx() { return dx; }
    public int getDy() { return dy; }
}
```

3. **SnakeGame initialization** (5 min):
```java
public class SnakeGame {
    private final int width, height;
    private final Deque<Position> snake;
    private final Set<Position> snakeBody;
    private Direction currentDirection;
    private boolean gameOver;
    private int moveCount;
    
    public SnakeGame(int width, int height) {
        this.width = width;
        this.height = height;
        this.snake = new LinkedList<>();
        this.snakeBody = new HashSet<>();
        this.currentDirection = Direction.RIGHT;
        this.gameOver = false;
        this.moveCount = 0;
        
        // Initialize snake at center with length 3
        int centerX = width / 2;
        int centerY = height / 2;
        for (int i = 2; i >= 0; i--) {
            Position pos = new Position(centerX - i, centerY);
            snake.addLast(pos);
            snakeBody.add(pos);
        }
    }
    
    public boolean moveSnake(Direction direction) {
        if (gameOver) return false;
        
        currentDirection = direction;
        Position currentHead = snake.getLast();
        Position newHead = currentHead.move(direction);
        
        // Add new head
        snake.addLast(newHead);
        snakeBody.add(newHead);
        
        // Remove tail (no growth yet)
        Position tail = snake.removeFirst();
        snakeBody.remove(tail);
        
        moveCount++;
        return true;
    }
}
```

### Test

```java
SnakeGame game = new SnakeGame(20, 20);
assert game.getSnakeLength() == 3;

game.moveSnake(Direction.RIGHT);
assert game.getSnakeLength() == 3;  // No growth yet
assert !game.isGameOver();
```

### What to Say

> "Starting with basic movement:
> 
> 1. **Initialize snake**: 3 cells at center, facing right
> 2. **Move**: Add new head, remove tail (constant length)
> 3. **Track state**: Move count, direction, game over flag
> 
> This gives us the foundation. Next, I'll add collision detection."

## 🚧 Phase 4: Collision Detection (10 minutes)

### Implementation

```java
public boolean moveSnake(Direction direction) {
    if (gameOver) return false;
    
    currentDirection = direction;
    Position currentHead = snake.getLast();
    Position newHead = currentHead.move(direction);
    
    // Check boundary collision
    if (!newHead.isWithinBounds(width, height)) {
        gameOver = true;
        return false;
    }
    
    // Remove tail first (if not growing)
    Position tail = snake.removeFirst();
    snakeBody.remove(tail);
    
    // Check self-collision
    if (snakeBody.contains(newHead)) {
        gameOver = true;
        // Restore tail
        snake.addFirst(tail);
        snakeBody.add(tail);
        return false;
    }
    
    // Move successful
    snake.addLast(newHead);
    snakeBody.add(newHead);
    moveCount++;
    
    return true;
}
```

### Test

```java
// Test boundary collision
SnakeGame game = new SnakeGame(10, 10);
for (int i = 0; i < 20; i++) {
    if (!game.moveSnake(Direction.LEFT)) break;
}
assert game.isGameOver();

// Test self-collision
SnakeGame game2 = new SnakeGame(20, 20);
// Create a box pattern...
// Eventually hits itself
assert game2.isGameOver();
```

### What to Say

> "For collision detection:
> 
> 1. **Boundary check**: Before moving, verify new head is in bounds
> 2. **Self-collision**: After removing tail, check if new head hits body
> 3. **Order matters**: Remove tail first so we don't false-positive
> 4. **Restore state**: If collision, restore tail before returning
> 
> Using HashSet makes collision check O(1) instead of O(N)."

## 🌱 Phase 5: Growth Mechanic (10 minutes)

### Implementation

```java
public boolean moveSnake(Direction direction) {
    if (gameOver) return false;
    
    currentDirection = direction;
    Position currentHead = snake.getLast();
    Position newHead = currentHead.move(direction);
    
    // Check boundary
    if (!newHead.isWithinBounds(width, height)) {
        gameOver = true;
        return false;
    }
    
    moveCount++;
    
    // Determine if should grow
    boolean shouldGrow = (moveCount % 5 == 0);
    
    // Remove tail if not growing
    Position removedTail = null;
    if (!shouldGrow) {
        removedTail = snake.removeFirst();
        snakeBody.remove(removedTail);
    }
    
    // Check self-collision
    if (snakeBody.contains(newHead)) {
        gameOver = true;
        if (removedTail != null) {
            snake.addFirst(removedTail);
            snakeBody.add(removedTail);
        }
        return false;
    }
    
    // Add new head
    snake.addLast(newHead);
    snakeBody.add(newHead);
    
    return true;
}
```

### Test

```java
SnakeGame game = new SnakeGame(20, 20);
assert game.getSnakeLength() == 3;

// Move 5 times
for (int i = 0; i < 5; i++) {
    game.moveSnake(Direction.RIGHT);
}
assert game.getSnakeLength() == 4;  // Grew!

// Move 5 more times
for (int i = 0; i < 5; i++) {
    game.moveSnake(Direction.RIGHT);
}
assert game.getSnakeLength() == 5;  // Grew again!
```

### What to Say

> "For growth every 5 moves:
> 
> 1. **Check move count**: `moveCount % 5 == 0`
> 2. **Conditional tail removal**: Only remove if not growing
> 3. **Increment first**: Count move before checking growth
> 4. **Track removed tail**: Need to restore on collision
> 
> This can be extracted to a Strategy pattern for flexibility."

## 🍎 Phase 6: Food System (10 minutes)

### Implementation

```java
// Add to class
private Position food;
private final Random random = new Random();

// In constructor
spawnFood();

private void spawnFood() {
    Position newFood;
    do {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        newFood = new Position(x, y);
    } while (snakeBody.contains(newFood));
    food = newFood;
}

// In moveSnake
boolean ateFood = newHead.equals(food);
boolean shouldGrow = ateFood;  // Or use strategy

if (shouldGrow && ateFood) {
    spawnFood();
}
```

### Strategy Pattern

```java
interface GrowthStrategy {
    boolean shouldGrow(int moveCount, boolean ateFood);
}

class FixedIntervalGrowthStrategy implements GrowthStrategy {
    private final int interval;
    
    public boolean shouldGrow(int moveCount, boolean ateFood) {
        return moveCount % interval == 0;
    }
}

class FoodGrowthStrategy implements GrowthStrategy {
    public boolean shouldGrow(int moveCount, boolean ateFood) {
        return ateFood;
    }
}
```

### What to Say

> "For the food system:
> 
> 1. **Random placement**: Generate random position, avoid snake
> 2. **Collision detection**: Check if new head equals food
> 3. **Spawn new food**: After eating, place new food
> 4. **Strategy pattern**: Allows switching between growth modes
> 
> Expected O(1) food placement with low snake density.
> Handles full board by checking all positions."

## ✅ Phase 7: Testing (5 minutes)

### Quick Validation

```java
// Test all features
SnakeGame game = new SnakeGame(20, 20);

// Movement
game.moveSnake(Direction.RIGHT);
assert game.getSnakeLength() == 3;

// Growth
for (int i = 0; i < 5; i++) game.moveSnake(Direction.RIGHT);
assert game.getSnakeLength() == 4;

// Boundary collision
SnakeGame game2 = new SnakeGame(5, 5);
for (int i = 0; i < 10; i++) {
    if (!game2.moveSnake(Direction.RIGHT)) break;
}
assert game2.isGameOver();

// Food system
SnakeGame game3 = new SnakeGame(20, 20, new FoodGrowthStrategy(), true);
assert game3.getFood() != null;
```

## 🎯 Common Interview Questions

**Q: How to optimize collision detection?**
> "Currently O(1) with HashSet. For very large snakes, could use spatial hashing or quadtree. But HashSet is optimal for typical snake lengths."

**Q: How to prevent opposite direction?**
> "Check if new direction is opposite of current: `if (direction == currentDirection.opposite()) direction = currentDirection;`"

**Q: How to add obstacles?**
> "Add `Set<Position> obstacles`. Check collision with obstacles same as self-collision. Generate obstacles avoiding snake and food."

**Q: How to support different growth rates?**
> "Use Strategy pattern. Create different implementations: FixedInterval, Food-based, Exponential, etc."

**Q: How to implement AI?**
> "Use BFS/A* to find path to food. Avoid walls and self. Consider snake length in pathfinding cost."

**Q: Thread safety?**
> "Current implementation is not thread-safe. Add synchronized methods or use concurrent collections if needed."

## 📝 Interview Checklist

- [ ] Clarified all requirements
- [ ] Designed data structures (Deque + Set)
- [ ] Implemented Position and Direction
- [ ] Implemented basic movement
- [ ] Added boundary collision
- [ ] Added self-collision
- [ ] Implemented growth mechanic
- [ ] (Optional) Added food system
- [ ] Tested with examples
- [ ] Discussed optimizations

## 🎓 Success Tips

1. **Start simple**: Get movement working first
2. **Test incrementally**: Validate each feature
3. **Explain trade-offs**: Deque+Set vs just Deque
4. **Handle edge cases**: Empty board, full board, size 1 snake
5. **Clean code**: Use meaningful names, extract methods
6. **Think extensibility**: Strategy pattern for growth
7. **Communicate**: Explain your thinking process

---

**Remember**: The interviewer wants to see your problem-solving approach, not just the final code. Communicate clearly, write clean code, and be ready to discuss alternatives!

