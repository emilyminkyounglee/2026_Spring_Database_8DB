package service;

import dao.CustomerDAO;
import dao.ReviewDAO;
import util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

public class CustomerService {
    //TODO: Handel customer-realted business logic
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    // Register customer
    public void registerCustomer(String firstName,
                                 String lastName,
                                 String email,
                                 String password,
                                 String phone,
                                 Date birthDate,
                                 String city,
                                 String membershipLevel) {

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                customerDAO.registerCustomer(
                        conn,
                        firstName,
                        lastName,
                        email,
                        password,
                        phone,
                        birthDate
                );

                Integer customerId = customerDAO.findCustomerIdByEmail(conn, email);
                if (customerId == null) {
                    throw new SQLException("Failed to find registered customer.");
                }
                int newProfileId = customerDAO.getNextProfileId(conn);
                customerDAO.insertCustomerProfileHistory(
                        conn,
                        newProfileId,
                        customerId,
                        city,
                        membershipLevel
                );

                conn.commit();
                System.out.println("Customer registered.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean updateCustomerProfileHistory(int customerId,
                                                String city,
                                                String membershipLevel) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                customerDAO.closeCurrentProfileHistory(conn, customerId);

                int newProfileId = customerDAO.getNextProfileId(conn);

                customerDAO.insertCustomerProfileHistory(
                        conn,
                        newProfileId,
                        customerId,
                        city,
                        membershipLevel
                );
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
    public Integer login(String email, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            return customerDAO.findCustomerIdByEmailAndPassword(conn, email, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer findCustomerIdByEmail(String email) {
        try (Connection conn = DBConnection.getConnection()) {
            return customerDAO.findCustomerIdByEmail(conn, email);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Update customer profile (transaction)
    public void updateCustomerProfile(int customerId,
                                      String firstName,
                                      String lastName,
                                      String email,
                                      String phone,
                                      Date birthDate,
                                      String city,
                                      String membershipLevel) {

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {
                customerDAO.updateCustomerProfile(
                        conn,
                        customerId,
                        firstName,
                        lastName,
                        email,
                        phone,
                        birthDate
                );

                customerDAO.closeCurrentProfileHistory(conn, customerId);

                int newProfileId = customerDAO.getNextProfileId(conn);

                customerDAO.insertCustomerProfileHistory(
                        conn,
                        newProfileId,
                        customerId,
                        city,
                        membershipLevel
                );
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Write review
    public void writeReview(int customerId,
                            int productId,
                            int rating,
                            String reviewText) {

        try (Connection conn = DBConnection.getConnection()) {
            boolean purchased =
                    reviewDAO.checkPurchaseBeforeReview(
                            conn,
                            customerId,
                            productId
                    );
            if (!purchased) {
                System.out.println("Customer did not purchase this book.");
                return;
            }
            reviewDAO.writeReview(
                    conn,
                    customerId,
                    productId,
                    rating,
                    reviewText
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete review
    public void deleteReview(int reviewId,
                             int customerId) {
        try (Connection conn = DBConnection.getConnection()) {

            reviewDAO.deleteReview(
                    conn,
                    reviewId,
                    customerId
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
