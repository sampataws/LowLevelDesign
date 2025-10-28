import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test suite for Group class
 * 
 * Test Coverage:
 * - Group creation
 * - Adding sub-groups
 * - Adding employees
 * - Parent-child relationships
 * - toString method
 */
public class GroupTest {
    
    private Group parent;
    private Group child1;
    private Group child2;
    
    @Before
    public void setUp() {
        parent = new Group("Parent");
        child1 = new Group("Child1");
        child2 = new Group("Child2");
    }
    
    // ========== Constructor Tests ==========
    
    @Test
    public void testConstructor_CreatesGroupWithName() {
        Group group = new Group("TestGroup");
        
        assertEquals("TestGroup", group.name);
        assertNull(group.parent);
        assertNotNull(group.subGroups);
        assertNotNull(group.employees);
        assertTrue(group.subGroups.isEmpty());
        assertTrue(group.employees.isEmpty());
    }
    
    @Test
    public void testConstructor_EmptyName() {
        Group group = new Group("");
        
        assertEquals("", group.name);
    }
    
    @Test
    public void testConstructor_NullName() {
        Group group = new Group(null);
        
        assertNull(group.name);
    }
    
    // ========== Add SubGroup Tests ==========
    
    @Test
    public void testAddSubGroup_SingleChild() {
        parent.addSubGroup(child1);
        
        assertEquals(1, parent.subGroups.size());
        assertTrue(parent.subGroups.contains(child1));
        assertEquals(parent, child1.parent);
    }
    
    @Test
    public void testAddSubGroup_MultipleChildren() {
        parent.addSubGroup(child1);
        parent.addSubGroup(child2);
        
        assertEquals(2, parent.subGroups.size());
        assertTrue(parent.subGroups.contains(child1));
        assertTrue(parent.subGroups.contains(child2));
        assertEquals(parent, child1.parent);
        assertEquals(parent, child2.parent);
    }
    
    @Test
    public void testAddSubGroup_SetsParentPointer() {
        assertNull(child1.parent);
        
        parent.addSubGroup(child1);
        
        assertNotNull(child1.parent);
        assertEquals(parent, child1.parent);
    }
    
    @Test
    public void testAddSubGroup_OverwritesExistingParent() {
        Group oldParent = new Group("OldParent");
        oldParent.addSubGroup(child1);
        
        assertEquals(oldParent, child1.parent);
        
        parent.addSubGroup(child1);
        
        assertEquals(parent, child1.parent);
    }
    
    @Test
    public void testAddSubGroup_DeepHierarchy() {
        Group level1 = new Group("Level1");
        Group level2 = new Group("Level2");
        Group level3 = new Group("Level3");
        
        level1.addSubGroup(level2);
        level2.addSubGroup(level3);
        
        assertEquals(level1, level2.parent);
        assertEquals(level2, level3.parent);
        assertNull(level1.parent);
    }
    
    // ========== Add Employee Tests ==========
    
    @Test
    public void testAddEmployee_SingleEmployee() {
        parent.addEmployee("Alice");
        
        assertEquals(1, parent.employees.size());
        assertTrue(parent.employees.contains("Alice"));
    }
    
    @Test
    public void testAddEmployee_MultipleEmployees() {
        parent.addEmployee("Alice");
        parent.addEmployee("Bob");
        parent.addEmployee("Carol");
        
        assertEquals(3, parent.employees.size());
        assertTrue(parent.employees.contains("Alice"));
        assertTrue(parent.employees.contains("Bob"));
        assertTrue(parent.employees.contains("Carol"));
    }
    
    @Test
    public void testAddEmployee_DuplicateEmployee() {
        parent.addEmployee("Alice");
        parent.addEmployee("Alice");
        
        // List allows duplicates
        assertEquals(2, parent.employees.size());
    }
    
    @Test
    public void testAddEmployee_NullEmployee() {
        parent.addEmployee(null);
        
        assertEquals(1, parent.employees.size());
        assertTrue(parent.employees.contains(null));
    }
    
    @Test
    public void testAddEmployee_EmptyString() {
        parent.addEmployee("");
        
        assertEquals(1, parent.employees.size());
        assertTrue(parent.employees.contains(""));
    }
    
    // ========== ToString Tests ==========
    
