package dao;

import model.Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManagerDAO {

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

    public Integer findManagerIdByEmail(Connection conn, String email) throws SQLException {
        String sql = """
                SELECT manager_id
                FROM manager
                WHERE email = ?
                """;

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

    public boolean existsRole(Connection conn, int roleId) throws SQLException {
        String sql = """
                SELECT 1
                FROM manager_role
                WHERE role_id = ?
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean assignManagerRole(Connection conn, int managerId, int roleId) throws SQLException {
        String sql = """
                INSERT IGNORE INTO manager_role_assignment
                (manager_id, role_id)
                VALUES (?, ?)
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, managerId);
            pstmt.setInt(2, roleId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean removeManagerRole(Connection conn, int managerId, int roleId) throws SQLException {
        String sql = """
                DELETE FROM manager_role_assignment
                WHERE manager_id = ?
                AND role_id = ?
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, managerId);
            pstmt.setInt(2, roleId);

            return pstmt.executeUpdate() > 0;
        }
    }

    private void loadRoles(Connection conn, Manager manager) throws SQLException {
        String sql = """
                SELECT mr.role_name
                FROM manager_role_assignment mra
                JOIN manager_role mr ON mra.role_id = mr.role_id
                WHERE mra.manager_id = ?
                ORDER BY mr.role_id
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, manager.getManagerId());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    manager.addRoleName(rs.getString("role_name"));
                }
            }
        }
    }

    private Manager toManager(ResultSet rs) throws SQLException {
        return new Manager(
                rs.getInt("manager_id"),
                rs.getString("manager_name"),
                rs.getString("email"),
                rs.getString("password")
        );
    }
}
