package dao;
import java.sql.*;

public class CustomerDAO {
    //TODO: Register new customer
    public void registerCustomer(Connection conn,
                                 int customerId,
                                 String firstName,
                                 String lastName,
                                 String email,
                                 String phone,
                                 Date birthDate) throws SQLException {
        registerCustomer(conn, customerId, firstName, lastName, email, "password", phone, birthDate);
    }

    public void registerCustomer(Connection conn,
                                 int customerId,
                                 String firstName,
                                 String lastName,
                                 String email,
                                 String password,
                                 String phone,
                                 Date birthDate) throws SQLException {
        String sql = """
                INSERT INTO customer
                (customer_id, first_name, last_name, email, password, phone, birth_date, join_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE())
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, email);
            pstmt.setString(5, password);
            pstmt.setString(6, phone);
            pstmt.setDate(7, birthDate);

            pstmt.executeUpdate();
        }
    }

    public Integer findCustomerIdByEmailAndPassword(Connection conn,
                                                    String email,
                                                    String password) throws SQLException {
        String sql = """
                SELECT customer_id
                FROM customer
                WHERE email = ?
                AND password = ?
                """;

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

    public Integer findCustomerIdByEmail(Connection conn, String email) throws SQLException {
        String sql = """
                SELECT customer_id
                FROM customer
                WHERE email = ?
                """;

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

    //TODO: Find customer by customer_id
    public void findCustomerById(Connection conn, int customerId) throws SQLException {
        String sql = """
                SELECT customer_id, first_name, last_name, email, phone, birth_date, join_date
                FROM customer
                WHERE customer_id = ?
                """;

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

    //TODO: Find customer by email
    public void findCustomerByEmail(Connection conn, String email) throws SQLException {
        String sql = """
                SELECT customer_id, first_name, last_name, email, phone, birth_date, join_date
                FROM customer
                WHERE email = ?
                """;

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

    //TODO: Update customer profile
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

    //TODO: Close current active profile history
    public void closeCurrentProfileHistory(Connection conn,
                                           int customerId) throws SQLException {
        String sql = """
            UPDATE customer_profile_history
            SET end_date = CURRENT_TIMESTAMP
            WHERE customer_id = ?
            AND end_date IS NULL
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.executeUpdate();
        }
    }

    //TODO: Insert new customer profile history
    public void insertCustomerProfileHistory(Connection conn,
                                             int profileId,
                                             int customerId,
                                             String city,
                                             String membershipLevel) throws SQLException {
        String sql = """
            INSERT INTO customer_profile_history
            (profile_id, customer_id, city, membership_level, start_date, end_date)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, NULL)
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profileId);
            pstmt.setInt(2, customerId);
            pstmt.setString(3, city);
            pstmt.setString(4, membershipLevel);

            pstmt.executeUpdate();
        }
    }

    //TODO: View customer purchase history
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
                WHERE s.customer_id = ?
                ORDER BY s.sales_timestamp DESC
                """;

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

    //TODO: Analyze purchases before/after customer profile changes
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
