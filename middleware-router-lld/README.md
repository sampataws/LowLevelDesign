# Middleware Router

A comprehensive middleware router for web services that supports exact path matching, wildcard patterns, and path parameter extraction with priority-based routing.

## 📋 Problem Statement

Design a middleware router for web services that:

### Basic Requirements
- Route requests based on path patterns
- Return different handlers for different paths
- Support exact path matching (e.g., `/users`)

### Scale Up 1: Wildcards
- Support wildcard patterns using `*`
- Example: `/api/*` matches `/api/users`, `/api/products`, etc.
- Use ordered checking for priority

### Scale Up 2: Path Parameters
- Support path parameter extraction using `:param` syntax
- Example: `/users/:id` matches `/users/123` and extracts `id=123`
- Support multiple parameters: `/users/:userId/posts/:postId`

## 🏗️ Architecture

### Core Components

```
MiddlewareRouter
├── Route                    # Route with pattern and handler
├── RouteMatch              # Match result with path parameters
└── MiddlewareRouter        # Main router with registration and matching
```

### Data Structures

```java
class Route {
    String pattern;              // "/users/:id"
    String handler;              // "UserHandler"
    int priority;                // Lower = higher priority
    boolean hasWildcard;         // Contains *
    boolean hasPathParams;       // Contains :param
    List<String> pathParamNames; // ["id"]
    Pattern compiledPattern;     // Regex pattern
}

class MiddlewareRouter {
    List<Route> routes;          // All registered routes
    ReadWriteLock lock;          // Thread safety
    boolean autoSort;            // Auto-sort by priority
}
```

## 🚀 Usage

### Basic Exact Path Routing

```java
MiddlewareRouter router = new MiddlewareRouter();

// Register exact routes
router.register("/", "HomeHandler");
router.register("/users", "UsersHandler");
router.register("/products", "ProductsHandler");

// Route requests
String handler = router.route("/users");  // Returns "UsersHandler"
String handler = router.route("/notfound");  // Returns null
```

### Wildcard Routing (Scale Up 1)

```java
MiddlewareRouter router = new MiddlewareRouter();

// Register wildcard routes
router.register("/api/*", "ApiHandler");
router.register("/static/*", "StaticHandler");

// Route requests
String handler = router.route("/api/v1/users");  // Returns "ApiHandler"
String handler = router.route("/static/css/style.css");  // Returns "StaticHandler"
```

### Path Parameters (Scale Up 2)

```java
MiddlewareRouter router = new MiddlewareRouter();

// Register routes with path parameters
router.register("/users/:id", "UserHandler");
router.register("/users/:userId/posts/:postId", "PostHandler");

// Route with parameter extraction
RouteMatch match = router.routeWithParams("/users/123");
System.out.println(match.getHandler());  // "UserHandler"
System.out.println(match.getPathParam("id"));  // "123"

RouteMatch match2 = router.routeWithParams("/users/alice/posts/hello");
System.out.println(match2.getPathParam("userId"));  // "alice"
System.out.println(match2.getPathParam("postId"));  // "hello"
```

### Priority-Based Routing

```java
MiddlewareRouter router = new MiddlewareRouter();

// Routes are auto-sorted by priority
router.register("/api/*", "WildcardHandler");
router.register("/api/users", "ExactHandler");
router.register("/api/:version/users", "ParamHandler");

// Most specific route matches first
router.route("/api/users");        // "ExactHandler" (exact - highest priority)
router.route("/api/v1/users");     // "ParamHandler" (path param - medium)
router.route("/api/anything");     // "WildcardHandler" (wildcard - lowest)
```

### Explicit Priority

```java
MiddlewareRouter router = new MiddlewareRouter();

// Lower priority number = higher priority
router.register("/users", "LowPriority", 100);
router.register("/users", "HighPriority", 10);

router.route("/users");  // Returns "HighPriority"
```

### Route Management

```java
// Get route count
int count = router.getRouteCount();

// Remove routes
int removed = router.removeRoute("/users");

// Clear all routes
router.clear();

// Get statistics
String stats = router.getStatistics();
// "Total routes: 5 (Exact: 2, Wildcard: 1, Path params: 2)"
```

## 📊 Algorithm Details

### Route Matching
- **Time Complexity**: O(N) where N = number of routes
- **Space Complexity**: O(1) per match
- Routes are checked in priority order
- First matching route wins

### Priority Calculation
Routes are automatically prioritized:
1. **Exact paths** (priority 0): `/users`
2. **Path parameters** (priority 100-N): `/users/:id`
3. **Wildcards** (priority 200-N): `/api/*`

More specific patterns within each category get higher priority.

### Pattern Compilation
- Path parameters (`:param`) → Regex groups `([^/]+)`
- Wildcards (`*`) → Regex `.*`
- Compiled once during registration
- Fast matching using Java Pattern/Matcher

