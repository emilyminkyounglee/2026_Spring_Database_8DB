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

    public Manager findManagerById(int managerId) {
        try (Connection conn = DBConnection.getConnection()) {
            return managerDAO.findById(conn, managerId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean registerManager(int managerId, String managerName, String email,
                                   String password, int roleId) {
        try (Connection conn = DBConnection.getConnection()) {
            if (!managerDAO.existsRole(conn, roleId)) {
                System.out.println("Role does not exist.");
                return false;
            }

            conn.setAutoCommit(false);
            try {
                managerDAO.insertManager(conn, managerId, managerName, email, password);
                managerDAO.assignManagerRole(conn, managerId, roleId);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean dismissManager(int managerId) {
        try (Connection conn = DBConnection.getConnection()) {
            if (managerDAO.findById(conn, managerId) == null) {
                System.out.println("Manager does not exist.");
                return false;
            }

            conn.setAutoCommit(false);
            try {
                managerDAO.removeAllManagerRoles(conn, managerId);
                boolean deleted = managerDAO.deleteManager(conn, managerId);
                conn.commit();
                return deleted;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignManagerRole(int managerId, int roleId) {
        try (Connection conn = DBConnection.getConnection()) {
            if (!managerDAO.existsRole(conn, roleId)) {
                System.out.println("Role does not exist.");
                return false;
            }

            return managerDAO.assignManagerRole(conn, managerId, roleId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeManagerRole(int managerId, int roleId) {
        try (Connection conn = DBConnection.getConnection()) {
            if (!managerDAO.existsRole(conn, roleId)) {
                System.out.println("Role does not exist.");
                return false;
            }

            return managerDAO.removeManagerRole(conn, managerId, roleId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
