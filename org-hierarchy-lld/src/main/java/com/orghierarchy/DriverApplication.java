package com.orghierarchy;

import java.util.*;

/**
 * Driver application demonstrating the Organization Hierarchy system.
 */
public class DriverApplication {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║     Organization Hierarchy - Closest Common Group Demo        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            runDemo();
        } catch (Exception e) {
            System.err.println("Error running demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void runDemo() {
        OrgHierarchy org = new OrgHierarchy();
        
        // Demo 1: Basic Hierarchy
        System.out.println("📋 Demo 1: Building Basic Organization Hierarchy");
        System.out.println("─".repeat(64));
        
        // Create employees
        Employee alice = new Employee("E001", "Alice Johnson", "alice@atlassian.com");
        Employee bob = new Employee("E002", "Bob Smith", "bob@atlassian.com");
        Employee carol = new Employee("E003", "Carol White", "carol@atlassian.com");
        Employee david = new Employee("E004", "David Brown", "david@atlassian.com");
        Employee emma = new Employee("E005", "Emma Davis", "emma@atlassian.com");
        Employee frank = new Employee("E006", "Frank Wilson", "frank@atlassian.com");
        
        org.addEmployee(alice);
        org.addEmployee(bob);
        org.addEmployee(carol);
        org.addEmployee(david);
        org.addEmployee(emma);
        org.addEmployee(frank);
        
        System.out.println("✓ Added 6 employees");
        
        // Create hierarchy:
        //                    Atlassian (G1)
        //                    /           \
        //            Engineering (G2)   Sales (G3)
        //            /         \
        //      Backend (G4)  Frontend (G5)
        
        Group atlassian = new Group("G1", "Atlassian");
        Group engineering = new Group("G2", "Engineering");
        Group sales = new Group("G3", "Sales");
        Group backend = new Group("G4", "Backend Team");
        Group frontend = new Group("G5", "Frontend Team");
        
        org.addGroup(atlassian);
        org.addGroup(engineering);
        org.addGroup(sales);
        org.addGroup(backend);
        org.addGroup(frontend);
        
        System.out.println("✓ Created 5 groups");
        
        // Build hierarchy
        org.addGroupToGroup("G2", "G1"); // Engineering -> Atlassian
        org.addGroupToGroup("G3", "G1"); // Sales -> Atlassian
        org.addGroupToGroup("G4", "G2"); // Backend -> Engineering
        org.addGroupToGroup("G5", "G2"); // Frontend -> Engineering
        
        System.out.println("✓ Built hierarchy structure");
        
        // Add employees to groups
        org.addEmployeeToGroup("E001", "G4"); // Alice -> Backend
        org.addEmployeeToGroup("E002", "G4"); // Bob -> Backend
        org.addEmployeeToGroup("E003", "G5"); // Carol -> Frontend
        org.addEmployeeToGroup("E004", "G5"); // David -> Frontend
        org.addEmployeeToGroup("E005", "G3"); // Emma -> Sales
        org.addEmployeeToGroup("E006", "G3"); // Frank -> Sales
        
        System.out.println("✓ Assigned employees to groups");
        System.out.println();
        
        printHierarchy();
        
        // Demo 2: Find closest common group
        System.out.println("\n🔍 Demo 2: Finding Closest Common Group");
        System.out.println("─".repeat(64));
        
        // Case 1: Same team
        Set<String> sameTeam = new HashSet<>(Arrays.asList("E001", "E002"));
        Group common1 = org.getClosestCommonGroup(sameTeam);
        System.out.println("Alice & Bob (both Backend):");
        System.out.println("  → Closest common group: " + common1.getName() + " (" + common1.getGroupId() + ")");
        
        // Case 2: Same department
        Set<String> sameDept = new HashSet<>(Arrays.asList("E001", "E003"));
        Group common2 = org.getClosestCommonGroup(sameDept);
        System.out.println("\nAlice (Backend) & Carol (Frontend):");
        System.out.println("  → Closest common group: " + common2.getName() + " (" + common2.getGroupId() + ")");
        
        // Case 3: Different departments
        Set<String> diffDept = new HashSet<>(Arrays.asList("E001", "E005"));
        Group common3 = org.getClosestCommonGroup(diffDept);
        System.out.println("\nAlice (Backend) & Emma (Sales):");
        System.out.println("  → Closest common group: " + common3.getName() + " (" + common3.getGroupId() + ")");
        
        // Case 4: Three employees
        Set<String> threeEmps = new HashSet<>(Arrays.asList("E001", "E003", "E004"));
        Group common4 = org.getClosestCommonGroup(threeEmps);
        System.out.println("\nAlice (Backend), Carol & David (Frontend):");
        System.out.println("  → Closest common group: " + common4.getName() + " (" + common4.getGroupId() + ")");
        
        // Demo 3: Shared groups (employees in multiple groups)
        System.out.println("\n\n👥 Demo 3: Shared Groups - Employee in Multiple Groups");
        System.out.println("─".repeat(64));
        
        Group platform = new Group("G6", "Platform Team");
        org.addGroup(platform);
        org.addGroupToGroup("G6", "G2"); // Platform -> Engineering
        
        // Alice is now in both Backend and Platform
        org.addEmployeeToGroup("E001", "G6");
        
        System.out.println("✓ Alice is now in both Backend and Platform teams");
        System.out.println("  Groups for Alice: " + org.getGroupsForEmployee("E001"));
        
        // Demo 4: Dynamic updates with concurrent access
        System.out.println("\n\n⚡ Demo 4: Dynamic Updates (Thread-Safe Operations)");
        System.out.println("─".repeat(64));
        
        // Simulate concurrent updates
        Thread[] threads = new Thread[5];
        
        threads[0] = new Thread(() -> {
            Employee newEmp = new Employee("E007", "Grace Lee", "grace@atlassian.com");
            org.addEmployee(newEmp);
            org.addEmployeeToGroup("E007", "G4");
            System.out.println("  [Thread 1] Added Grace to Backend");
        });
        
        threads[1] = new Thread(() -> {
            org.moveEmployeeToGroup("E002", "G4", "G5");
            System.out.println("  [Thread 2] Moved Bob from Backend to Frontend");
        });
        
        threads[2] = new Thread(() -> {
            Set<String> emps = new HashSet<>(Arrays.asList("E001", "E002"));
            try {
                Group result = org.getClosestCommonGroup(emps);
                System.out.println("  [Thread 3] Found common group: " + result.getName());
            } catch (Exception e) {
                System.out.println("  [Thread 3] Query during update: " + e.getMessage());
            }
        });
        
        threads[3] = new Thread(() -> {
            Group devOps = new Group("G7", "DevOps Team");
            org.addGroup(devOps);
            org.addGroupToGroup("G7", "G2");
            System.out.println("  [Thread 4] Added DevOps team to Engineering");
        });
        
        threads[4] = new Thread(() -> {
            Set<String> allEmps = org.getAllEmployeesInGroupHierarchy("G2");
            System.out.println("  [Thread 5] Engineering has " + allEmps.size() + " employees");
        });
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for completion
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("\n✓ All concurrent operations completed successfully");
        
        // Demo 5: Flat hierarchy (single level)
        System.out.println("\n\n📊 Demo 5: Flat Hierarchy (Single Level Groups)");
        System.out.println("─".repeat(64));
        
        OrgHierarchy flatOrg = new OrgHierarchy();
        
        // Create flat structure
        Group teamA = new Group("T1", "Team A");
        Group teamB = new Group("T2", "Team B");
        Group teamC = new Group("T3", "Team C");
        
        flatOrg.addGroup(teamA);
        flatOrg.addGroup(teamB);
        flatOrg.addGroup(teamC);
        
        Employee emp1 = new Employee("F001", "John Doe", "john@atlassian.com");
        Employee emp2 = new Employee("F002", "Jane Smith", "jane@atlassian.com");
        Employee emp3 = new Employee("F003", "Mike Johnson", "mike@atlassian.com");
        
        flatOrg.addEmployee(emp1);
        flatOrg.addEmployee(emp2);
        flatOrg.addEmployee(emp3);
        
        flatOrg.addEmployeeToGroup("F001", "T1");
        flatOrg.addEmployeeToGroup("F002", "T2");
        flatOrg.addEmployeeToGroup("F003", "T3");
        
        // Employee in multiple groups
        flatOrg.addEmployeeToGroup("F001", "T2");
        
        System.out.println("✓ Created flat hierarchy with 3 teams");
        System.out.println("✓ John is in both Team A and Team B");
        
        Set<String> flatQuery = new HashSet<>(Arrays.asList("F001", "F002"));
        Group flatCommon = flatOrg.getClosestCommonGroup(flatQuery);
        
        if (flatCommon != null) {
            System.out.println("\nJohn & Jane:");
            System.out.println("  → Common group: " + flatCommon.getName());
        } else {
            System.out.println("\nJohn & Jane:");
            System.out.println("  → No common group (different teams in flat structure)");
        }
        
        // Demo 6: Statistics
        System.out.println("\n\n📈 Demo 6: Organization Statistics");
        System.out.println("─".repeat(64));
        
        System.out.println("Engineering Department:");
        Set<String> engEmps = org.getAllEmployeesInGroupHierarchy("G2");
        System.out.println("  Total employees: " + engEmps.size());
        System.out.println("  Employees: " + engEmps);
        
        System.out.println("\nBackend Team:");
        Set<String> backendEmps = org.getEmployeesInGroup("G4");
        System.out.println("  Direct members: " + backendEmps.size());
        System.out.println("  Members: " + backendEmps);
        
        System.out.println("\n✅ All demos completed successfully!");
        System.out.println("\n" + "═".repeat(64));
        System.out.println("Key Features Demonstrated:");
        System.out.println("  ✓ Hierarchical group structure");
        System.out.println("  ✓ Closest common group finding");
        System.out.println("  ✓ Shared groups (employees in multiple groups)");
        System.out.println("  ✓ Thread-safe concurrent operations");
        System.out.println("  ✓ Dynamic hierarchy updates");
        System.out.println("  ✓ Flat hierarchy support");
        System.out.println("═".repeat(64));
    }
    
    private static void printHierarchy() {
        System.out.println("\nOrganization Structure:");
        System.out.println("  Atlassian (G1)");
        System.out.println("  ├── Engineering (G2)");
        System.out.println("  │   ├── Backend Team (G4)");
        System.out.println("  │   │   ├── Alice");
        System.out.println("  │   │   └── Bob");
        System.out.println("  │   └── Frontend Team (G5)");
        System.out.println("  │       ├── Carol");
        System.out.println("  │       └── David");
        System.out.println("  └── Sales (G3)");
        System.out.println("      ├── Emma");
        System.out.println("      └── Frank");
    }
}

