# LCA Organization Hierarchy - Usage Examples

This guide provides practical, real-world examples of using the LCA Organization Hierarchy system.

---

## Quick Start

### Running the Demo

```bash
cd lca-orghierarchy-lld
mvn clean compile
mvn exec:java -Dexec.mainClass="DemoLCA"
```

**Expected Output:**
```
Common group (Alice, Bob): Backend
Common group (Alice, Carol): Engineering
Common group (Alice): API
```

---

## Example 1: Basic Company Structure

### Scenario
A small tech company with Engineering and Sales departments.

### Code

```java
import java.util.*;

public class BasicCompanyExample {
    public static void main(String[] args) {
        OrganizationHierarchy org = new OrganizationHierarchy();
        
        // Build organization tree
        Group company = new Group("TechCorp");
        Group engineering = new Group("Engineering");
        Group sales = new Group("Sales");
        
        company.addSubGroup(engineering);
        company.addSubGroup(sales);
        
        // Register employees
        org.registerEmployee(engineering, "Alice");
        org.registerEmployee(engineering, "Bob");
        org.registerEmployee(sales, "Carol");
        org.registerEmployee(sales, "David");
        
        // Find common groups
        System.out.println("Common group (Alice, Bob): " + 
            org.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob")));
        // Output: Engineering
        
        System.out.println("Common group (Alice, Carol): " + 
            org.getCommonGroupForEmployees(Arrays.asList("Alice", "Carol")));
        // Output: TechCorp
        
        System.out.println("Common group (Carol, David): " + 
            org.getCommonGroupForEmployees(Arrays.asList("Carol", "David")));
        // Output: Sales
    }
}
```

### Organization Chart

```
                TechCorp
                /      \
        Engineering    Sales
         /    \        /    \
      Alice  Bob   Carol  David
```

---

## Example 2: Multi-Level Engineering Organization

### Scenario
Large engineering department with specialized teams.

### Code

```java
public class EngineeringOrgExample {
    public static void main(String[] args) {
        OrganizationHierarchy org = new OrganizationHierarchy();
        
        // Build deep hierarchy
        Group engineering = new Group("Engineering");
        Group backend = new Group("Backend");
        Group frontend = new Group("Frontend");
        Group infrastructure = new Group("Infrastructure");
        
        Group apiTeam = new Group("API Team");
        Group databaseTeam = new Group("Database Team");
        Group webTeam = new Group("Web Team");
        Group mobileTeam = new Group("Mobile Team");
        Group devopsTeam = new Group("DevOps Team");
        Group securityTeam = new Group("Security Team");
        
        // Build tree
        engineering.addSubGroup(backend);
        engineering.addSubGroup(frontend);
        engineering.addSubGroup(infrastructure);
        
        backend.addSubGroup(apiTeam);
        backend.addSubGroup(databaseTeam);
        
        frontend.addSubGroup(webTeam);
        frontend.addSubGroup(mobileTeam);
        
        infrastructure.addSubGroup(devopsTeam);
        infrastructure.addSubGroup(securityTeam);
        
        // Register employees
        org.registerEmployee(apiTeam, "Alice");
        org.registerEmployee(databaseTeam, "Bob");
        org.registerEmployee(webTeam, "Carol");
        org.registerEmployee(mobileTeam, "David");
        org.registerEmployee(devopsTeam, "Eve");
        org.registerEmployee(securityTeam, "Frank");
        
        // Queries
        System.out.println("API + Database: " + 
            org.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob")));
        // Output: Backend
        
        System.out.println("Web + Mobile: " + 
            org.getCommonGroupForEmployees(Arrays.asList("Carol", "David")));
        // Output: Frontend
        
        System.out.println("API + Web: " + 
            org.getCommonGroupForEmployees(Arrays.asList("Alice", "Carol")));
        // Output: Engineering
        
        System.out.println("DevOps + Security: " + 
            org.getCommonGroupForEmployees(Arrays.asList("Eve", "Frank")));
        // Output: Infrastructure
        
        System.out.println("All Backend + Frontend: " + 
            org.getCommonGroupForEmployees(
                Arrays.asList("Alice", "Bob", "Carol", "David")));
        // Output: Engineering
    }
}
```

