package dao;

import model.Basket;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BasketDAO {
    public void addBook(Connection conn, int customerId, int productId, int quantity) throws SQLException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        BigDecimal currentPrice = findCurrentProductPrice(conn, productId);
        if (currentPrice == null) {
            throw new SQLException("Product does not exist: " + productId);
        }

        int existingBasketId = findBasketId(conn, customerId, productId);
        if (existingBasketId > 0) {
            String sql = """
                    UPDATE market_basket
                    SET quantity = quantity + ?, added_at = CURRENT_TIMESTAMP, unit_price_at_added = ?
                    WHERE basket_id = ?
                    """;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, quantity);
                pstmt.setBigDecimal(2, currentPrice);
                pstmt.setInt(3, existingBasketId);
                pstmt.executeUpdate();
            }
            return;
        }

        String sql = """
                INSERT INTO market_basket
                    (customer_id, product_id, quantity, added_at, unit_price_at_added)
                VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, quantity);
            pstmt.setBigDecimal(4, currentPrice);
            pstmt.executeUpdate();
        }
    }

    public boolean removeBook(Connection conn, int customerId, int productId) throws SQLException {
        String sql = "DELETE FROM market_basket WHERE customer_id = ? AND product_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, productId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Basket> findByCustomer(Connection conn, int customerId) throws SQLException {
        String sql = """
                SELECT basket_id, customer_id, product_id, quantity, added_at, unit_price_at_added
                FROM market_basket
                WHERE customer_id = ?
                ORDER BY added_at, basket_id
                """;
        List<Basket> baskets = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp addedAt = rs.getTimestamp("added_at");
                    baskets.add(new Basket(
                            rs.getInt("basket_id"),
                            rs.getInt("customer_id"),
                            rs.getInt("product_id"),
                            rs.getInt("quantity"),
                            addedAt == null ? null : addedAt.toLocalDateTime(),
                            rs.getBigDecimal("unit_price_at_added")
                    ));
                }
            }
        }
        return baskets;
    }

    public void clearBasket(Connection conn, int customerId) throws SQLException {
        String sql = "DELETE FROM market_basket WHERE customer_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.executeUpdate();
        }
    }

    private int findBasketId(Connection conn, int customerId, int productId) throws SQLException {
        String sql = """
                SELECT basket_id
                FROM market_basket
                WHERE customer_id = ? AND product_id = ?
                LIMIT 1
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt("basket_id") : -1;
            }
        }
    }

    private BigDecimal findCurrentProductPrice(Connection conn, int productId) throws SQLException {
        String sql = "SELECT unit_price FROM product WHERE product_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getBigDecimal("unit_price") : null;
            }
        }
    }

}
