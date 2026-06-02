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
                       m.role_id,
                       m.manager_name,
                       m.email,
                       m.password,
                       mr.role_name
                FROM manager m
                JOIN manager_role mr ON m.role_id = mr.role_id
                WHERE m.email = ?
                AND m.password = ?
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return toManager(rs);
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

    public boolean updateManagerRole(Connection conn, int managerId, int roleId) throws SQLException {
        String sql = """
                UPDATE manager
                SET role_id = ?
                WHERE manager_id = ?
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roleId);
            pstmt.setInt(2, managerId);

            return pstmt.executeUpdate() > 0;
        }
    }

    private Manager toManager(ResultSet rs) throws SQLException {
        return new Manager(
                rs.getInt("manager_id"),
                rs.getInt("role_id"),
                rs.getString("manager_name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("role_name")
        );
    }
}
