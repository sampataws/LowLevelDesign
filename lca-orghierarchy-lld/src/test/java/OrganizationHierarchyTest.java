import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;

/**
 * Comprehensive test suite for OrganizationHierarchy LCA implementation
 * 
 * Test Coverage:
 * - Basic LCA operations
 * - Edge cases (null, empty, single employee)
 * - Same group scenarios
 * - Cross-branch scenarios
 * - Multi-level hierarchies
 * - Multiple employees
 * - Parent-child relationships
 */
public class OrganizationHierarchyTest {
    
    private OrganizationHierarchy org;
    private Group engineering;
    private Group backend;
    private Group frontend;
    private Group api;
    private Group infra;
    private Group web;
    private Group mobile;
    
    /**
     * Setup standard organization structure before each test:
     * 
     *                Engineering
     *                /          \
     *           Backend      Frontend
     *           /     \        /    \
     *        API     Infra   Web  Mobile
     */
    @Before
    public void setUp() {
        org = new OrganizationHierarchy();
        
        // Create groups
        engineering = new Group("Engineering");
        backend = new Group("Backend");
        frontend = new Group("Frontend");
        api = new Group("API");
        infra = new Group("Infra");
        web = new Group("Web");
        mobile = new Group("Mobile");
        
        // Build tree
        engineering.addSubGroup(backend);
        engineering.addSubGroup(frontend);
        backend.addSubGroup(api);
        backend.addSubGroup(infra);
        frontend.addSubGroup(web);
        frontend.addSubGroup(mobile);
    }
    
    // ========== Basic LCA Tests ==========
    
    @Test
    public void testLCA_SameBranch_SiblingGroups() {
        org.registerEmployee(api, "Alice");
        org.registerEmployee(infra, "Bob");
        
        Group result = org.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob"));
        
        assertEquals("Backend", result.name);
    }
    
    @Test
    public void testLCA_DifferentBranches() {
        org.registerEmployee(api, "Alice");
        org.registerEmployee(web, "Carol");
        
        Group result = org.getCommonGroupForEmployees(Arrays.asList("Alice", "Carol"));
        
        assertEquals("Engineering", result.name);
    }
    
    @Test
    public void testLCA_SameGroup() {
        org.registerEmployee(api, "Alice");
        org.registerEmployee(api, "Bob");
        
        Group result = org.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob"));
        
        assertEquals("API", result.name);
    }
    
    @Test
    public void testLCA_ParentChild() {
        org.registerEmployee(backend, "Manager");
        org.registerEmployee(api, "Alice");
        
        Group result = org.getCommonGroupForEmployees(Arrays.asList("Manager", "Alice"));
        
        assertEquals("Backend", result.name);
    }
    
    // ========== Single Employee Tests ==========
    
    @Test
    public void testLCA_SingleEmployee() {
        org.registerEmployee(api, "Alice");
        
        Group result = org.getCommonGroupForEmployees(Collections.singletonList("Alice"));
        
        assertEquals("API", result.name);
    }
    
    @Test
    public void testLCA_SingleEmployee_RootLevel() {
        org.registerEmployee(engineering, "CEO");
        
        Group result = org.getCommonGroupForEmployees(Collections.singletonList("CEO"));
        
        assertEquals("Engineering", result.name);
    }
    
    // ========== Multiple Employees Tests ==========
    
    @Test
    public void testLCA_ThreeEmployees_SameBranch() {
        org.registerEmployee(api, "Alice");
        org.registerEmployee(infra, "Bob");
        org.registerEmployee(backend, "Charlie");
        
        Group result = org.getCommonGroupForEmployees(
            Arrays.asList("Alice", "Bob", "Charlie")
        );
        
        assertEquals("Backend", result.name);
    }
    
    @Test
    public void testLCA_ThreeEmployees_DifferentBranches() {
        org.registerEmployee(api, "Alice");
        org.registerEmployee(infra, "Bob");
        org.registerEmployee(web, "Carol");
        
        Group result = org.getCommonGroupForEmployees(
            Arrays.asList("Alice", "Bob", "Carol")
        );
        
        assertEquals("Engineering", result.name);
    }
    
    @Test
    public void testLCA_FourEmployees_AllLeaves() {
        org.registerEmployee(api, "Alice");
        org.registerEmployee(infra, "Bob");
        org.registerEmployee(web, "Carol");
        org.registerEmployee(mobile, "David");
        
        Group result = org.getCommonGroupForEmployees(
            Arrays.asList("Alice", "Bob", "Carol", "David")
        );
        
        assertEquals("Engineering", result.name);
    }
    
    @Test
    public void testLCA_MultipleEmployees_FrontendOnly() {
        org.registerEmployee(web, "Carol");
        org.registerEmployee(mobile, "David");
        
        Group result = org.getCommonGroupForEmployees(
            Arrays.asList("Carol", "David")
        );
        
        assertEquals("Frontend", result.name);
    }
    
