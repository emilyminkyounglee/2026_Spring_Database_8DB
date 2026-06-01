import service.SalesService;

public class Main {
    public static void main(String[] args) {
        SalesService salesService = new SalesService();

        int customerId = 9001;

        try {
            System.out.println("=== Purchase Basket Test Start ===");

            int salesId = salesService.purchaseBasket(customerId);

            System.out.println("Purchase completed successfully.");
            System.out.println("Created salesId = " + salesId);

            System.out.println("=== Purchase Basket Test End ===");
        } catch (Exception e) {
            System.out.println("Test failed.");
            e.printStackTrace();
        }
    }
}