# Middleware Router - Interview Strategy Guide

## 📝 Overview

This guide helps you solve the "Middleware Router" interview question in a 45-60 minute coding interview. The problem is typically presented in stages, starting with basic routing and adding wildcards and path parameters.

## ⏱️ Time Management (60 minutes)

| Phase | Time | Activity |
|-------|------|----------|
| 1. Clarification | 5 min | Understand requirements, ask questions |
| 2. Design | 10 min | Design data structures and API |
| 3. Basic Routing | 10 min | Implement exact path matching |
| 4. Wildcards | 10 min | Add wildcard support (Scale Up 1) |
| 5. Path Params | 15 min | Add path parameter extraction (Scale Up 2) |
| 6. Priority | 5 min | Implement priority ordering |
| 7. Testing | 5 min | Quick validation |

## 📋 Phase 1: Clarification (5 minutes)

### Questions to Ask

1. **Path Format**:
   - "What path format should we support?" → Standard URL paths
   - "Case sensitive?" → Yes
   - "Trailing slashes matter?" → Yes, different paths

2. **Wildcards**:
   - "What does * match?" → Any characters including /
   - "Multiple wildcards in one pattern?" → Start with single
   - "Wildcard at beginning/middle/end?" → Any position

3. **Path Parameters**:
   - "What syntax for parameters?" → `:param` (Express.js style)
   - "Parameter naming rules?" → Alphanumeric + underscore
   - "Can parameters contain slashes?" → No

4. **Priority**:
   - "How to handle conflicts?" → Most specific wins
   - "Registration order matters?" → No, use specificity

5. **Performance**:
   - "Expected scale?" → Hundreds of routes, thousands of requests/sec
   - "Thread safety needed?" → Yes

### What to Say

> "Let me clarify the requirements:
> - We need to route paths to handlers based on patterns
> - Support exact matching, wildcards (*), and path parameters (:param)
> - More specific routes should match before general ones
> - Thread-safe for concurrent requests
> 
> I'll design a router with:
> 1. Route registration with pattern and handler
> 2. Pattern matching using regex
> 3. Priority-based ordering (exact > params > wildcards)
> 4. Path parameter extraction
> 
> Does this align with your expectations?"

## 🏗️ Phase 2: Design (10 minutes)

### Data Structures

```java
// Core data model
class Route {
    String pattern;              // "/users/:id"
    String handler;              // "UserHandler"
    int priority;                // Lower = higher priority
    Pattern compiledPattern;     // Regex for matching
    List<String> pathParamNames; // ["id"]
}

class RouteMatch {
    String handler;
    Map<String, String> pathParams;
}

// Main router
class MiddlewareRouter {
    List<Route> routes;          // Sorted by priority
    ReadWriteLock lock;          // Thread safety
}
```

### API Design

```java
// Registration
void register(String pattern, String handler)
void register(String pattern, String handler, int priority)

// Routing
String route(String path)
RouteMatch routeWithParams(String path)

// Management
int removeRoute(String pattern)
void clear()
int getRouteCount()
```

### What to Say

> "I'll use these data structures:
> 
> 1. **Route class**: Stores pattern, handler, and compiled regex
> 2. **List of routes**: Sorted by priority for efficient matching
> 3. **ReadWriteLock**: Allow concurrent reads, exclusive writes
> 
> For pattern matching:
> - Convert `:param` to regex groups: `([^/]+)`
> - Convert `*` to regex: `.*`
> - Compile once, match many times
> 
> Priority calculation:
> - Exact paths: priority 0
> - Path params: priority 100
> - Wildcards: priority 200
> - More specific within category gets higher priority"

## 💻 Phase 3: Basic Routing (10 minutes)

### Implementation Order

1. **Route class** (3 min):
```java
public class Route {
    private final String pattern;
    private final String handler;
    private final int priority;
    
    public Route(String pattern, String handler, int priority) {
        this.pattern = pattern;
        this.handler = handler;
        this.priority = priority;
    }
    
    public boolean matches(String path) {
        return pattern.equals(path);  // Exact match for now
    }
}
```

2. **MiddlewareRouter class** (4 min):
```java
public class MiddlewareRouter {
    private final List<Route> routes = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public void register(String pattern, String handler) {
        lock.writeLock().lock();
        try {
            routes.add(new Route(pattern, handler, 0));
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public String route(String path) {
        lock.readLock().lock();
        try {
            for (Route route : routes) {
                if (route.matches(path)) {
                    return route.getHandler();
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }
}
```

3. **Test** (3 min):
```java
MiddlewareRouter router = new MiddlewareRouter();
router.register("/users", "UsersHandler");
router.register("/products", "ProductsHandler");

assert "UsersHandler".equals(router.route("/users"));
assert "ProductsHandler".equals(router.route("/products"));
assert router.route("/notfound") == null;
```

