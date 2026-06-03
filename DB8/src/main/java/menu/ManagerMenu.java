package menu;

import model.Manager;
import service.AnalysisService;
import service.ProductService;
import service.ManagerService;
import util.DBConnection;
import util.InputUtil;
import util.MenuPrinter;
import dao.AnalysisDAO;
import dao.ProductDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;


public class ManagerMenu {
    private final ManagerService managerService = new ManagerService();
    private final ProductService productService = new ProductService();
    private final ProductDAO productDAO = new ProductDAO();
    private final AnalysisService analysisService = new AnalysisService();

    //TODO: Print manager menu
    public void run(Manager manager){
        while (true) {
            MenuPrinter.printManagerMenu(manager.getRoleName());
            //TODO: Get user menu input
            int choice = InputUtil.readInt("Select an option: ");

            //TODO: Return to main menu
            if (choice == 0) {
                System.out.println("Manager logged out.");
                return;
            }

            if (!isAllowed(manager.getRoleName(), choice)) {
                System.out.println("You do not have permission for this menu.");
                continue;
            }

            if (choice == 8 && "MASTER".equals(manager.getRoleName())) {
                manageManagerRoles();
                continue;
            }

            switch (choice) {
                case 1 -> searchBooks();
                case 2 -> updateProductPrice();
                case 3 -> analyzePriceChange();
                case 4 -> viewProductSalesSummary();
                case 5 -> viewSalesAnalysisSummary();
                case 6 -> viewInventoryStatus();
                case 7 -> addStock();
                //TODO: Handle invalid menu input
                default -> System.out.println("Invalid option.");
            }
        }
    }

    public void run(){
        System.out.println("Manager login is required.");
    }

    private boolean isAllowed(String roleName, int choice) {
        if ("MASTER".equals(roleName)) {
            return choice >= 1 && choice <= 8;
        }
        if ("SALES_ANALYSIS".equals(roleName)) {
            return choice >= 3 && choice <= 5;
        }
        if ("INVENTORY_MANAGER".equals(roleName)) {
            return choice == 1 || choice == 6 || choice == 7;
        }
        return false;
    }

    private void manageManagerRoles() {
        int managerId = InputUtil.readInt("Target manager ID: ");

        System.out.println("1. MASTER");
        System.out.println("2. SALES_ANALYSIS");
        System.out.println("3. INVENTORY_MANAGER");
        int roleId = InputUtil.readInt("New role ID: ");

        boolean updated = managerService.updateManagerRole(managerId, roleId);
        if (updated) {
            System.out.println("Manager role updated.");
        } else {
            System.out.println("Failed to update manager role.");
        }
    }

    //TODO: Call ProductDAO for book search and price updates
    private void searchBooks() {
        try (Connection conn = DBConnection.getConnection()) {
            String keyword = InputUtil.readString("Keyword (title): ");
            String category = InputUtil.readStringOrEmpty("Category (Enter to skip): ");
            var result = productDAO.searchBooks(conn, keyword, category);
            if (result.isEmpty()) {
                System.out.println("No books found.");
            } else {
                result.forEach(p -> System.out.printf(
                        "ID: %d | %s | %s | %s | %s | Stock: %d%n",
                        p.getProductId(), p.getProductName(), p.getAuthor(),
                        p.getPublisher(), p.getUnitPrice(), p.getStockQuantity()));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateProductPrice() {
        int productId = InputUtil.readInt("Product ID: ");
        String priceStr = InputUtil.readString("New price: ");
        BigDecimal newPrice = new BigDecimal(priceStr);

        try (Connection conn = DBConnection.getConnection()) {
            if (!productDAO.existsById(conn, productId)) {
                System.out.println("Product not found.");
                return;
            }

            //TODO: Use transaction for product price update
            conn.setAutoCommit(false);
            try {
                productDAO.closeCurrentPriceHistory(conn, productId);
                productDAO.insertPriceHistory(conn, productId, newPrice);
                productDAO.updateProductPrice(conn, productId, newPrice);
                conn.commit();
                System.out.println("Price updated successfully.");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Price update failed (rolled back): " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewInventoryStatus() {
        try (Connection conn = DBConnection.getConnection()) {
            var result = productDAO.searchBooks(conn, "", "");
            if (result.isEmpty()) {
                System.out.println("No products found.");
            } else {
                result.forEach(p -> System.out.printf(
                        "ID: %d | %-30s | Stock: %d | Price: %s%n",
                        p.getProductId(), p.getProductName(),
                        p.getStockQuantity(), p.getUnitPrice()));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addStock() {
        int productId = InputUtil.readInt("Product ID: ");
        int quantity = InputUtil.readInt("Quantity to add: ");

        try {
            productService.addStock(productId, quantity);
            System.out.println("Stock updated.");
        } catch (IllegalArgumentException | SQLException e) {
            System.out.println("Failed to update stock: " + e.getMessage());
        }
    }

    //TODO: Call AnalysisDAO for sales analysis quires
    private void analyzePriceChange() {
        int productId = InputUtil.readInt("Product ID: ");
        try (Connection conn = DBConnection.getConnection()) {
            analysisService.showSalesAroundPriceChange(conn, productId);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewProductSalesSummary() {
        try (Connection conn = DBConnection.getConnection()) {
            analysisService.showProductTotalSalesSummary(conn);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewSalesAnalysisSummary() {
        try (Connection conn = DBConnection.getConnection()) {
            analysisService.showPopularCategoriesByAgeGroup(conn);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}