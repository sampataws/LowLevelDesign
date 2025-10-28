import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
class Group {
    String name;
    Group parent;
    List<Group> subGroups = new ArrayList<>();
    List<String> employees = new ArrayList<>();

    Group(String name) {
        this.name = name;
    }

    void addSubGroup(Group child) {
        child.parent = this;
        this.subGroups.add(child);
    }

    void addEmployee(String employee) {
        employees.add(employee);
    }

    @Override
    public String toString() { return name; }
}