    @Test
    public void testToString_ReturnsGroupName() {
        Group group = new Group("Engineering");
        
        assertEquals("Engineering", group.toString());
    }
    
    @Test
    public void testToString_EmptyName() {
        Group group = new Group("");
        
        assertEquals("", group.toString());
    }
    
    @Test
    public void testToString_NullName() {
        Group group = new Group(null);

        assertNull(group.toString());
    }
    
    // ========== Integration Tests ==========
    
    @Test
    public void testCompleteGroupSetup() {
        Group engineering = new Group("Engineering");
        Group backend = new Group("Backend");
        Group api = new Group("API");
        
        engineering.addSubGroup(backend);
        backend.addSubGroup(api);
        
        api.addEmployee("Alice");
        api.addEmployee("Bob");
        backend.addEmployee("Manager");
        
        // Verify structure
        assertEquals(engineering, backend.parent);
        assertEquals(backend, api.parent);
        assertNull(engineering.parent);
        
        // Verify employees
        assertEquals(2, api.employees.size());
        assertEquals(1, backend.employees.size());
        assertEquals(0, engineering.employees.size());
        
        // Verify subgroups
        assertEquals(1, engineering.subGroups.size());
        assertEquals(1, backend.subGroups.size());
        assertEquals(0, api.subGroups.size());
    }
    
    @Test
    public void testMultipleBranches() {
        Group root = new Group("Root");
        Group branch1 = new Group("Branch1");
        Group branch2 = new Group("Branch2");
        Group leaf1 = new Group("Leaf1");
        Group leaf2 = new Group("Leaf2");
        
        root.addSubGroup(branch1);
        root.addSubGroup(branch2);
        branch1.addSubGroup(leaf1);
        branch2.addSubGroup(leaf2);
        
        // Verify structure
        assertEquals(2, root.subGroups.size());
        assertEquals(1, branch1.subGroups.size());
        assertEquals(1, branch2.subGroups.size());
        
        assertEquals(root, branch1.parent);
        assertEquals(root, branch2.parent);
        assertEquals(branch1, leaf1.parent);
        assertEquals(branch2, leaf2.parent);
    }
    
    // ========== Edge Case Tests ==========
    
    @Test
    public void testAddSubGroup_SameGroupMultipleTimes() {
        parent.addSubGroup(child1);
        parent.addSubGroup(child1);
        
        // List allows duplicates
        assertEquals(2, parent.subGroups.size());
        assertEquals(parent, child1.parent);
    }
    
    @Test
    public void testCircularReference_ChildAsParent() {
        parent.addSubGroup(child1);
        
        // This creates a circular reference (not prevented by current implementation)
        child1.addSubGroup(parent);
        
        assertEquals(child1, parent.parent);
        assertEquals(parent, child1.parent);
    }
    
    @Test
    public void testSelfReference() {
        // Adding group as its own subgroup
        parent.addSubGroup(parent);
        
        assertEquals(1, parent.subGroups.size());
        assertEquals(parent, parent.parent);
    }
    
    // ========== Initialization Tests ==========
    
    @Test
    public void testInitialState_EmptySubGroups() {
        Group group = new Group("Test");
        
        assertNotNull(group.subGroups);
        assertEquals(0, group.subGroups.size());
    }
    
    @Test
    public void testInitialState_EmptyEmployees() {
        Group group = new Group("Test");
        
        assertNotNull(group.employees);
        assertEquals(0, group.employees.size());
    }
    
    @Test
    public void testInitialState_NullParent() {
        Group group = new Group("Test");
        
        assertNull(group.parent);
    }
    
    // ========== Name Tests ==========
    
    @Test
    public void testName_SpecialCharacters() {
        Group group = new Group("Test@#$%^&*()");
        
        assertEquals("Test@#$%^&*()", group.name);
    }
    
    @Test
    public void testName_Unicode() {
        Group group = new Group("测试");
        
        assertEquals("测试", group.name);
    }
    
    @Test
    public void testName_LongName() {
        String longName = "A".repeat(1000);
        Group group = new Group(longName);
        
        assertEquals(longName, group.name);
    }
    
    @Test
    public void testName_WithSpaces() {
        Group group = new Group("Engineering Team");
        
        assertEquals("Engineering Team", group.name);
    }
}

