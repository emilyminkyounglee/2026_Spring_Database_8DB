package dao;
import java.sql.*;

public class CustomerDAO {
    // [REQ5][REQ10] Inserts a new customer using user-entered registration data.
    public void registerCustomer(Connection conn,
                                 String firstName,
                                 String lastName,
                                 String email,
                                 String phone,
                                 Date birthDate) throws SQLException {
        registerCustomer(conn, firstName, lastName, email, "password", phone, birthDate);
    }

    public void registerCustomer(Connection conn,
                                 String firstName,
                                 String lastName,
                                 String email,
                                 String password,
                                 String phone,
                                 Date birthDate) throws SQLException {
        String sql = """
                INSERT INTO customer
                (first_name, last_name, email, password, phone, birth_date, join_date)
                VALUES (?, ?, ?, ?, ?, ?, CURDATE())
                """;

        // [REQ10] PreparedStatement binds user input instead of concatenating SQL strings.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setString(5, phone);
            pstmt.setDate(6, birthDate);

            pstmt.executeUpdate();
        }
    }

    // [REQ10] Validates customer email and password with PreparedStatement parameters.
    public Integer findCustomerIdByEmailAndPassword(Connection conn,
                                                    String email,
                                                    String password) throws SQLException {
        String sql = """
                SELECT customer_id
                FROM customer
                WHERE email = ?
                AND password = ?
                """;

        // [REQ10] Email and password are passed as bind variables.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("customer_id");
                }
                return null;
            }
        }
    }

    // [REQ10] Checks if a customer email exists using a bind variable.
    public Integer findCustomerIdByEmail(Connection conn, String email) throws SQLException {
        String sql = """
                SELECT customer_id
                FROM customer
                WHERE email = ?
                """;

        // [REQ10] The email input is safely bound to the query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("customer_id");
                }
                return null;
            }
        }
    }

    // [REQ10] Finds a customer by id using PreparedStatement.
    public void findCustomerById(Connection conn, int customerId) throws SQLException {
        String sql = """
                SELECT customer_id, first_name, last_name, email, phone, birth_date, join_date
                FROM customer
                WHERE customer_id = ?
                """;

        // [REQ10] The customer id is safely bound to the SELECT query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    printCustomer(rs);
                } else {
                    System.out.println("Customer not found.");
                }
            }
        }
    }

    // [REQ14] Displays customer demographic/profile history used for demographic sales analysis.
    public void viewCustomerProfile(Connection conn, int customerId) throws SQLException {
        String customerSql = """
                SELECT customer_id, first_name, last_name, email, phone, birth_date, join_date
                FROM customer
                WHERE customer_id = ?
                """;

        // [REQ10] The customer id is safely bound when retrieving basic customer data.
        try (PreparedStatement pstmt = conn.prepareStatement(customerSql)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println();
                    System.out.println("===== My Customer Information =====");
                    printCustomer(rs);
                } else {
                    System.out.println("Customer not found.");
                    return;
                }
            }
        }

        String profileSql = """
                SELECT profile_id, city, membership_level, start_date, end_date
                FROM customer_profile_history
                WHERE customer_id = ?
                ORDER BY start_date
                """;

        // [REQ10][REQ14] The same customer id is bound to retrieve profile history rows.
        try (PreparedStatement pstmt = conn.prepareStatement(profileSql)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println();
                System.out.println("===== Membership / Profile History =====");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println(
                            "Profile ID: " + rs.getInt("profile_id") +
                                    " | City: " + rs.getString("city") +
                                    " | Membership: " + rs.getString("membership_level") +
                                    " | Start: " + rs.getTimestamp("start_date") +
                                    " | End: " + rs.getTimestamp("end_date")
                    );
                }
                if (!found) {
                    System.out.println("No profile history found.");
                }
            }
        }
    }

    // [REQ10] Finds customer information by email using PreparedStatement.
    public void findCustomerByEmail(Connection conn, String email) throws SQLException {
        String sql = """
                SELECT customer_id, first_name, last_name, email, phone, birth_date, join_date
                FROM customer
                WHERE email = ?
                """;

        // [REQ10] The email input is safely bound to the query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    printCustomer(rs);
                } else {
                    System.out.println("Customer not found.");
                }
            }
        }
    }

    // [REQ8][REQ10] Updates customer profile fields using user input.
    public void updateCustomerProfile(Connection conn,
                                      int customerId,
                                      String firstName,
                                      String lastName,
                                      String email,
                                      String phone,
                                      Date birthDate) throws SQLException {
        String sql = """
                UPDATE customer
                SET first_name = ?,
                    last_name = ?,
                    email = ?,
                    phone = ?,
                    birth_date = ?
                WHERE customer_id = ?
                """;

        // [REQ10] Every updated field is passed through PreparedStatement bind variables.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setDate(5, birthDate);
            pstmt.setInt(6, customerId);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Customer profile updated.");
            } else {
                System.out.println("Customer not found.");
            }
        }
    }

    // [REQ8][REQ14] Closes the active customer profile history row before inserting a new one.
    public void closeCurrentProfileHistory(Connection conn,
                                           int customerId) throws SQLException {
        String sql = """
            UPDATE customer_profile_history
            SET end_date = CURRENT_TIMESTAMP
            WHERE customer_id = ?
            AND end_date IS NULL
            """;

        // [REQ10] The customer id is bound when updating profile history.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.executeUpdate();
        }
    }

    // [REQ5][REQ14] Inserts a new profile history row for city and membership changes.
    public void insertCustomerProfileHistory(Connection conn,
                                             int customerId,
                                             String city,
                                             String membershipLevel) throws SQLException {
        String sql = """
            INSERT INTO customer_profile_history
            (customer_id, city, membership_level, start_date, end_date)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP, NULL)
            """;

        // [REQ10] Profile history values are safely bound to the INSERT statement.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setString(2, city);
            pstmt.setString(3, membershipLevel);

            pstmt.executeUpdate();
        }
    }

    // [REQ6][REQ10] Uses user input, a view, and a join to show customer purchase history.
    public void viewCustomerPurchaseHistory(Connection conn, int customerId) throws SQLException {
        String sql = """
                SELECT s.sales_id,
                       s.sales_timestamp,
                       s.product_name,
                       s.quantity,
                       s.unit_price_at_sale,
                       s.subtotal,
                       s.total_amount
                FROM v_customer_purchase_history s
                JOIN product p ON s.product_id = p.product_id
                WHERE s.customer_id = ?
                ORDER BY s.sales_timestamp DESC
                """;

        // [REQ10] The logged-in customer id is bound to the view-based SELECT query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    System.out.println(
                            "Sales ID: " + rs.getInt("sales_id") +
                                    " | Date: " + rs.getTimestamp("sales_timestamp") +
                                    " | Product: " + rs.getString("product_name") +
                                    " | Quantity: " + rs.getInt("quantity") +
                                    " | Unit Price: " + rs.getBigDecimal("unit_price_at_sale") +
                                    " | Subtotal: " + rs.getBigDecimal("subtotal") +
                                    " | Total: " + rs.getBigDecimal("total_amount")
                    );
                }

                if (!found) {
                    System.out.println("No purchase history found.");
                }
            }
        }
    }

    // [REQ7][REQ10][REQ14] Aggregates sales before and after a customer profile change date.
    public void analyzePurchasesBeforeAfterCustomerProfileChanges(Connection conn,
                                                                  int customerId,
                                                                  Date changeDate) throws SQLException {
        String sql = """
                SELECT
                    CASE
                        WHEN DATE(s.sales_timestamp) < ? THEN 'BEFORE'
                        ELSE 'AFTER'
                    END AS period,
                    COUNT(DISTINCT s.sales_id) AS sales_count,
                    SUM(sd.quantity) AS total_quantity,
                    SUM(sd.subtotal) AS total_spent
                FROM sales s
                JOIN sales_detail sd ON s.sales_id = sd.sales_id
                WHERE s.customer_id = ?
                GROUP BY period
                ORDER BY period
                """;

        // [REQ10] The change date and customer id are bound to the aggregation query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, changeDate);
            pstmt.setInt(2, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean found = false;

                while (rs.next()) {
                    found = true;

                    System.out.println(
                            rs.getString("period") +
                                    " | Sales Count: " + rs.getInt("sales_count") +
                                    " | Total Quantity: " + rs.getInt("total_quantity") +
                                    " | Total Spent: " + rs.getBigDecimal("total_spent")
                    );
                }

                if (!found) {
                    System.out.println("No sales data found.");
                }
            }
        }
    }

    // [REQ15] Prints customer rows in the text-based interface.
    private void printCustomer(ResultSet rs) throws SQLException {
        System.out.println(
                rs.getInt("customer_id") + " | " +
                        rs.getString("first_name") + " | " +
                        rs.getString("last_name") + " | " +
                        rs.getString("email") + " | " +
                        rs.getString("phone") + " | " +
                        rs.getDate("birth_date") + " | " +
                        rs.getDate("join_date")
        );
    }
}
