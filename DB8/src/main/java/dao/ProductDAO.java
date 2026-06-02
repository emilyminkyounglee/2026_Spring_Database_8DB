package dao;

import model.Product;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // 카테고리 또는 키워드로 책 검색
    // TODO: v_product_catalog 뷰 생성 후 아래 sql로 교체
    /* String sql = """
            SELECT * FROM v_product_catalog
            WHERE product_name LIKE ? OR category_name LIKE ?
            """;
    */
    public List<Product> searchBooks(Connection conn, String keyword, String category) throws SQLException {
        String sql = """
                SELECT p.*, bc.category_name
                FROM product p
                JOIN book_category bc ON p.category_id = bc.category_id
                WHERE p.product_name LIKE ? OR bc.category_name LIKE ?
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

    // 가격 변경 (트랜잭션)
    public void updatePrice(Connection conn, int productId, BigDecimal newPrice) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // 1. 기존 가격 이력 닫기
            String closeOld = """
                    UPDATE product_price_history
                    SET end_date = NOW()
                    WHERE product_id = ? AND end_date IS NULL
                    """;
            try (PreparedStatement ps = conn.prepareStatement(closeOld)) {
                ps.setInt(1, productId);
                ps.executeUpdate();
            }

            // 2. 새 가격 이력 추가
            String insertNew = """
                    INSERT INTO product_price_history
                        (price_history_id, product_id, unit_price, start_date, end_date)
                    VALUES (?, ?, ?, NOW(), NULL)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(insertNew)) {
                ps.setInt(1, nextPriceHistoryId(conn));
                ps.setInt(2, productId);
                ps.setBigDecimal(3, newPrice);
                ps.executeUpdate();
            }

            // 3. product 테이블 현재 가격 업데이트
            String updateProduct = """
                    UPDATE product
                    SET unit_price = ?
                    WHERE product_id = ?
                    """;
            try (PreparedStatement ps = conn.prepareStatement(updateProduct)) {
                ps.setBigDecimal(1, newPrice);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("가격이 성공적으로 변경되었습니다.");

        } catch (SQLException e) {
            conn.rollback();
            System.out.println("가격 변경 실패! 롤백: " + e.getMessage());
            throw e;
        } finally {
            conn.setAutoCommit(true);
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
    public void insertBook(Connection conn, int categoryId, String productName,
                           String author, String publisher,
                           BigDecimal unitPrice, int stockQuantity) throws SQLException {
        String sql = """
                INSERT INTO product
                    (category_id, product_name, author, publisher, unit_price, stock_quantity)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setString(2, productName);
            ps.setString(3, author);
            ps.setString(4, publisher);
            ps.setBigDecimal(5, unitPrice);
            ps.setInt(6, stockQuantity);
            ps.executeUpdate();
            System.out.println("책이 추가되었습니다.");
        }
    }

    // 다음 price_history_id 생성
    private int nextPriceHistoryId(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(price_history_id), 0) + 1 AS next_id FROM product_price_history";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt("next_id");
        }
    }
}