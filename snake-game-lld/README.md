# Snake Game

A comprehensive implementation of the classic Snake game with configurable growth strategies, collision detection, and optional food system.

## 📋 Problem Statement

Implement the base logic of the classic Snake game with the following requirements:

### Core Requirements
- Snake moves in 4 directions: UP, DOWN, LEFT, RIGHT
- Every time `moveSnake()` is called, the snake moves in the current direction
- Initial snake size is 3
- Snake grows by 1 every 5 moves (configurable)
- Game ends when snake hits itself or goes out of bounds
- Board is 2D with arbitrary size

### Optional Scale-Up (Change #2)
- Snake grows when it eats food rather than every N moves
- Food is dropped at random positions on the board
- New food spawns after current food is eaten

## 🏗️ Architecture

### Core Components

```
SnakeGame
├── Direction           # Enum for UP, DOWN, LEFT, RIGHT
├── Position            # Immutable (x, y) coordinate
├── GrowthStrategy      # Interface for growth logic
│   ├── FixedIntervalGrowthStrategy  # Grow every N moves
│   └── FoodGrowthStrategy           # Grow when eating food
└── SnakeGame           # Main game logic
```

### Data Structures

```java
class SnakeGame {
    Deque<Position> snake;        // Snake body (head at end)
    Set<Position> snakeBody;      // Fast collision detection
    Direction currentDirection;    // Current movement direction
    GrowthStrategy growthStrategy; // Pluggable growth logic
    Position food;                 // Food position (optional)
    int moveCount;                 // Total moves made
    boolean gameOver;              // Game state
}
```

## 🚀 Usage

### Basic Game (Growth Every 5 Moves)

```java
// Create game with default settings (20x20 board, grow every 5 moves)
SnakeGame game = new SnakeGame();

// Move the snake
game.moveSnake(Direction.RIGHT);
game.moveSnake(Direction.DOWN);
game.moveSnake(Direction.LEFT);

// Check game state
System.out.println("Length: " + game.getSnakeLength());
System.out.println("Game Over: " + game.isGameOver());
```

### Custom Board Size

```java
// Create 30x30 board
SnakeGame game = new SnakeGame(30, 30);

// Move and check
while (!game.isGameOver()) {
    game.moveSnake(Direction.RIGHT);
}
```

### Custom Growth Interval

```java
// Grow every 3 moves instead of 5
SnakeGame game = new SnakeGame(20, 20, 
    new FixedIntervalGrowthStrategy(3), false);

// After 3 moves, snake will be length 4
for (int i = 0; i < 3; i++) {
    game.moveSnake(Direction.RIGHT);
}
System.out.println(game.getSnakeLength());  // 4
```

### Food-Based Growth (Optional Scale-Up)

```java
// Create game with food system
SnakeGame game = new SnakeGame(20, 20, 
    new FoodGrowthStrategy(), true);

// Snake only grows when it eats food
Position foodPos = game.getFood();
System.out.println("Food at: " + foodPos);

// Move towards food
game.moveSnake(Direction.RIGHT);

// Check if ate food
if (game.getSnakeLength() > 3) {
    System.out.println("Ate food! New food at: " + game.getFood());
}
```

### Direction Control

```java
SnakeGame game = new SnakeGame();

// Set direction before moving
game.setDirection(Direction.UP);
game.moveSnake();  // Moves up

// Or specify direction in move
game.moveSnake(Direction.RIGHT);
```

### Game State

```java
// Get current state
Position head = game.getHead();
Position tail = game.getTail();
int length = game.getSnakeLength();
int moves = game.getMoveCount();
boolean gameOver = game.isGameOver();

// Get snake body
Deque<Position> snake = game.getSnake();

// Check position
boolean occupied = game.isSnakeAt(new Position(5, 5));

// Reset game
game.reset();
```

### Visual Board

```java
// Print the board
String board = game.getBoardString();
System.out.println(board);

// Output:
// . . . . . . . . . .
// . . . . . . . . . .
// . . . S S H . . . .
// . . . . . . . . . .
// . . . F . . . . . .
//
// H = Head, S = Snake body, F = Food, . = Empty
```

## 📊 Algorithm Details

### Movement Algorithm
1. Increment move count
2. Check if should grow (based on strategy)
3. If not growing, remove tail
4. Calculate new head position
5. Check for collisions (boundary, self)
6. Add new head
7. If ate food, spawn new food

**Time Complexity**: O(1) per move
**Space Complexity**: O(N) where N = snake length

### Collision Detection
- **Boundary**: Check if new head is within `[0, width) x [0, height)`
- **Self-collision**: Check if new head is in `snakeBody` set
- Uses HashSet for O(1) collision checks

### Growth Strategies

**FixedIntervalGrowthStrategy**:
- Grows every N moves (default 5)
- `shouldGrow(moveCount, ateFood) = (moveCount % interval == 0)`