### What to Say

> "Starting with basic exact matching:
> 
> 1. **Route class**: Stores pattern and handler, exact match for now
> 2. **Router**: List of routes, linear search for match
> 3. **Thread safety**: ReadWriteLock for concurrent access
> 
> This gives us O(N) routing time, which is acceptable for now.
> We can optimize later with tries or hash maps."

## 🌟 Phase 4: Wildcards (10 minutes)

### Implementation

1. **Update Route class** (5 min):
```java
public class Route {
    private final Pattern compiledPattern;
    private final boolean hasWildcard;
    
    public Route(String pattern, String handler, int priority) {
        this.pattern = pattern;
        this.handler = handler;
        this.priority = priority;
        this.hasWildcard = pattern.contains("*");
        this.compiledPattern = compilePattern(pattern);
    }
    
    private Pattern compilePattern(String pattern) {
        String regex = pattern.replace("*", ".*");
        return Pattern.compile("^" + regex + "$");
    }
    
    public boolean matches(String path) {
        return compiledPattern.matcher(path).matches();
    }
}
```

2. **Add priority calculation** (3 min):
```java
private static int calculateDefaultPriority(String pattern) {
    if (!pattern.contains("*") && !pattern.contains(":")) {
        return 0;  // Exact
    }
    if (pattern.contains("*")) {
        return 200 - pattern.indexOf("*");  // Wildcard
    }
    return 100;  // Path params (later)
}
```

3. **Sort routes** (2 min):
```java
public void register(String pattern, String handler) {
    lock.writeLock().lock();
    try {
        routes.add(new Route(pattern, handler));
        routes.sort(Comparator.comparingInt(Route::getPriority));
    } finally {
        lock.writeLock().unlock();
    }
}
```

### Test

```java
router.register("/api/*", "ApiHandler");
router.register("/api/users", "ExactHandler");

assert "ExactHandler".equals(router.route("/api/users"));  // Exact wins
assert "ApiHandler".equals(router.route("/api/products")); // Wildcard
```

### What to Say

> "For wildcards:
> 
> 1. **Convert to regex**: Replace `*` with `.*`
> 2. **Compile once**: Store Pattern object for reuse
> 3. **Priority**: Exact routes before wildcards
> 4. **Auto-sort**: Keep routes sorted by priority
> 
> This ensures most specific routes match first."

## 🔗 Phase 5: Path Parameters (15 minutes)

### Implementation

1. **Update pattern compilation** (5 min):
```java
private Pattern compilePattern(String pattern) {
    String regex = pattern;
    
    // Extract parameter names
    Pattern paramPattern = Pattern.compile(":([a-zA-Z][a-zA-Z0-9_]*)");
    Matcher matcher = paramPattern.matcher(pattern);
    while (matcher.find()) {
        pathParamNames.add(matcher.group(1));
    }
    
    // Convert :param to regex groups
    regex = regex.replaceAll(":([a-zA-Z][a-zA-Z0-9_]*)", "([^/]+)");
    
    // Convert wildcards
    regex = regex.replace("*", ".*");
    
    return Pattern.compile("^" + regex + "$");
}
```

2. **Add parameter extraction** (5 min):
```java
public Map<String, String> extractPathParams(String path) {
    Map<String, String> params = new HashMap<>();
    
    if (!hasPathParams) {
        return params;
    }
    
    Matcher matcher = compiledPattern.matcher(path);
    if (matcher.matches()) {
        for (int i = 0; i < pathParamNames.size(); i++) {
            params.put(pathParamNames.get(i), matcher.group(i + 1));
        }
    }
    
    return params;
}
```

3. **Create RouteMatch class** (3 min):
```java
public class RouteMatch {
    private final String handler;
    private final Map<String, String> pathParams;
    
    public RouteMatch(String handler, Map<String, String> pathParams) {
        this.handler = handler;
        this.pathParams = pathParams;
    }
    
    public String getPathParam(String name) {
        return pathParams.get(name);
    }
}
```

4. **Add routeWithParams method** (2 min):
```java
public RouteMatch routeWithParams(String path) {
    lock.readLock().lock();
    try {
        for (Route route : routes) {
            if (route.matches(path)) {
                Map<String, String> params = route.extractPathParams(path);
                return new RouteMatch(route.getHandler(), params);
            }
        }
        return null;
    } finally {
        lock.readLock().unlock();
    }
}
```

### Test