### Organization Chart

```
                    Engineering
                   /     |      \
              Backend Frontend Infrastructure
              /    \    /    \      /      \
           API   DB  Web  Mobile DevOps Security
            |     |   |     |      |       |
         Alice  Bob Carol David  Eve    Frank
```

---

## Example 3: Real-World Use Case - Meeting Scheduler

### Scenario
Find the smallest team that needs to be involved in a meeting.

### Code

```java
public class MeetingScheduler {
    private OrganizationHierarchy org;
    
    public MeetingScheduler(OrganizationHierarchy org) {
        this.org = org;
    }
    
    public String scheduleMeeting(List<String> attendees) {
        if (attendees.isEmpty()) {
            return "No attendees specified";
        }
        
        Group commonGroup = org.getCommonGroupForEmployees(attendees);
        
        if (commonGroup == null) {
            return "Error: Some attendees not found";
        }
        
        return String.format(
            "Meeting scheduled for %s team (attendees: %s)",
            commonGroup.name,
            String.join(", ", attendees)
        );
    }
    
    public static void main(String[] args) {
        // Setup organization
        OrganizationHierarchy org = new OrganizationHierarchy();
        
        Group engineering = new Group("Engineering");
        Group backend = new Group("Backend");
        Group frontend = new Group("Frontend");
        
        engineering.addSubGroup(backend);
        engineering.addSubGroup(frontend);
        
        org.registerEmployee(backend, "Alice");
        org.registerEmployee(backend, "Bob");
        org.registerEmployee(frontend, "Carol");
        
        // Use scheduler
        MeetingScheduler scheduler = new MeetingScheduler(org);
        
        System.out.println(scheduler.scheduleMeeting(
            Arrays.asList("Alice", "Bob")));
        // Output: Meeting scheduled for Backend team (attendees: Alice, Bob)
        
        System.out.println(scheduler.scheduleMeeting(
            Arrays.asList("Alice", "Carol")));
        // Output: Meeting scheduled for Engineering team (attendees: Alice, Carol)
    }
}
```

---

## Example 4: Access Control System

### Scenario
Determine if users share a common security group.

### Code

```java
public class AccessControlExample {
    private OrganizationHierarchy org;
    
    public boolean canCollaborate(String user1, String user2, String requiredGroup) {
        Group commonGroup = org.getCommonGroupForEmployees(
            Arrays.asList(user1, user2)
        );
        
        if (commonGroup == null) return false;
        
        // Check if common group is at or below required group
        Group current = commonGroup;
        while (current != null) {
            if (current.name.equals(requiredGroup)) {
                return true;
            }
            current = current.parent;
        }
        
        return false;
    }
    
    public static void main(String[] args) {
        OrganizationHierarchy org = new OrganizationHierarchy();
        
        Group company = new Group("Company");
        Group engineering = new Group("Engineering");
        Group backend = new Group("Backend");
        
        company.addSubGroup(engineering);
        engineering.addSubGroup(backend);
        
        org.registerEmployee(backend, "Alice");
        org.registerEmployee(backend, "Bob");
        
        AccessControlExample acl = new AccessControlExample();
        acl.org = org;
        
        System.out.println("Can Alice and Bob collaborate in Backend? " + 
            acl.canCollaborate("Alice", "Bob", "Backend"));
        // Output: true
        
        System.out.println("Can Alice and Bob collaborate in Engineering? " + 
            acl.canCollaborate("Alice", "Bob", "Engineering"));
        // Output: true
        
        System.out.println("Can Alice and Bob collaborate in Company? " + 
            acl.canCollaborate("Alice", "Bob", "Company"));
        // Output: true
    }
}
```

---

## Example 5: Cost Center Allocation

### Scenario
Determine which department should be charged for a multi-person project.

### Code

