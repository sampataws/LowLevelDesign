import java.util.*;

public class DemoLCA {
    public static void main(String[] args) {
        OrganizationHierarchy org = new OrganizationHierarchy();

        // Build org tree
        Group engineering = new Group("Engineering");
        Group backend = new Group("Backend");
        Group frontend = new Group("Frontend");
        Group api = new Group("API");
        Group infra = new Group("Infra");

        engineering.addSubGroup(backend);
        engineering.addSubGroup(frontend);
        backend.addSubGroup(api);
        backend.addSubGroup(infra);

        // Register employees
        org.registerEmployee(api, "Alice");
        org.registerEmployee(infra, "Bob");
        org.registerEmployee(frontend, "Carol");

        // Queries
        System.out.println("Common group (Alice, Bob): " +
                org.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob")));
        System.out.println("Common group (Alice, Carol): " +
                org.getCommonGroupForEmployees(Arrays.asList("Alice", "Carol")));
        System.out.println("Common group (Alice): " +
                org.getCommonGroupForEmployees(Collections.singletonList("Alice")));
    }
}
