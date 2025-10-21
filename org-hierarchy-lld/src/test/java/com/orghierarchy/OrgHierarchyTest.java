package com.orghierarchy;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Comprehensive tests for OrgHierarchy system.
 */
public class OrgHierarchyTest {
    
    private OrgHierarchy org;
    private Employee alice, bob, carol, david;
    private Group atlassian, engineering, sales, backend, frontend;
    
    @Before
    public void setUp() {
        org = new OrgHierarchy();
        
        // Create employees
        alice = new Employee("E001", "Alice", "alice@test.com");
        bob = new Employee("E002", "Bob", "bob@test.com");
        carol = new Employee("E003", "Carol", "carol@test.com");
        david = new Employee("E004", "David", "david@test.com");
        
        org.addEmployee(alice);
        org.addEmployee(bob);
        org.addEmployee(carol);
        org.addEmployee(david);
        
        // Create hierarchy:
        //        Atlassian
        //        /       \
        //   Engineering  Sales
        //    /      \
        // Backend  Frontend
        
        atlassian = new Group("G1", "Atlassian");
        engineering = new Group("G2", "Engineering");
        sales = new Group("G3", "Sales");
        backend = new Group("G4", "Backend");
        frontend = new Group("G5", "Frontend");
        
        org.addGroup(atlassian);
        org.addGroup(engineering);
        org.addGroup(sales);
        org.addGroup(backend);
        org.addGroup(frontend);
        
        org.addGroupToGroup("G2", "G1");
        org.addGroupToGroup("G3", "G1");
        org.addGroupToGroup("G4", "G2");
        org.addGroupToGroup("G5", "G2");
        
        // Add employees to groups
        org.addEmployeeToGroup("E001", "G4"); // Alice -> Backend
        org.addEmployeeToGroup("E002", "G4"); // Bob -> Backend
        org.addEmployeeToGroup("E003", "G5"); // Carol -> Frontend
        org.addEmployeeToGroup("E004", "G3"); // David -> Sales
    }
    
    @Test
    public void testAddEmployee() {
        Employee newEmp = new Employee("E005", "Emma", "emma@test.com");
        org.addEmployee(newEmp);
        
        assertNotNull(org.getEmployee("E005"));
        assertEquals("Emma", org.getEmployee("E005").getName());
    }
    
    @Test
    public void testAddGroup() {
        Group newGroup = new Group("G6", "Platform");
        org.addGroup(newGroup);
        
        assertNotNull(org.getGroup("G6"));
        assertEquals("Platform", org.getGroup("G6").getName());
    }
    
    @Test
    public void testAddEmployeeToGroup() {
        Set<String> groups = org.getGroupsForEmployee("E001");
        assertTrue(groups.contains("G4"));
    }
    
    @Test
    public void testGetEmployeesInGroup() {
        Set<String> employees = org.getEmployeesInGroup("G4");
        
        assertEquals(2, employees.size());
        assertTrue(employees.contains("E001"));
        assertTrue(employees.contains("E002"));
    }
    
    @Test
    public void testClosestCommonGroup_SameTeam() {
        Set<String> employees = new HashSet<>(Arrays.asList("E001", "E002"));
        Group common = org.getClosestCommonGroup(employees);
        
        assertNotNull(common);
        assertEquals("G4", common.getGroupId());
        assertEquals("Backend", common.getName());
    }
    
    @Test
    public void testClosestCommonGroup_SameDepartment() {
        Set<String> employees = new HashSet<>(Arrays.asList("E001", "E003"));
        Group common = org.getClosestCommonGroup(employees);
        
        assertNotNull(common);
        assertEquals("G2", common.getGroupId());
        assertEquals("Engineering", common.getName());
    }
    
    @Test
    public void testClosestCommonGroup_DifferentDepartments() {
        Set<String> employees = new HashSet<>(Arrays.asList("E001", "E004"));
        Group common = org.getClosestCommonGroup(employees);
        
        assertNotNull(common);
        assertEquals("G1", common.getGroupId());
        assertEquals("Atlassian", common.getName());
    }
    
