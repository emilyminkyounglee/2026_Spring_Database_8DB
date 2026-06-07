package menu;

import dao.*;
import model.Basket;
import service.AnalysisService;
import service.CustomerService;
import util.DBConnection;
import util.InputUtil;
import util.MenuPrinter;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class CustomerMenu {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final BasketDAO basketDAO = new BasketDAO();
    private final SalesDAO salesDAO = new SalesDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final AnalysisService analysisService = new AnalysisService();
    private final CustomerService customerService = new CustomerService();

    private int currentCustomerId;

    // [REQ15] Runs the text-based customer menu after a successful customer login.
    public void run(int customerId) {
        this.currentCustomerId = customerId;
        while (true) {
            MenuPrinter.printCustomerMenu();
            // [REQ15] Reads the user's menu selection from the console.
            int choice = InputUtil.readInt("Select an option: ");

            switch (choice) {
                case 0 -> {
                    System.out.println("Logged out.");
                    return;
                }
                case 1 -> searchBooks();
                case 2 -> addToBasket();
                case 3 -> removeFromBasket();
                case 4 -> purchase();
                case 5 -> viewPurchaseHistory();
                case 6 -> viewProfileChangeAnalysis();
                case 7 -> viewPopularCategories();
                case 8 -> viewMyProfile();
                case 9 -> updateProfile();
                case 10 -> writeReview();
                case 11 -> deleteReview();
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // [REQ6][REQ10] Uses user input to search books through a DAO query using a view and join.
    private void searchBooks() {
        try (Connection conn = DBConnection.getConnection()) {
            String keyword = InputUtil.readStringOrEmpty("Keyword (title/author, Enter to skip): ");
            String category = InputUtil.readStringOrEmpty("Category (Enter to skip): ");
            var result = productDAO.searchBooks(conn, keyword, category);
            if (result.isEmpty()) {
                System.out.println("No books found.");
                return;
            }

            result.forEach(p -> System.out.printf(
                    "ID: %d | %s | %s | %s | %s | Stock: %d%n",
                    p.getProductId(), p.getProductName(), p.getAuthor(),
                    p.getPublisher(), p.getUnitPrice(), p.getStockQuantity()));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // [REQ14] Shows the logged-in customer's current information and profile history.
    private void viewMyProfile() {
        try (Connection conn = DBConnection.getConnection()) {
            customerDAO.viewCustomerProfile(conn, currentCustomerId);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // [REQ8][REQ12][REQ14] Updates customer profile history through the service transaction.
    private void updateProfile() {
        String city = InputUtil.readString("New city: ");
        String membershipLevel = InputUtil.readString("New membership level: ");

        boolean updated = customerService.updateCustomerProfileHistory(
                currentCustomerId,
                city,
                membershipLevel
        );

        if (updated) {
            System.out.println("Customer profile history updated.");
        } else {
            System.out.println("Failed to update customer profile history.");
        }
    }

    // [REQ5][REQ10] Inserts or updates a basket item using customer text input.
    private void addToBasket() {
        try (Connection conn = DBConnection.getConnection()) {
            int productId = InputUtil.readInt("Product ID: ");
            int quantity = InputUtil.readInt("Quantity: ");
            basketDAO.addBook(conn, currentCustomerId, productId, quantity);
            System.out.println("Book added to basket.");
        } catch (IllegalArgumentException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // [REQ9][REQ10] Deletes an item from the logged-in customer's market basket.
    private void removeFromBasket() {
        try (Connection conn = DBConnection.getConnection()) {
            List<Basket> basket = basketDAO.findByCustomer(conn, currentCustomerId);
            if (basket.isEmpty()) {
                System.out.println("Your basket is empty.");
                return;
            }
            basket.forEach(b -> System.out.printf(
                    "Product ID: %d | Quantity: %d | Unit Price: %s | Added: %s%n",
                    b.getProductId(), b.getQuantity(), b.getUnitPriceAtAdded(), b.getAddedAt()));

            int productId = InputUtil.readInt("Product ID to remove: ");
            boolean removed = basketDAO.removeBook(conn, currentCustomerId, productId);
            System.out.println(removed ? "Item removed from basket." : "Item not found.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // [REQ5][REQ8][REQ12][REQ13][REQ14] Processes purchase-related inserts and updates in one transaction.
    private void purchase() {
        try (Connection conn = DBConnection.getConnection()) {
            List<SalesDAO.PurchaseItem> items = salesDAO.findPurchaseItems(conn, currentCustomerId);
            salesDAO.validatePurchaseItems(items);

            items.forEach(i -> System.out.printf(
                    "Product ID: %d | Qty: %d | Price: %s | Subtotal: %s%n",
                    i.productId(), i.quantity(), i.unitPriceAtSale(), i.subtotal()));

            String confirm = InputUtil.readString("Proceed with purchase? (y/n): ");
            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("Purchase cancelled.");
                return;
            }

            conn.setAutoCommit(false);
            try {
                var totalAmount = salesDAO.calculateTotalAmount(items);
                // [REQ12] All purchase steps are committed together or rolled back together.
                int salesId = salesDAO.insertSale(conn, currentCustomerId, totalAmount);
                salesDAO.insertSalesDetails(conn, salesId, items);
                salesDAO.upsertTotalSales(conn, items);
                salesDAO.decreaseStock(conn, items);
                basketDAO.clearBasket(conn, currentCustomerId);
                conn.commit();
                System.out.println("Purchase completed. Total: " + totalAmount);
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Purchase failed (rolled back): " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // [REQ5][REQ10] Inserts a review after checking that the customer purchased the book.
    private void writeReview() {
        try (Connection conn = DBConnection.getConnection()) {
            int productId = InputUtil.readInt("Product ID: ");

            // [REQ10] Purchase validation uses PreparedStatement in ReviewDAO.
            if (!reviewDAO.checkPurchaseBeforeReview(conn, currentCustomerId, productId)) {
                System.out.println("You must purchase this book before writing a review.");
                return;
            }

            int rating = InputUtil.readInt("Rating (1-5): ");
            String reviewText = InputUtil.readString("Review: ");
            reviewDAO.writeReview(conn, currentCustomerId, productId, rating, reviewText);
        } catch (IllegalArgumentException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // [REQ9][REQ10] Deletes only reviews owned by the logged-in customer.
    private void deleteReview() {
        try (Connection conn = DBConnection.getConnection()) {
            int reviewId = InputUtil.readInt("Review ID to delete: ");
            reviewDAO.deleteReview(conn, reviewId, currentCustomerId);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // [REQ6] Displays purchase history using a user-specific query with a view and joins.
    private void viewPurchaseHistory() {
        try (Connection conn = DBConnection.getConnection()) {
            analysisService.showMyPurchaseHistory(conn, currentCustomerId);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // [REQ7][REQ14] Shows aggregated sales before and after customer profile changes.
    private void viewProfileChangeAnalysis() {
        try (Connection conn = DBConnection.getConnection()) {
            analysisService.showPurchasesAroundProfileChange(conn, currentCustomerId);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // [REQ7] Shows aggregated popular categories grouped by customer age group.
    private void viewPopularCategories() {
        try (Connection conn = DBConnection.getConnection()) {
            analysisService.showPopularCategoriesByAgeGroup(conn);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