    // ========== Edge Cases ==========
    
    @Test
    public void testLCA_NullList() {
        Group result = org.getCommonGroupForEmployees(null);
        
        assertNull(result);
    }
    
    @Test
    public void testLCA_EmptyList() {
        Group result = org.getCommonGroupForEmployees(Collections.emptyList());
        
        assertNull(result);
    }
    
    @Test
    public void testLCA_EmployeeNotFound() {
        org.registerEmployee(api, "Alice");
        
        Group result = org.getCommonGroupForEmployees(
            Arrays.asList("Alice", "Unknown")
        );
        
        assertNull(result);
    }
    
    @Test
    public void testLCA_AllEmployeesNotFound() {
        Group result = org.getCommonGroupForEmployees(
            Arrays.asList("Unknown1", "Unknown2")
        );
        
        assertNull(result);
    }
    
    @Test
    public void testLCA_FirstEmployeeNotFound() {
        org.registerEmployee(api, "Bob");
        
        Group result = org.getCommonGroupForEmployees(
            Arrays.asList("Unknown", "Bob")
        );
        
        assertNull(result);
    }
    
    // ========== Deep Hierarchy Tests ==========
    
    @Test
    public void testLCA_DeepHierarchy() {
        // Create deeper hierarchy
        Group level1 = new Group("Level1");
        Group level2 = new Group("Level2");
        Group level3 = new Group("Level3");
        Group level4 = new Group("Level4");
        
        level1.addSubGroup(level2);
        level2.addSubGroup(level3);
        level3.addSubGroup(level4);
        
        org.registerEmployee(level4, "DeepEmployee");
        org.registerEmployee(level2, "ShallowEmployee");
        
        Group result = org.getCommonGroupForEmployees(
            Arrays.asList("DeepEmployee", "ShallowEmployee")
        );
        
        assertEquals("Level2", result.name);
    }
    
    // ========== Complex Scenarios ==========
    
    @Test
    public void testLCA_ComplexMultiLevelOrg() {
        // Create a more complex organization
        Group company = new Group("Company");
        Group sales = new Group("Sales");
        Group regional = new Group("Regional");
        
        company.addSubGroup(engineering);
        company.addSubGroup(sales);
        sales.addSubGroup(regional);
        
        org.registerEmployee(api, "Alice");
        org.registerEmployee(regional, "Eve");
        
        Group result = org.getCommonGroupForEmployees(
            Arrays.asList("Alice", "Eve")
        );
        
        assertEquals("Company", result.name);
    }
    
    @Test
    public void testLCA_MultipleEmployeesInSameGroup() {
        org.registerEmployee(api, "Alice");
        org.registerEmployee(api, "Bob");
        org.registerEmployee(api, "Charlie");
        
        Group result = org.getCommonGroupForEmployees(
            Arrays.asList("Alice", "Bob", "Charlie")
        );
        
        assertEquals("API", result.name);
    }
    
    // ========== Registration Tests ==========
    
    @Test
    public void testRegisterEmployee_SingleEmployee() {
        org.registerEmployee(api, "Alice");
        
        Group result = org.getCommonGroupForEmployees(
            Collections.singletonList("Alice")
        );
        
        assertNotNull(result);
        assertEquals("API", result.name);
    }
    
    @Test
    public void testRegisterEmployee_MultipleInDifferentGroups() {
        org.registerEmployee(api, "Alice");
        org.registerEmployee(infra, "Bob");
        org.registerEmployee(web, "Carol");
        org.registerEmployee(mobile, "David");
        
        // Verify each employee is in correct group
        assertEquals("API", org.getCommonGroupForEmployees(
            Collections.singletonList("Alice")).name);
        assertEquals("Infra", org.getCommonGroupForEmployees(
            Collections.singletonList("Bob")).name);
        assertEquals("Web", org.getCommonGroupForEmployees(
            Collections.singletonList("Carol")).name);
        assertEquals("Mobile", org.getCommonGroupForEmployees(
            Collections.singletonList("David")).name);
    }
    
    // ========== Group Structure Tests ==========
    
    @Test
    public void testGroupStructure_ParentPointers() {
        assertNull(engineering.parent);
        assertEquals(engineering, backend.parent);
        assertEquals(engineering, frontend.parent);
        assertEquals(backend, api.parent);
        assertEquals(backend, infra.parent);
        assertEquals(frontend, web.parent);
        assertEquals(frontend, mobile.parent);
    }
    
    @Test
    public void testGroupStructure_SubGroups() {
        assertEquals(2, engineering.subGroups.size());
        assertTrue(engineering.subGroups.contains(backend));
        assertTrue(engineering.subGroups.contains(frontend));
        
        assertEquals(2, backend.subGroups.size());
        assertTrue(backend.subGroups.contains(api));
        assertTrue(backend.subGroups.contains(infra));
    }
}

