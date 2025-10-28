import java.util.*;

/*


                   ┌────────────────────────────┐
                   │        Engineering         │   ← Root group
                   └────────────────────────────┘
                           /             \
                          /               \
           ┌────────────────────┐     ┌──────────────────┐
           │      Backend       │     │     Frontend     │
           └────────────────────┘     └──────────────────┘
                 /        \                    ↑
                /          \        Carol (belongs to Frontend)
     ┌────────────────┐  ┌────────────────┐
     │      API       │  │     Infra      │
     └────────────────┘  └────────────────┘
           ↑                     ↑
           │                     │
         Alice                 Bob




 */


public class OrganizationHierarchy {

    private Map<String, Group> employeeToGroup = new HashMap<>();
    private Group root;

    public OrganizationHierarchy() {
        this.root = root;
    }

    public void registerEmployee(Group group, String employee) {
        group.addEmployee(employee);
        employeeToGroup.put(employee, group);
    }

    public Group getCommonGroupForEmployees(List<String> employeeNames) {
        if (employeeNames == null || employeeNames.isEmpty()) return null;

        // start with the group of the first employee
        Group common = employeeToGroup.get(employeeNames.get(0));
        if (common == null) return null;

        for (int i = 1; i < employeeNames.size(); i++) {
            Group other = employeeToGroup.get(employeeNames.get(i));
            if (other == null) return null;
            common = findLCA(common, other);
            if (common == null) return null;
        }

        return common;
    }

    private Group findLCA(Group a, Group b) {
        // record all ancestors of a
        Set<Group> ancestors = new HashSet<>();
        while (a != null) {
            ancestors.add(a);
            a = a.parent;
        }

        // climb b’s chain until we find a shared ancestor
        while (b != null) {
            if (ancestors.contains(b)) return b;
            b = b.parent;
        }

        return null;
    }
}
