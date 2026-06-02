package dao;

import model.Product;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> searchBooks(Connection conn, String keyword, String category) throws SQLException {
        String sql = """
                SELECT product_id,
                       category_id,
                       product_name,
                       author,
                       publisher,
                       unit_price,
                       stock_quantity
                FROM v_product_catalog
                WHERE product_name LIKE ?
                OR category_name LIKE ?
                """;
        List<Product> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + category + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setProductId(rs.getInt("product_id"));
                    p.setProductName(rs.getString("product_name"));
                    p.setAuthor(rs.getString("author"));
                    p.setPublisher(rs.getString("publisher"));
                    p.setUnitPrice(rs.getBigDecimal("unit_price"));
                    p.setStockQuantity(rs.getInt("stock_quantity"));
                    result.add(p);
                }
            }
        }
        return result;
    }

    public boolean existsById(Connection conn, int productId) throws SQLException {
        String sql = "SELECT 1 FROM product WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void closeCurrentPriceHistory(Connection conn, int productId) throws SQLException {
        String sql = """
                UPDATE product_price_history
                SET end_date = NOW()
                WHERE product_id = ?
                AND end_date IS NULL
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    public void insertPriceHistory(Connection conn, int productId, BigDecimal newPrice) throws SQLException {
        String sql = """
                INSERT INTO product_price_history
                    (product_id, unit_price, start_date, end_date)
                VALUES (?, ?, NOW(), NULL)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setBigDecimal(2, newPrice);
            ps.executeUpdate();
        }
    }

    public boolean updateProductPrice(Connection conn, int productId, BigDecimal newPrice) throws SQLException {
        String sql = """
                UPDATE product
                SET unit_price = ?
                WHERE product_id = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newPrice);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean addStock(Connection conn, int productId, int quantity) throws SQLException {
        String sql = """
                UPDATE product
                SET stock_quantity = stock_quantity + ?
                WHERE product_id = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        }
    }

    // 가격 변경 전후 매출 분석
    public void analyzePriceChangeSales(Connection conn, int productId) throws SQLException {
        String sql = """
                SELECT
                    pph.unit_price AS price,
                    pph.start_date,
                    pph.end_date,
                    COUNT(sd.sales_detail_id) AS order_count,
                    COALESCE(SUM(sd.quantity), 0) AS total_quantity,
                    COALESCE(SUM(sd.subtotal), 0) AS total_revenue
                FROM product_price_history pph
                LEFT JOIN sales_detail sd
                    ON sd.product_id = pph.product_id
                    AND sd.unit_price_at_sale = pph.unit_price
                WHERE pph.product_id = ?
                GROUP BY pph.price_history_id, pph.unit_price, pph.start_date, pph.end_date
                ORDER BY pph.start_date
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n===== 가격 변경 전후 매출 분석 =====");
                System.out.printf("%-12s %-22s %-22s %-10s %-10s %-12s%n",
                        "가격", "시작일", "종료일", "주문건수", "판매수량", "총매출");
                System.out.println("-".repeat(90));
                while (rs.next()) {
                    System.out.printf("%-12s %-22s %-22s %-10d %-10d %-12s%n",
                            rs.getBigDecimal("price"),
                            rs.getString("start_date"),
                            rs.getString("end_date") == null ? "현재" : rs.getString("end_date"),
                            rs.getInt("order_count"),
                            rs.getInt("total_quantity"),
                            rs.getBigDecimal("total_revenue"));
                }
            }
        }
    }

    // 새 책 추가
    public void insertBook(Connection conn, int productId, int categoryId, String productName,
                           String author, String publisher,
                           BigDecimal unitPrice, int stockQuantity) throws SQLException {
        String sql = """
                INSERT INTO product
                    (product_id, category_id, product_name, author, publisher, unit_price, stock_quantity)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, categoryId);
            ps.setString(3, productName);
            ps.setString(4, author);
            ps.setString(5, publisher);
            ps.setBigDecimal(6, unitPrice);
            ps.setInt(7, stockQuantity);
            ps.executeUpdate();
            System.out.println("책이 추가되었습니다.");
        }
    }

}