```java
public class CostCenterExample {
    private OrganizationHierarchy org;
    private Map<String, Double> employeeCosts = new HashMap<>();
    
    public String allocateCost(List<String> projectMembers) {
        Group commonGroup = org.getCommonGroupForEmployees(projectMembers);
        
        if (commonGroup == null) {
            return "Error: Cannot determine cost center";
        }
        
        double totalCost = projectMembers.stream()
            .mapToDouble(emp -> employeeCosts.getOrDefault(emp, 0.0))
            .sum();
        
        return String.format(
            "Cost Center: %s, Total: $%.2f, Members: %s",
            commonGroup.name,
            totalCost,
            String.join(", ", projectMembers)
        );
    }
    
    public static void main(String[] args) {
        OrganizationHierarchy org = new OrganizationHierarchy();
        
        Group engineering = new Group("Engineering");
        Group backend = new Group("Backend");
        Group frontend = new Group("Frontend");
        
        engineering.addSubGroup(backend);
        engineering.addSubGroup(frontend);
        
        org.registerEmployee(backend, "Alice");
        org.registerEmployee(backend, "Bob");
        org.registerEmployee(frontend, "Carol");
        
        CostCenterExample costCenter = new CostCenterExample();
        costCenter.org = org;
        costCenter.employeeCosts.put("Alice", 5000.0);
        costCenter.employeeCosts.put("Bob", 6000.0);
        costCenter.employeeCosts.put("Carol", 5500.0);
        
        System.out.println(costCenter.allocateCost(
            Arrays.asList("Alice", "Bob")));
        // Output: Cost Center: Backend, Total: $11000.00, Members: Alice, Bob
        
        System.out.println(costCenter.allocateCost(
            Arrays.asList("Alice", "Carol")));
        // Output: Cost Center: Engineering, Total: $10500.00, Members: Alice, Carol
    }
}
```

---

## Example 6: Reporting Structure

### Scenario
Find the common manager for multiple employees.

### Code

```java
public class ReportingStructureExample {
    private OrganizationHierarchy org;
    private Map<String, String> groupManagers = new HashMap<>();
    
    public String findCommonManager(List<String> employees) {
        Group commonGroup = org.getCommonGroupForEmployees(employees);
        
        if (commonGroup == null) {
            return "No common manager found";
        }
        
        String manager = groupManagers.get(commonGroup.name);
        
        return String.format(
            "Common manager: %s (manages %s team)",
            manager != null ? manager : "Unknown",
            commonGroup.name
        );
    }
    
    public static void main(String[] args) {
        OrganizationHierarchy org = new OrganizationHierarchy();
        
        Group engineering = new Group("Engineering");
        Group backend = new Group("Backend");
        Group frontend = new Group("Frontend");
        
        engineering.addSubGroup(backend);
        engineering.addSubGroup(frontend);
        
        org.registerEmployee(backend, "Alice");
        org.registerEmployee(backend, "Bob");
        org.registerEmployee(frontend, "Carol");
        
        ReportingStructureExample reporting = new ReportingStructureExample();
        reporting.org = org;
        reporting.groupManagers.put("Engineering", "VP Engineering");
        reporting.groupManagers.put("Backend", "Backend Manager");
        reporting.groupManagers.put("Frontend", "Frontend Manager");
        
        System.out.println(reporting.findCommonManager(
            Arrays.asList("Alice", "Bob")));
        // Output: Common manager: Backend Manager (manages Backend team)
        
        System.out.println(reporting.findCommonManager(
            Arrays.asList("Alice", "Carol")));
        // Output: Common manager: VP Engineering (manages Engineering team)
    }
}
```

---

## Example 7: Building from Configuration

### Scenario
Load organization structure from configuration.

### Code

