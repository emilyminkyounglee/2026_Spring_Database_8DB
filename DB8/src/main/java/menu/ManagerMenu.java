package menu;

import model.Manager;
import service.ProductService;
import service.ManagerService;
import util.InputUtil;
import util.MenuPrinter;

import java.sql.SQLException;

public class ManagerMenu {
    //TODO: Print manager menu

    //TODO: Get user menu input

    //TODO: Call ProductDAO for book search and price updates

    //TODO: Call AnalysisDAO for sales analysis quires

    //TODO: Use transaction for product price update

    //TODO: Handle invalid menu input

    //TODO: Return to main menu

    private final ManagerService managerService = new ManagerService();
    private final ProductService productService = new ProductService();

    public void run(Manager manager){
        while (true) {
            MenuPrinter.printManagerMenu(manager.getRoleName());
            int choice = InputUtil.readInt("Select an option: ");

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

            if (choice == 7) {
                addStock();
                continue;
            }

            System.out.println("Selected menu is not implemented yet.");
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
}