    @Test
    public void testClosestCommonGroup_ThreeEmployees() {
        Set<String> employees = new HashSet<>(Arrays.asList("E001", "E002", "E003"));
        Group common = org.getClosestCommonGroup(employees);
        
        assertNotNull(common);
        assertEquals("G2", common.getGroupId());
    }
    
    @Test
    public void testClosestCommonGroup_AllEmployees() {
        Set<String> employees = new HashSet<>(Arrays.asList("E001", "E002", "E003", "E004"));
        Group common = org.getClosestCommonGroup(employees);
        
        assertNotNull(common);
        assertEquals("G1", common.getGroupId());
    }
    
    @Test
    public void testSharedGroups_EmployeeInMultipleGroups() {
        // Add Alice to Frontend as well
        org.addEmployeeToGroup("E001", "G5");
        
        Set<String> groups = org.getGroupsForEmployee("E001");
        assertEquals(2, groups.size());
        assertTrue(groups.contains("G4"));
        assertTrue(groups.contains("G5"));
    }
    
    @Test
    public void testSharedGroups_CommonGroupWithSharedEmployee() {
        // Alice is in both Backend and Frontend
        org.addEmployeeToGroup("E001", "G5");
        
        // Alice and Carol should have Frontend as common group
        Set<String> employees = new HashSet<>(Arrays.asList("E001", "E003"));
        Group common = org.getClosestCommonGroup(employees);
        
        assertNotNull(common);
        // Should be Frontend (G5) since Alice is in both Backend and Frontend
        assertEquals("G5", common.getGroupId());
    }
    
    @Test
    public void testRemoveEmployeeFromGroup() {
        org.removeEmployeeFromGroup("E001", "G4");
        
        Set<String> employees = org.getEmployeesInGroup("G4");
        assertFalse(employees.contains("E001"));
        
        Set<String> groups = org.getGroupsForEmployee("E001");
        assertFalse(groups.contains("G4"));
    }
    
    @Test
    public void testMoveEmployeeToGroup() {
        org.moveEmployeeToGroup("E001", "G4", "G5");
        
        Set<String> backendEmps = org.getEmployeesInGroup("G4");
        assertFalse(backendEmps.contains("E001"));
        
        Set<String> frontendEmps = org.getEmployeesInGroup("G5");
        assertTrue(frontendEmps.contains("E001"));
    }
    
    @Test
    public void testGetAllEmployeesInGroupHierarchy() {
        Set<String> allEmps = org.getAllEmployeesInGroupHierarchy("G2");
        
        assertEquals(3, allEmps.size());
        assertTrue(allEmps.contains("E001")); // Backend
        assertTrue(allEmps.contains("E002")); // Backend
        assertTrue(allEmps.contains("E003")); // Frontend
    }
    
