package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SalesDAO {
    public List<PurchaseItem> findPurchaseItems(Connection conn, int customerId) throws SQLException {
        String sql = """
                SELECT b.product_id, b.quantity, p.unit_price, p.stock_quantity
                FROM market_basket b
                JOIN product p ON b.product_id = p.product_id
                WHERE b.customer_id = ?
                ORDER BY b.basket_id
                """;
        List<PurchaseItem> items = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new PurchaseItem(
                            rs.getInt("product_id"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("unit_price"),
                            rs.getInt("stock_quantity")
                    ));
                }
            }
        }
        return items;
    }

    public int insertSale(Connection conn, int customerId, BigDecimal totalAmount) throws SQLException {
        int profileId = findCurrentProfileId(conn, customerId);
        int ageAtSale = findAgeAtSale(conn, customerId);

        String sql = """
                INSERT INTO sales
                    (customer_id, sales_timestamp, total_amount, profile_id, age_at_sale)
                VALUES (?, CURRENT_TIMESTAMP, ?, ?, ?)
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, customerId);
            pstmt.setBigDecimal(2, totalAmount);
            pstmt.setInt(3, profileId);
            pstmt.setInt(4, ageAtSale);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create sale.");
    }

    public void insertSalesDetails(Connection conn, int salesId, List<PurchaseItem> items) throws SQLException {
        String sql = """
                INSERT INTO sales_detail
                    (sales_id, product_id, quantity, unit_price_at_sale, subtotal)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (PurchaseItem item : items) {
                pstmt.setInt(1, salesId);
                pstmt.setInt(2, item.productId());
                pstmt.setInt(3, item.quantity());
                pstmt.setBigDecimal(4, item.unitPriceAtSale());
                pstmt.setBigDecimal(5, item.subtotal());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public void upsertTotalSales(Connection conn, List<PurchaseItem> items) throws SQLException {
        String sql = """
                INSERT INTO total_sales
                    (product_id, total_quantity, total_revenue)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    total_quantity = total_quantity + VALUES(total_quantity),
                    total_revenue = total_revenue + VALUES(total_revenue)
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (PurchaseItem item : items) {
                pstmt.setInt(1, item.productId());
                pstmt.setInt(2, item.quantity());
                pstmt.setBigDecimal(3, item.subtotal());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public void decreaseStock(Connection conn, List<PurchaseItem> items) throws SQLException {
        String sql = """
                UPDATE product
                SET stock_quantity = stock_quantity - ?
                WHERE product_id = ? AND stock_quantity >= ?
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (PurchaseItem item : items) {
                pstmt.setInt(1, item.quantity());
                pstmt.setInt(2, item.productId());
                pstmt.setInt(3, item.quantity());
                pstmt.addBatch();
            }
            int[] counts = pstmt.executeBatch();
            for (int count : counts) {
                if (count != 1) {
                    throw new SQLException("Failed to decrease stock. Not enough stock may remain.");
                }
            }
        }
    }

    public BigDecimal calculateTotalAmount(List<PurchaseItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseItem item : items) {
            total = total.add(item.subtotal());
        }
        return total;
    }

    public void validatePurchaseItems(List<PurchaseItem> items) throws SQLException {
        if (items.isEmpty()) {
            throw new SQLException("Market basket is empty.");
        }
        for (PurchaseItem item : items) {
            if (item.quantity() <= 0) {
                throw new SQLException("Invalid quantity for product: " + item.productId());
            }
            if (item.stockQuantity() < item.quantity()) {
                throw new SQLException("Not enough stock for product: " + item.productId());
            }
        }
    }

    private int findCurrentProfileId(Connection conn, int customerId) throws SQLException {
        String sql = """
                SELECT profile_id
                FROM customer_profile_history
                WHERE customer_id = ? AND end_date IS NULL
                ORDER BY start_date DESC
                LIMIT 1
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("profile_id");
                }
            }
        }
        throw new SQLException("Current customer profile does not exist: " + customerId);
    }

    private int findAgeAtSale(Connection conn, int customerId) throws SQLException {
        String sql = """
                SELECT TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) AS age_at_sale
                FROM customer
                WHERE customer_id = ?
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("age_at_sale");
                }
            }
        }
        throw new SQLException("Customer does not exist: " + customerId);
    }

    public record PurchaseItem(int productId, int quantity, BigDecimal unitPriceAtSale, int stockQuantity) {
        public BigDecimal subtotal() {
            return unitPriceAtSale.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
