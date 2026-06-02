package model;

public class Manager {
    private int managerId;
    private int roleId;
    private String managerName;
    private String email;
    private String password;
    private String roleName;

    public Manager() {
    }

    public Manager(int managerId, int roleId, String managerName,
                   String email, String password, String roleName) {
        this.managerId = managerId;
        this.roleId = roleId;
        this.managerName = managerName;
        this.email = email;
        this.password = password;
        this.roleName = roleName;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