### Path Parameter Extraction
- **Time Complexity**: O(M) where M = number of parameters
- **Space Complexity**: O(M)
- Uses regex capture groups
- Returns immutable map

## 🔒 Thread Safety

The router uses `ReadWriteLock` for thread safety:
- **Multiple concurrent readers**: Can route simultaneously
- **Exclusive writers**: Only one thread can register/remove at a time
- **No data races**: All shared data structures are properly synchronized

## 🧪 Testing

### Test Coverage

**RouteTest** (13 tests):
- Exact route matching
- Wildcard matching
- Single and multiple path parameters
- Path parameter extraction
- Priority calculation
- Validation (null/empty patterns)

**MiddlewareRouterTest** (27 tests):
- Basic routing (exact, root, null, empty)
- Wildcard matching (single, multiple, vs exact)
- Path parameters (single, multiple, special chars)
- Priority ordering (auto-sort, explicit, disabled)
- Route management (remove, clear, get routes)
- Statistics
- Complex scenarios (nested paths, combinations)
- Concurrent operations (registration, routing)
- Edge cases (trailing slash, case sensitivity, duplicates)

### Run Tests

```bash
# Run all tests
mvn test -pl middleware-router-lld

# Run with coverage
mvn clean test jacoco:report -pl middleware-router-lld
```

## 🎯 Design Decisions

### 1. Regex-Based Matching
- **Pros**: Flexible, powerful, standard Java API
- **Cons**: Slightly slower than string operations
- **Decision**: Flexibility worth the cost for web routing

### 2. Priority-Based Ordering
- **Pros**: Predictable, automatic, configurable
- **Cons**: Requires sorting on registration
- **Decision**: Auto-sort by default, can be disabled

### 3. Immutable RouteMatch
- **Pros**: Thread-safe, prevents accidental modification
- **Cons**: Slightly more memory
- **Decision**: Safety and clarity worth it

### 4. ReadWriteLock
- **Pros**: Optimized for read-heavy workloads
- **Cons**: More complex than synchronized
- **Decision**: Web routing is read-heavy, worth the complexity

### 5. Path Parameter Syntax
- **Syntax**: `:param` (Express.js style)
- **Alternative**: `{param}` (Spring style)
- **Decision**: `:param` is more concise and widely used

## 🎓 Interview Tips

### Key Points to Mention

1. **Pattern Matching Approaches**:
   - String prefix matching (simple but limited)
   - Regex matching (flexible, chosen approach)
   - Trie-based routing (fast but complex)

2. **Priority Strategies**:
   - Registration order (simple)
   - Specificity-based (chosen approach)
   - Explicit priority (also supported)

3. **Path Parameter Extraction**:
   - Regex capture groups (chosen)
   - Manual parsing (error-prone)
   - Template-based (complex)

4. **Thread Safety**:
   - ReadWriteLock for concurrent reads
   - Proper lock management
   - No data races

5. **Performance Optimizations**:
   - Compile patterns once
   - Sort routes by priority
   - Early exit on first match

### Follow-up Questions

**Q: How to optimize for thousands of routes?**
- Use trie (prefix tree) for exact paths
- Group routes by prefix
- Cache compiled patterns
- Consider radix tree for path parameters

**Q: How to support HTTP methods (GET, POST, etc.)?**
- Add method field to Route
- Match both path and method
- Separate route lists per method

**Q: How to handle middleware chains?**
- Change handler from String to List<Middleware>
- Execute middlewares in order
- Support next() for chaining

**Q: How to support query parameters?**
- Parse query string separately
- Return Map<String, String> for query params
- Don't include in route matching

**Q: How to support regex in patterns?**
- Add regex route type
- Allow full regex patterns
- Compile and cache patterns

## 🚀 Running the Demo

```bash
# Compile
mvn clean compile -pl middleware-router-lld

# Run demo
mvn exec:java -Dexec.mainClass="com.middlewarerouter.DriverApplication" \
  -pl middleware-router-lld

# Run tests
mvn test -pl middleware-router-lld
```

## 📈 Complexity Summary

| Operation | Time | Space |
|-----------|------|-------|
| register() | O(N log N) | O(1) |
| route() | O(N) | O(1) |
| routeWithParams() | O(N + M) | O(M) |
| removeRoute() | O(N) | O(1) |
| clear() | O(1) | O(1) |

Where:
- N = number of routes
- M = number of path parameters

## 🎯 Real-World Applications

- **Web Frameworks**: Express.js, Spring MVC, Flask
- **API Gateways**: Kong, Nginx, AWS API Gateway
- **Microservices**: Service mesh routing
- **CDN**: Content routing and caching
- **Load Balancers**: Path-based routing
- **Reverse Proxies**: Request forwarding

---

**Status**: ✅ Production-ready implementation with comprehensive testing and documentation

