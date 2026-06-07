package util;

import java.util.Set;

public class MenuPrinter {

    private MenuPrinter() {
    }

    // [REQ15] Prints the main text-based application menu.
    public static void printMainMenu() {
        System.out.println();
        System.out.println("===== Online Book Store =====");
        System.out.println("1. Customer Login");
        System.out.println("2. Register new customer");
        System.out.println("3. Manager Login");
        System.out.println("0. Exit");
    }

    // [REQ15] Prints the customer text-based menu.
    public static void printCustomerMenu() {
        System.out.println();
        System.out.println("===== Customer Menu =====");
        System.out.println("1. Search books by category or keyword");
        System.out.println("2. Add book to market basket");
        System.out.println("3. Remove book from market basket");
        System.out.println("4. Purchase books in market basket");
        System.out.println("5. View my purchase history");
        System.out.println("6. View purchases before/after my profile changes");
        System.out.println("7. View popular categories by age group");
        System.out.println("8. View my profile information");
        System.out.println("9. Update my profile information");
        System.out.println("10. Write book review");
        System.out.println("11. Delete my review");
        System.out.println("0. Logout");
    }

    // [REQ15] Backward-compatible overload for a single manager role.
    public static void printManagerMenu(String roleName) {
        printManagerMenu(Set.of(roleName));
    }

    // [REQ15] Prints manager menu items and MASTER-only account management options.
    public static void printManagerMenu(Set<String> roleNames) {
        System.out.println();
        System.out.println("===== Manager Menu (" + String.join(", ", roleNames) + ") =====");
        System.out.println("1. Search books by category or keyword");
        System.out.println("2. Update product price");
        System.out.println("3. Analyze sales before/after product price changes");
        System.out.println("4. View product total sales summary");
        System.out.println("5. View sales analysis summary");
        System.out.println("6. View inventory status");
        System.out.println("7. Update stock quantity");
        if (roleNames.contains("MASTER")) {
            System.out.println("8. Manage manager roles");
            System.out.println("9. Register new manager");
            System.out.println("10. Dismiss manager");
        }
        System.out.println("0. Logout");
    }

    // [REQ15] Prints a fallback manager menu when role information is unavailable.
    public static void printManagerMenu() {
        printManagerMenu("UNKNOWN");
    }
}
