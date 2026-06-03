package menu;

import dao.*;
import model.Basket;
import service.AnalysisService;
import util.DBConnection;
import util.InputUtil;
import util.MenuPrinter;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class CustomerMenu {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final BasketDAO basketDAO = new BasketDAO();
    private final SalesDAO salesDAO = new SalesDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final AnalysisService analysisService = new AnalysisService();

    //TODO: Print customer menu
    public void run(int customerId) {
        while (true) {
            MenuPrinter.printCustomerMenu();
            //TODO: Get user menu input
            int choice = InputUtil.readInt("Select an option: ");

            switch (choice) {
                //TODO: Return to main menu
                case 0 -> {
                    System.out.println("Logged out.");
                    return;
                }
                case 1 -> registerCustomer();
                case 2 -> addToBasket(customerId);
                case 3 -> removeFromBasket(customerId);
                case 4 -> purchase(customerId);
                case 5 -> viewPurchaseHistory(customerId);
                case 6 -> viewProfileChangeAnalysis(customerId);
                case 7 -> viewPopularCategories();
                case 8 -> updateProfile(customerId);
                case 9 -> writeReview(customerId);
                case 10 -> deleteReview(customerId);
                //TODO: Handle invaild menu input
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void run(){
        run(-1);
    }

    //TODO: Call CustomerDAO for customer-related functions
    private void registerCustomer() {
        try (Connection conn = DBConnection.getConnection()) {
            int customerId = InputUtil.readInt("Customer ID: ");
            String firstName = InputUtil.readString("First name: ");
            String lastName = InputUtil.readString("Last name: ");
            String email = InputUtil.readString("Email: ");
            String password = InputUtil.readString("Password: ");
            String phone = InputUtil.readString("Phone: ");
            String birthStr = InputUtil.readString("Birth date (YYYY-MM-DD): ");
            Date birthDate = Date.valueOf(birthStr);

            customerDAO.registerCustomer(conn, customerId, firstName, lastName, email, password, phone, birthDate);
            System.out.println("Customer registered successfully.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateProfile(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            String firstName = InputUtil.readString("First name: ");
            String lastName = InputUtil.readString("Last name: ");
            String email = InputUtil.readString("Email: ");
            String phone = InputUtil.readString("Phone: ");
            String birthStr = InputUtil.readString("Birth date (YYYY-MM-DD): ");
            Date birthDate = Date.valueOf(birthStr);

            customerDAO.updateCustomerProfile(conn, customerId, firstName, lastName, email, phone, birthDate);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //TODO: Call BasketDAO for basket-related functions
    private void addToBasket(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            int productId = InputUtil.readInt("Product ID: ");
            int quantity = InputUtil.readInt("Quantity: ");
            basketDAO.addBook(conn, customerId, productId, quantity);
            System.out.println("Book added to basket.");
        } catch (IllegalArgumentException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void removeFromBasket(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            List<Basket> basket = basketDAO.findByCustomer(conn, customerId);
            if (basket.isEmpty()) {
                System.out.println("Your basket is empty.");
                return;
            }
            basket.forEach(b -> System.out.printf(
                    "Product ID: %d | Quantity: %d | Unit Price: %s | Added: %s%n",
                    b.getProductId(), b.getQuantity(), b.getUnitPriceAtAdded(), b.getAddedAt()));

            int productId = InputUtil.readInt("Product ID to remove: ");
            boolean removed = basketDAO.removeBook(conn, customerId, productId);
            System.out.println(removed ? "Item removed from basket." : "Item not found.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void purchase(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            List<SalesDAO.PurchaseItem> items = salesDAO.findPurchaseItems(conn, customerId);
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
                //TODO: Call SalesDAO for purchase transaction
                int salesId = salesDAO.insertSale(conn, customerId, totalAmount);
                salesDAO.insertSalesDetails(conn, salesId, items);
                salesDAO.upsertTotalSales(conn, items);
                salesDAO.decreaseStock(conn, items);
                basketDAO.clearBasket(conn, customerId);
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

    //TODO: Call ReviewDAO for review functions
    private void writeReview(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            int productId = InputUtil.readInt("Product ID: ");

            // 구매 여부 확인
            if (!reviewDAO.checkPurchaseBeforeReview(conn, customerId, productId)) {
                System.out.println("You must purchase this book before writing a review.");
                return;
            }

            int reviewId = InputUtil.readInt("Review ID: ");
            int rating = InputUtil.readInt("Rating (1-5): ");
            String reviewText = InputUtil.readString("Review: ");
            reviewDAO.writeReview(conn, reviewId, customerId, productId, rating, reviewText);
        } catch (IllegalArgumentException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteReview(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            int reviewId = InputUtil.readInt("Review ID to delete: ");
            reviewDAO.deleteReview(conn, reviewId, customerId);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    //TODO: Call AnalysisDAO for analysis queries
    private void viewPurchaseHistory(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            analysisService.showMyPurchaseHistory(conn, customerId);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewProfileChangeAnalysis(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            analysisService.showPurchasesAroundProfileChange(conn, customerId);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewPopularCategories() {
        try (Connection conn = DBConnection.getConnection()) {
            analysisService.showPopularCategoriesByAgeGroup(conn);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}