```java
router.register("/users/:id", "UserHandler");
router.register("/users/:userId/posts/:postId", "PostHandler");

RouteMatch match = router.routeWithParams("/users/123");
assert "UserHandler".equals(match.getHandler());
assert "123".equals(match.getPathParam("id"));

RouteMatch match2 = router.routeWithParams("/users/alice/posts/hello");
assert "alice".equals(match2.getPathParam("userId"));
assert "hello".equals(match2.getPathParam("postId"));
```

### What to Say

> "For path parameters:
> 
> 1. **Extract names**: Find all `:param` in pattern
> 2. **Convert to regex**: Replace with capture groups `([^/]+)`
> 3. **Extract values**: Use Matcher.group() to get captured values
> 4. **Return map**: Parameter name → value
> 
> The regex `[^/]+` matches any characters except slash,
> ensuring parameters don't span multiple path segments."

## 🎯 Phase 6: Priority (5 minutes)

### Implementation

```java
private static int calculateDefaultPriority(String pattern) {
    int priority = 0;
    
    // Exact paths have highest priority
    if (!pattern.contains("*") && !pattern.contains(":")) {
        return 0;
    }
    
    // Path params have medium priority
    if (pattern.contains(":")) {
        priority = 100;
        // More specific (more segments) gets higher priority
        priority -= pattern.split("/").length * 10;
    }
    
    // Wildcards have lowest priority
    if (pattern.contains("*")) {
        priority = 200;
        // Earlier wildcard = less specific
        priority -= pattern.indexOf("*");
    }
    
    return priority;
}
```

### Test

```java
router.register("/api/*", "WildcardHandler");
router.register("/api/users", "ExactHandler");
router.register("/api/:version/users", "ParamHandler");

assert "ExactHandler".equals(router.route("/api/users"));
assert "ParamHandler".equals(router.route("/api/v1/users"));
assert "WildcardHandler".equals(router.route("/api/anything"));
```

### What to Say

> "Priority calculation ensures correct matching order:
> 
> 1. **Exact paths**: Priority 0 (highest)
> 2. **Path parameters**: Priority 100-N (medium)
> 3. **Wildcards**: Priority 200-N (lowest)
> 
> Within each category, more specific patterns get higher priority.
> Routes are sorted once on registration, then matched in order."

## ✅ Phase 7: Testing (5 minutes)

### Quick Validation

```java
// Test all features
MiddlewareRouter router = new MiddlewareRouter();

// Exact
router.register("/users", "UsersHandler");
assert "UsersHandler".equals(router.route("/users"));

// Wildcard
router.register("/api/*", "ApiHandler");
assert "ApiHandler".equals(router.route("/api/anything"));

// Path params
router.register("/users/:id", "UserHandler");
RouteMatch match = router.routeWithParams("/users/123");
assert "123".equals(match.getPathParam("id"));

// Priority
router.register("/api/users", "ExactApiHandler");
assert "ExactApiHandler".equals(router.route("/api/users"));
```

## 🎯 Common Interview Questions

**Q: How to optimize for many routes?**
> "Use trie (prefix tree) for exact paths. Group routes by prefix. For path params, use radix tree. This reduces O(N) to O(log N) or O(1) for exact matches."

**Q: How to support HTTP methods?**
> "Add method field to Route. Match both path and method. Can use separate route lists per method for faster lookup."

**Q: How to handle middleware chains?**
> "Change handler to List<Middleware>. Execute in order. Support next() callback for chaining. Add error handling middleware."

**Q: What about query parameters?**
> "Parse separately from path. Don't include in route matching. Return as separate Map<String, String>."

**Q: How to support regex patterns?**
> "Add regex route type. Allow full regex patterns. Compile and cache. Lower priority than exact/params."

**Q: Thread safety concerns?**
> "ReadWriteLock allows concurrent reads. Routes are immutable after creation. Sorting happens under write lock."

## 📝 Interview Checklist

- [ ] Clarified all requirements
- [ ] Designed data structures
- [ ] Implemented basic routing
- [ ] Added wildcard support
- [ ] Added path parameter extraction
- [ ] Implemented priority ordering
- [ ] Added thread safety
- [ ] Tested with examples
- [ ] Discussed optimizations
- [ ] Mentioned trade-offs

## 🎓 Success Tips

1. **Start simple**: Get exact matching working first
2. **Incremental**: Add wildcards, then params
3. **Test as you go**: Validate each feature
4. **Communicate**: Explain your thinking
5. **Consider edge cases**: Empty paths, null, duplicates
6. **Discuss trade-offs**: Regex vs string matching, sorting cost
7. **Think production**: Thread safety, performance, scalability

---

**Remember**: The interviewer wants to see your problem-solving process, not just the final code. Communicate clearly, write clean code, and be ready to discuss alternatives!