```java
public class ConfigBasedOrgExample {
    public static OrganizationHierarchy buildFromConfig() {
        OrganizationHierarchy org = new OrganizationHierarchy();
        
        // Simulate loading from JSON/YAML
        Map<String, Object> config = Map.of(
            "Engineering", Map.of(
                "Backend", Arrays.asList("Alice", "Bob"),
                "Frontend", Arrays.asList("Carol", "David")
            ),
            "Sales", Map.of(
                "Regional", Arrays.asList("Eve", "Frank")
            )
        );
        
        // Build tree
        Group root = new Group("Company");
        
        for (Map.Entry<String, Object> dept : config.entrySet()) {
            Group deptGroup = new Group(dept.getKey());
            root.addSubGroup(deptGroup);
            
            @SuppressWarnings("unchecked")
            Map<String, List<String>> teams = 
                (Map<String, List<String>>) dept.getValue();
            
            for (Map.Entry<String, List<String>> team : teams.entrySet()) {
                Group teamGroup = new Group(team.getKey());
                deptGroup.addSubGroup(teamGroup);
                
                for (String employee : team.getValue()) {
                    org.registerEmployee(teamGroup, employee);
                }
            }
        }
        
        return org;
    }
    
    public static void main(String[] args) {
        OrganizationHierarchy org = buildFromConfig();
        
        System.out.println("Alice + Bob: " + 
            org.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob")));
        // Output: Backend
        
        System.out.println("Alice + Carol: " + 
            org.getCommonGroupForEmployees(Arrays.asList("Alice", "Carol")));
        // Output: Engineering
    }
}
```

---

## Example 8: Dynamic Organization Changes

### Scenario
Handle employee transfers and reorganizations.

### Code

```java
public class DynamicOrgExample {
    private OrganizationHierarchy org;
    
    public void transferEmployee(String employee, Group newGroup) {
        // Remove from old group (would need to track this)
        // Add to new group
        org.registerEmployee(newGroup, employee);
        System.out.println(employee + " transferred to " + newGroup.name);
    }
    
    public static void main(String[] args) {
        OrganizationHierarchy org = new OrganizationHierarchy();
        
        Group engineering = new Group("Engineering");
        Group backend = new Group("Backend");
        Group frontend = new Group("Frontend");
        
        engineering.addSubGroup(backend);
        engineering.addSubGroup(frontend);
        
        org.registerEmployee(backend, "Alice");
        org.registerEmployee(backend, "Bob");
        
        System.out.println("Before transfer - Alice + Bob: " + 
            org.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob")));
        // Output: Backend
        
        DynamicOrgExample dynamic = new DynamicOrgExample();
        dynamic.org = org;
        
        // Transfer Alice to Frontend
        dynamic.transferEmployee("Alice", frontend);
        
        System.out.println("After transfer - Alice + Bob: " + 
            org.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob")));
        // Output: Engineering
    }
}
```

---

## Common Patterns

### Pattern 1: Single Employee Query
```java
Group group = org.getCommonGroupForEmployees(
    Collections.singletonList("Alice")
);
// Returns Alice's direct group
```

### Pattern 2: Pair Query
```java
Group group = org.getCommonGroupForEmployees(
    Arrays.asList("Alice", "Bob")
);
// Returns LCA of Alice and Bob's groups
```

### Pattern 3: Multiple Employees
```java
Group group = org.getCommonGroupForEmployees(
    Arrays.asList("Alice", "Bob", "Carol", "David")
);
// Returns LCA of all employees' groups
```

### Pattern 4: Null Handling
```java
Group group = org.getCommonGroupForEmployees(
    Arrays.asList("Alice", "Unknown")
);
// Returns null if any employee not found
```

---

## Best Practices

1. **Build the tree first**, then register employees
2. **Check for null** when querying non-existent employees
3. **Use meaningful group names** for debugging
4. **Cache common queries** if performance is critical
5. **Validate employee names** before registration

---

## Running the Examples

```bash
# Compile
javac -d target/classes src/main/java/*.java

# Run demo
java -cp target/classes DemoLCA

# Run custom example
java -cp target/classes BasicCompanyExample
```

---

**These examples demonstrate practical applications of the LCA algorithm in real-world organizational scenarios!**

