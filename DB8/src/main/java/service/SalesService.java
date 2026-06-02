package service;

import dao.BasketDAO;
import dao.SalesDAO;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SalesService {
    private final BasketDAO basketDAO = new BasketDAO();
    private final SalesDAO salesDAO = new SalesDAO();

    public void addBookToBasket(int customerId, int productId, int quantity) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            basketDAO.addBook(conn, customerId, productId, quantity);
        }
    }

    public boolean removeBookFromBasket(int customerId, int productId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return basketDAO.removeBook(conn, customerId, productId);
        }
    }

    public int purchaseBasket(int customerId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                List<SalesDAO.PurchaseItem> items = salesDAO.findPurchaseItems(conn, customerId);
                salesDAO.validatePurchaseItems(items);

                BigDecimal totalAmount = salesDAO.calculateTotalAmount(items);
                int salesId = salesDAO.insertSale(conn, customerId, totalAmount);
                salesDAO.insertSalesDetails(conn, salesId, items);
                salesDAO.upsertTotalSales(conn, items);
                salesDAO.decreaseStock(conn, items);
                basketDAO.clearBasket(conn, customerId);

                conn.commit();
                return salesId;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        }
    }
}
