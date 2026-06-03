package service;

import dao.AnalysisDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AnalysisService {
    //TODO: Handle sales and customer analysis logic
    private final AnalysisDAO analysisDAO = new AnalysisDAO();

    public void showMyPurchaseHistory(Connection conn, int customerId) {
        System.out.println("\n===== My Purchase History =====");
        try {
            List<Map<String, Object>> result = analysisDAO.getCustomerPurchaseHistory(conn, customerId);
            analysisDAO.printResultTable(result);
        } catch (SQLException e) {
            System.out.println("Error: Failed to load purchase history: " + e.getMessage());
        }
    }

    public void showPurchasesAroundProfileChange(Connection conn, int customerId) {
        System.out.println("\n===== Purchases Before/After Profile Changes =====");
        try {
            List<Map<String, Object>> result = analysisDAO.analyzePurchasesAroundProfileChange(conn, customerId);
            if (result.isEmpty()) {
                System.out.println("No profile change history or purchase records found.");
                return;
            }
            analysisDAO.printResultTable(result);
        } catch (SQLException e) {
            System.out.println("Error: Failed to load profile change analysis: " + e.getMessage());
        }
    }

    public void showPopularCategoriesByAgeGroup(Connection conn) {
        System.out.println("\n===== Popular Categories by Age Group =====");
        try {
            List<Map<String, Object>> result = analysisDAO.getPopularCategoriesByAgeGroup(conn);
            analysisDAO.printResultTable(result);
        } catch (SQLException e) {
            System.out.println("Error: Failed to load category analysis: " + e.getMessage());
        }
    }

    public void showSalesAroundPriceChange(Connection conn, int productId) {
        System.out.println("\n===== Sales Before/After Price Change (product_id: " + productId + ") =====");
        try {
            List<Map<String, Object>> result = analysisDAO.analyzeSalesAroundPriceChange(conn, productId);
            if (result.isEmpty()) {
                System.out.println("No price change history found for this product.");
                return;
            }
            analysisDAO.printResultTable(result);
        } catch (SQLException e) {
            System.out.println("Error: Failed to load price change analysis: " + e.getMessage());
        }
    }

    public void showProductTotalSalesSummary(Connection conn) {
        System.out.println("\n===== Product Total Sales Summary =====");
        try {
            List<Map<String, Object>> result = analysisDAO.getProductTotalSalesSummary(conn);
            analysisDAO.printResultTable(result);
        } catch (SQLException e) {
            System.out.println("Error: Failed to load sales summary: " + e.getMessage());
        }
    }
}