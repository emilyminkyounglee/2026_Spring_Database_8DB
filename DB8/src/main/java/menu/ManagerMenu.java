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
            MenuPrinter.printManagerMenu(manager.getRoleNames());
            //TODO: Get user menu input
            int choice = InputUtil.readInt("Select an option: ");

            //TODO: Return to main menu
            if (choice == 0) {
                System.out.println("Manager logged out.");
                return;
            }

            if (!isAllowed(manager, choice)) {
                System.out.println("You do not have permission for this menu.");
                continue;
            }

            if (choice == 8 && manager.hasRole("MASTER")) {
                manageManagerRoles();
                continue;
            }
            if (choice == 9 && manager.hasRole("MASTER")) {
                registerManager();
                continue;
            }
            if (choice == 10 && manager.hasRole("MASTER")) {
                dismissManager(manager.getManagerId());
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

    private boolean isAllowed(Manager manager, int choice) {
        if (manager.hasRole("MASTER")) {
            return choice >= 1 && choice <= 10;
        }
        if (manager.hasRole("SALES_ANALYSIS") && choice >= 3 && choice <= 5) {
            return true;
        }
        if (manager.hasRole("INVENTORY_MANAGER") && (choice == 1 || choice == 6 || choice == 7)) {
            return true;
        }
        if (manager.hasRole("PRICE_MANAGER") && (choice == 2 || choice == 3)) {
            return true;
        }
        if (manager.hasRole("ORDER_MANAGER") && (choice == 4 || choice == 5)) {
            return true;
        }
        if (manager.hasRole("CATEGORY_MANAGER") && choice == 1) {
            return true;
        }
        if (manager.hasRole("CUSTOMER_MANAGER") && choice == 5) {
            return true;
        }
        if (manager.hasRole("REVIEW_MANAGER") && choice == 1) {
            return true;
        }
        if (manager.hasRole("BASKET_MANAGER") && choice == 1) {
            return true;
        }
        return false;
    }

    private void registerManager() {
        int managerId = InputUtil.readInt("New manager ID: ");
        String managerName = InputUtil.readString("Manager name: ");
        String email = InputUtil.readString("Email: ");
        String password = InputUtil.readString("Password: ");

        printRoleOptions();
        int roleId = InputUtil.readInt("Initial role ID: ");

        boolean registered = managerService.registerManager(managerId, managerName, email, password, roleId);
        if (registered) {
            System.out.println("Manager registered.");
        } else {
            System.out.println("Failed to register manager.");
        }
    }

    private void dismissManager(int currentManagerId) {
        int managerId = InputUtil.readInt("Target manager ID to dismiss: ");
        if (managerId == currentManagerId) {
            System.out.println("You cannot dismiss your own manager account while logged in.");
            return;
        }

        Manager targetManager = managerService.findManagerById(managerId);
        if (targetManager == null) {
            System.out.println("Manager not found.");
            return;
        }

        System.out.println("Target manager: " + targetManager.getManagerName()
                + " (" + targetManager.getEmail() + ")");
        System.out.println("Current roles: " + targetManager.getRoleSummary());

        String confirm = InputUtil.readString("Dismiss this manager? (y/n): ");
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("Dismiss cancelled.");
            return;
        }

        boolean dismissed = managerService.dismissManager(managerId);
        if (dismissed) {
            System.out.println("Manager dismissed.");
        } else {
            System.out.println("Failed to dismiss manager.");
        }
    }

    private void manageManagerRoles() {
        int managerId = InputUtil.readInt("Target manager ID: ");
        Manager targetManager = managerService.findManagerById(managerId);
        if (targetManager == null) {
            System.out.println("Manager not found.");
            return;
        }

        System.out.println("Target manager: " + targetManager.getManagerName()
                + " (" + targetManager.getEmail() + ")");
        System.out.println("Current roles: " + targetManager.getRoleSummary());

        printRoleOptions();
        int roleId = InputUtil.readInt("New role ID: ");
        System.out.println("1. Assign role");
        System.out.println("2. Remove role");
        int action = InputUtil.readInt("Action: ");

        boolean updated;
        if (action == 1) {
            updated = managerService.assignManagerRole(managerId, roleId);
        } else if (action == 2) {
            updated = managerService.removeManagerRole(managerId, roleId);
        } else {
            System.out.println("Invalid action.");
            return;
        }

        if (updated) {
            System.out.println("Manager role assignment updated.");
        } else {
            System.out.println("Failed to update manager role assignment.");
        }
    }

    private void printRoleOptions() {
        System.out.println("1. MASTER");
        System.out.println("2. SALES_ANALYSIS");
        System.out.println("3. INVENTORY_MANAGER");
        System.out.println("4. CUSTOMER_MANAGER");
        System.out.println("5. REVIEW_MANAGER");
        System.out.println("6. PRICE_MANAGER");
        System.out.println("7. ORDER_MANAGER");
        System.out.println("8. BASKET_MANAGER");
        System.out.println("9. CATEGORY_MANAGER");
        System.out.println("10. SUPPORT_MANAGER");
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