    @Test
    public void testCycleDetection() {
        try {
            // Try to create a cycle: G1 -> G2 -> G1
            org.addGroupToGroup("G1", "G2");
            fail("Should have thrown exception for cycle");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("cycle"));
        }
    }
    
    @Test
    public void testMultipleParents_SharedGroup() {
        // Create a shared group scenario
        Group platform = new Group("G6", "Platform");
        org.addGroup(platform);
        
        // Platform is under both Engineering and Sales
        org.addGroupToGroup("G6", "G2");
        org.addGroupToGroup("G6", "G3");
        
        Group platformGroup = org.getGroup("G6");
        assertEquals(2, platformGroup.getParentGroupIds().size());
    }
    
    @Test
    public void testConcurrentReads() throws InterruptedException {
        final int numThreads = 10;
        final CountDownLatch latch = new CountDownLatch(numThreads);
        final AtomicInteger successCount = new AtomicInteger(0);
        
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                try {
                    Set<String> employees = new HashSet<>(Arrays.asList("E001", "E003"));
                    Group common = org.getClosestCommonGroup(employees);
                    
                    if (common != null && "G2".equals(common.getGroupId())) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
            threads[i].start();
        }
        
        latch.await();
        assertEquals(numThreads, successCount.get());
    }
    
    @Test
    public void testConcurrentWritesAndReads() throws InterruptedException {
        final int numThreads = 20;
        final CountDownLatch latch = new CountDownLatch(numThreads);
        
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                try {
                    if (threadId % 2 == 0) {
                        // Write operation
                        Employee emp = new Employee("E" + (100 + threadId), "Emp" + threadId, "emp@test.com");
                        org.addEmployee(emp);
                        org.addEmployeeToGroup("E" + (100 + threadId), "G4");
                    } else {
                        // Read operation
                        Set<String> employees = new HashSet<>(Arrays.asList("E001", "E002"));
                        org.getClosestCommonGroup(employees);
                    }
                } finally {
                    latch.countDown();
                }
            });
            threads[i].start();
        }
        
        latch.await();
        
        // Verify backend group has new employees
        Set<String> backendEmps = org.getEmployeesInGroup("G4");
        assertTrue(backendEmps.size() >= 2); // At least original 2
    }
    
    @Test
    public void testFlatHierarchy() {
        OrgHierarchy flatOrg = new OrgHierarchy();
        
        Group team1 = new Group("T1", "Team 1");
        Group team2 = new Group("T2", "Team 2");
        
        flatOrg.addGroup(team1);
        flatOrg.addGroup(team2);
        
        Employee emp1 = new Employee("F001", "John", "john@test.com");
        Employee emp2 = new Employee("F002", "Jane", "jane@test.com");
        
        flatOrg.addEmployee(emp1);
        flatOrg.addEmployee(emp2);
        
        flatOrg.addEmployeeToGroup("F001", "T1");
        flatOrg.addEmployeeToGroup("F002", "T2");
        
        // No common group in flat structure
        Set<String> employees = new HashSet<>(Arrays.asList("F001", "F002"));
        Group common = flatOrg.getClosestCommonGroup(employees);
        
        assertNull(common); // No common parent
    }
    
    @Test
    public void testFlatHierarchy_SharedEmployee() {
        OrgHierarchy flatOrg = new OrgHierarchy();
        
        Group team1 = new Group("T1", "Team 1");
        Group team2 = new Group("T2", "Team 2");
        
        flatOrg.addGroup(team1);
        flatOrg.addGroup(team2);
        
        Employee emp1 = new Employee("F001", "John", "john@test.com");
        Employee emp2 = new Employee("F002", "Jane", "jane@test.com");
        
        flatOrg.addEmployee(emp1);
        flatOrg.addEmployee(emp2);
        
        // John is in both teams
        flatOrg.addEmployeeToGroup("F001", "T1");
        flatOrg.addEmployeeToGroup("F001", "T2");
        flatOrg.addEmployeeToGroup("F002", "T2");
        
        // Common group should be T2
        Set<String> employees = new HashSet<>(Arrays.asList("F001", "F002"));
        Group common = flatOrg.getClosestCommonGroup(employees);
        
        assertNotNull(common);
        assertEquals("T2", common.getGroupId());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetClosestCommonGroup_EmptySet() {
        org.getClosestCommonGroup(new HashSet<>());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetClosestCommonGroup_NonExistentEmployee() {
        Set<String> employees = new HashSet<>(Arrays.asList("E999"));
        org.getClosestCommonGroup(employees);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetClosestCommonGroup_EmployeeNotInAnyGroup() {
        Employee orphan = new Employee("E999", "Orphan", "orphan@test.com");
        org.addEmployee(orphan);
        
        Set<String> employees = new HashSet<>(Arrays.asList("E999"));
        org.getClosestCommonGroup(employees);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddEmployeeToGroup_NonExistentEmployee() {
        org.addEmployeeToGroup("E999", "G1");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddEmployeeToGroup_NonExistentGroup() {
        org.addEmployeeToGroup("E001", "G999");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddGroupToGroup_NonExistentChild() {
        org.addGroupToGroup("G999", "G1");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAddGroupToGroup_NonExistentParent() {
        org.addGroupToGroup("G1", "G999");
    }
}

