import menu.CustomerMenu;
import menu.ManagerMenu;
import util.InputUtil;
import util.MenuPrinter;
import model.Manager;
import service.CustomerService;
import service.ManagerService;

import java.sql.Connection;
import java.sql.SQLException;


public class Main {
    private static final int MAX_PASSWORD_ATTEMPTS = 3;

    //TODO: Start program and connect menus/services
    public static void main(String[] args) {
        testConnection();

        CustomerMenu customerMenu = new CustomerMenu();
        CustomerService customerService = new CustomerService();
        ManagerMenu managerMenu = new ManagerMenu();
        ManagerService managerService = new ManagerService();

        while (true){
            MenuPrinter.printMainMenu();
            int choice = InputUtil.readInt("Select an option: ");

            switch (choice){
                case 1 -> loginCustomer(customerService, customerMenu);
                case 2 -> loginManager(managerService, managerMenu);
                case 0 -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Invalid input!");
            }
        }
    }

    private static void loginCustomer(CustomerService customerService, CustomerMenu customerMenu) {
        String email = InputUtil.readString("Customer email: ");
        Integer customerId = customerService.findCustomerIdByEmail(email);

        if (customerId == null) {
            System.out.println("Customer email does not exist. Please register as a new customer first.");
            return;
        }

        for (int attempt = 1; attempt <= MAX_PASSWORD_ATTEMPTS; attempt++) {
            String password = InputUtil.readString("Password: ");
            Integer authenticatedCustomerId = customerService.login(email, password);

            if (authenticatedCustomerId != null) {
                System.out.println("Customer login successful.");
                customerMenu.run(authenticatedCustomerId);
                return;
            }

            System.out.println("Incorrect password. Attempts left: " + (MAX_PASSWORD_ATTEMPTS - attempt));
        }

        System.out.println("Customer login failed.");
    }

    private static void loginManager(ManagerService managerService, ManagerMenu managerMenu) {
        String email = InputUtil.readString("Manager email: ");
        Integer managerId = managerService.findManagerIdByEmail(email);

        if (managerId == null) {
            System.out.println("Manager email does not exist.");
            return;
        }

        for (int attempt = 1; attempt <= MAX_PASSWORD_ATTEMPTS; attempt++) {
            String password = InputUtil.readString("Password: ");
            Manager manager = managerService.login(email, password);

            if (manager != null) {
                System.out.println("Manager login successful. roles = " + manager.getRoleSummary());
                managerMenu.run(manager);
                return;
            }

            System.out.println("Incorrect password. Attempts left: " + (MAX_PASSWORD_ATTEMPTS - attempt));
        }

        System.out.println("Manager login failed.");
    }

    private static void testConnection(){
        try (Connection conn = util.DBConnection.getConnection()){
            System.out.println("Connected to database: " + conn.getCatalog());
        } catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