**FoodGrowthStrategy**:
- Grows only when eating food
- `shouldGrow(moveCount, ateFood) = ateFood`

### Food Spawning
- Random position generation
- Ensures food doesn't spawn on snake
- O(1) expected time (with low snake density)
- Handles full board gracefully

## 🎯 Design Decisions

### 1. Deque for Snake Body
- **Pros**: O(1) add/remove at both ends
- **Cons**: O(N) for collision checks
- **Solution**: Also maintain HashSet for O(1) collision checks

### 2. Strategy Pattern for Growth
- **Pros**: Easy to add new growth mechanics
- **Cons**: Slight complexity overhead
- **Decision**: Flexibility worth it for extensibility

### 3. Immutable Position
- **Pros**: Thread-safe, prevents bugs
- **Cons**: More object creation
- **Decision**: Safety and clarity worth it

### 4. Prevent Opposite Direction
- **Pros**: Prevents instant self-collision
- **Cons**: Slightly less flexible
- **Decision**: Matches classic Snake game behavior

### 5. Separate Food System Flag
- **Pros**: Can use FixedInterval without food clutter
- **Cons**: Two parameters for configuration
- **Decision**: Cleaner separation of concerns

## 🧪 Testing

### Test Coverage

**SnakeGameTest** (31 tests):
- Initialization (state, position, validation)
- Movement (all 4 directions, multiple moves, opposite prevention)
- Growth (intervals, custom intervals)
- Collisions (all 4 boundaries, self-collision, post-game-over)
- Food system (initialization, growth strategy)
- Reset (normal, after game over)
- Utilities (board string, position checks, defensive copying)

**PositionTest** (12 tests):
- Creation, movement in all directions
- Boundary checking, equals/hashCode
- Immutability

**DirectionTest** (3 tests):
- Direction deltas, opposite directions

**GrowthStrategyTest** (10 tests):
- Fixed interval (default, custom, validation)
- Food growth (eating, ignoring moves)
- Reset behavior

### Run Tests

```bash
# Run all tests
mvn test -pl snake-game-lld

# Run with coverage
mvn clean test jacoco:report -pl snake-game-lld
```

## 🎓 Interview Tips

### Key Points to Mention

1. **Data Structure Choice**:
   - Deque for efficient add/remove
   - HashSet for O(1) collision detection
   - Trade-off: 2x space for better time complexity

2. **Growth Strategies**:
   - Strategy pattern for flexibility
   - Easy to add new mechanics (e.g., shrink on obstacles)
   - Separates growth logic from movement logic

3. **Collision Detection**:
   - Boundary check before self-collision
   - Remove tail before checking (if not growing)
   - Restore state on collision

4. **Food System**:
   - Random generation with collision avoidance
   - Handles edge cases (full board)
   - Optional feature with clean separation

5. **Direction Handling**:
   - Prevent opposite direction (instant death)
   - Maintain current direction
   - Validate input

### Follow-up Questions

**Q: How to optimize for very large snakes?**
- Use spatial hashing for collision detection
- Quadtree for food placement
- Limit snake length or board size

**Q: How to add obstacles?**
- Add `Set<Position> obstacles`
- Check collision with obstacles
- Generate obstacles avoiding snake and food

**Q: How to support multiple snakes?**
- Create `Snake` class
- List of snakes in game
- Check collisions between snakes

**Q: How to add power-ups?**
- Create `PowerUp` interface
- Different types (speed, invincibility, etc.)
- Apply effects on collection

**Q: How to implement AI?**
- Pathfinding (A*, BFS) to food
- Avoid walls and self
- Consider snake length in decisions

**Q: How to make it multiplayer?**
- Separate `Snake` instances
- Turn-based or real-time
- Collision between snakes

## 🚀 Running the Demo

```bash
# Compile
mvn clean compile -pl snake-game-lld

# Run demo
mvn exec:java -Dexec.mainClass="com.snakegame.DriverApplication" \
  -pl snake-game-lld

# Run tests
mvn test -pl snake-game-lld
```

## 📈 Complexity Summary

| Operation | Time | Space |
|-----------|------|-------|
| moveSnake() | O(1) | O(1) |
| Collision check | O(1) | O(N) |
| Food spawn | O(1) expected | O(1) |
| reset() | O(N) | O(1) |
| getBoardString() | O(W×H) | O(W×H) |

Where:
- N = snake length
- W = board width
- H = board height

## 🎯 Real-World Applications

- **Classic Games**: Nokia Snake, Slither.io
- **AI Training**: Reinforcement learning environments
- **Pathfinding**: Testing algorithms
- **Game Development**: Learning game loops and state management
- **Education**: Teaching data structures and algorithms

---

**Status**: ✅ Production-ready implementation with comprehensive testing and documentation

**Test Results**: 56/56 tests passing

