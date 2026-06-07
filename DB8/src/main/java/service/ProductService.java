package service;

import dao.ProductDAO;
import model.Product;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductService {
    // [REQ17] Service layer wraps product DAO methods and product-related transactions.
    private final ProductDAO productDAO = new ProductDAO();

    // [REQ6][REQ10] Searches products through the DAO using user-supplied keyword/category values.
    public List<Product> searchBooks(String keyword, String category) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return productDAO.searchBooks(conn, keyword, category);
        }
    }

    // [REQ5][REQ10] Inserts a new book using manager-entered product information.
    public void insertBook(int productId, int categoryId, String productName,
                           String author, String publisher,
                           BigDecimal unitPrice, int stockQuantity) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            productDAO.insertBook(conn, productId, categoryId, productName, author, publisher, unitPrice, stockQuantity);
        }
    }

    // [REQ8][REQ12][REQ13] Updates current price and product_price_history as one transaction.
    public void updatePrice(int productId, BigDecimal newPrice) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {
                if (!productDAO.existsById(conn, productId)) {
                    throw new SQLException("Product does not exist: " + productId);
                }

                productDAO.closeCurrentPriceHistory(conn, productId);
                productDAO.insertPriceHistory(conn, productId, newPrice);

                if (!productDAO.updateProductPrice(conn, productId, newPrice)) {
                    throw new SQLException("Failed to update product price: " + productId);
                }

                conn.commit();
                System.out.println("Product price updated.");
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        }
    }

    // [REQ8][REQ10] Adds stock quantity for an existing product.
    public void addStock(int productId, int quantity) throws SQLException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (!productDAO.addStock(conn, productId, quantity)) {
                throw new SQLException("Product does not exist: " + productId);
            }
        }
    }

    // [REQ7][REQ13] Shows aggregated sales by product price history period.
    public void analyzePriceChangeSales(int productId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            productDAO.analyzePriceChangeSales(conn, productId);
        }
    }
}
