package model;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringJoiner;

// [REQ17] Model class representing a manager account and its assigned roles.
public class Manager {
    private int managerId;
    private String managerName;
    private String email;
    private String password;
    private Set<String> roleNames = new LinkedHashSet<>();

    // [REQ17] Default constructor used when creating manager objects manually.
    public Manager() {
    }

    // [REQ17] Constructor maps manager table columns to Java fields.
    public Manager(int managerId, String managerName, String email, String password) {
        this.managerId = managerId;
        this.managerName = managerName;
        this.email = email;
        this.password = password;
    }

    // [REQ17] Standard getters and setters expose manager fields and role assignments.
    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(Set<String> roleNames) {
        this.roleNames = new LinkedHashSet<>(roleNames);
    }

    public void addRoleName(String roleName) {
        roleNames.add(roleName);
    }

    // [REQ15] Checks role-based access permissions in ManagerMenu.
    public boolean hasRole(String roleName) {
        return roleNames.contains(roleName);
    }

    public String getRoleName() {
        return getRoleSummary();
    }

    public void setRoleName(String roleName) {
        this.roleNames.clear();
        this.roleNames.add(roleName);
    }

    // [REQ15] Returns manager roles as readable text for the console UI.
    public String getRoleSummary() {
        StringJoiner joiner = new StringJoiner(", ");
        roleNames.forEach(joiner::add);
        return joiner.toString();
    }
}
