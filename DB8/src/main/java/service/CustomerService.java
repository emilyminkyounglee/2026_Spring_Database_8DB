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
    public void registerCustomer(int customerId,
                                 String firstName,
                                 String lastName,
                                 String email,
                                 String password,
                                 String phone,
                                 Date birthDate) {

        try (Connection conn = DBConnection.getConnection()) {

            customerDAO.registerCustomer(
                    conn,
                    customerId,
                    firstName,
                    lastName,
                    email,
                    password,
                    phone,
                    birthDate
            );

            System.out.println("Customer registered.");

        } catch (SQLException e) {
            e.printStackTrace();
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
                                      int newProfileId,
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
    public void writeReview(int reviewId,
                            int customerId,
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
                    reviewId,
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
