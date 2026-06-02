package service;

import dao.ManagerDAO;
import model.Manager;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class ManagerService {
    private final ManagerDAO managerDAO = new ManagerDAO();

    public Manager login(String email, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            return managerDAO.findByEmailAndPassword(conn, email, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer findManagerIdByEmail(String email) {
        try (Connection conn = DBConnection.getConnection()) {
            return managerDAO.findManagerIdByEmail(conn, email);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateManagerRole(int managerId, int roleId) {
        try (Connection conn = DBConnection.getConnection()) {
            if (!managerDAO.existsRole(conn, roleId)) {
                System.out.println("Role does not exist.");
                return false;
            }

            return managerDAO.updateManagerRole(conn, managerId, roleId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
