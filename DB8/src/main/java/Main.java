public class Main {
    //TODO: Start program and connect menus/services

    public static void main(String[] args) throws Exception {
        try (java.sql.Connection conn = util.DBConnection.get()) {
            System.out.println("Connected: " + conn.getCatalog());
        }
    }
}
