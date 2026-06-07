package dao;

import model.Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManagerDAO {

    // [REQ10] Authenticates a manager with email/password bind variables.
    public Manager findByEmailAndPassword(Connection conn,
                                          String email,
                                          String password) throws SQLException {
        String sql = """
                SELECT m.manager_id,
                       m.manager_name,
                       m.email,
                       m.password
                FROM manager m
                WHERE m.email = ?
                AND m.password = ?
                """;

        // [REQ10] Manager email and password are bound through PreparedStatement.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Manager manager = toManager(rs);
                    loadRoles(conn, manager);
                    return manager;
                }
                return null;
            }
        }
    }

    // [REQ10] Checks whether a manager email exists before password attempts.
    public Integer findManagerIdByEmail(Connection conn, String email) throws SQLException {
        String sql = """
                SELECT manager_id
                FROM manager
                WHERE email = ?
                """;

        // [REQ10] Manager email is bound to the SELECT query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("manager_id");
                }
                return null;
            }
        }
    }

    // [REQ10] Loads manager account information by manager id.
    public Manager findById(Connection conn, int managerId) throws SQLException {
        String sql = """
                SELECT manager_id,
                       manager_name,
                       email,
                       password
                FROM manager
                WHERE manager_id = ?
                """;

        // [REQ10] Manager id is bound to the SELECT query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, managerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Manager manager = toManager(rs);
                    loadRoles(conn, manager);
                    return manager;
                }
                return null;
            }
        }
    }

    // [REQ5][REQ10] Inserts a new manager account from MASTER-entered data.
    public boolean insertManager(Connection conn, int managerId, String managerName,
                                 String email, String password) throws SQLException {
        String sql = """
                INSERT INTO manager
                (manager_id, manager_name, email, password)
                VALUES (?, ?, ?, ?)
                """;

        // [REQ10] Manager fields are bound through PreparedStatement.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, managerId);
            pstmt.setString(2, managerName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);

            return pstmt.executeUpdate() > 0;
        }
    }

    // [REQ10] Validates that the selected role id exists.
    public boolean existsRole(Connection conn, int roleId) throws SQLException {
        String sql = """
                SELECT 1
                FROM manager_role
                WHERE role_id = ?
                """;

        // [REQ10] Role id is bound to the validation query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // [REQ8][REQ10] Assigns a role to a manager through the bridge table.
    public boolean assignManagerRole(Connection conn, int managerId, int roleId) throws SQLException {
        String sql = """
                INSERT IGNORE INTO manager_role_assignment
                (manager_id, role_id)
                VALUES (?, ?)
                """;

        // [REQ10] Manager id and role id are bound to the assignment query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, managerId);
            pstmt.setInt(2, roleId);

            return pstmt.executeUpdate() > 0;
        }
    }

    // [REQ8][REQ10] Removes one role assignment from a manager.
    public boolean removeManagerRole(Connection conn, int managerId, int roleId) throws SQLException {
        String sql = """
                DELETE FROM manager_role_assignment
                WHERE manager_id = ?
                AND role_id = ?
                """;

        // [REQ10] Manager id and role id are bound to the delete query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, managerId);
            pstmt.setInt(2, roleId);

            return pstmt.executeUpdate() > 0;
        }
    }

    // [REQ9][REQ12] Removes all role assignments before deleting a manager account.
    public void removeAllManagerRoles(Connection conn, int managerId) throws SQLException {
        String sql = """
                DELETE FROM manager_role_assignment
                WHERE manager_id = ?
                """;

        // [REQ10] Manager id is bound to the role cleanup query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, managerId);
            pstmt.executeUpdate();
        }
    }

    // [REQ9][REQ10] Deletes a manager account by manager id.
    public boolean deleteManager(Connection conn, int managerId) throws SQLException {
        String sql = """
                DELETE FROM manager
                WHERE manager_id = ?
                """;

        // [REQ10] Manager id is bound to the delete query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, managerId);

            return pstmt.executeUpdate() > 0;
        }
    }

    // [REQ10] Loads all roles assigned to a manager through manager_role_assignment.
    private void loadRoles(Connection conn, Manager manager) throws SQLException {
        String sql = """
                SELECT mr.role_name
                FROM manager_role_assignment mra
                JOIN manager_role mr ON mra.role_id = mr.role_id
                WHERE mra.manager_id = ?
                ORDER BY mr.role_id
                """;

        // [REQ10] Manager id is bound to the role lookup query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, manager.getManagerId());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    manager.addRoleName(rs.getString("role_name"));
                }
            }
        }
    }

    // [REQ17] Converts a JDBC ResultSet row into a Manager model object.
    private Manager toManager(ResultSet rs) throws SQLException {
        return new Manager(
                rs.getInt("manager_id"),
                rs.getString("manager_name"),
                rs.getString("email"),
                rs.getString("password")
        );
    }
}
