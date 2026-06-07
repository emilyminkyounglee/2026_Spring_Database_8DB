package dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class AnalysisDAO {
    // [REQ7] Aggregates category popularity by age group using GROUP BY.
    public List<Map<String, Object>> getPopularCategoriesByAgeGroup(Connection conn) throws SQLException {
        String sql = """
            SELECT age_group, category_name, order_count, total_quantity, total_revenue
            FROM (
                SELECT
                    CASE
                        WHEN s.age_at_sale < 20 THEN 'Under 20'
                        WHEN s.age_at_sale < 30 THEN '20s'
                        WHEN s.age_at_sale < 40 THEN '30s'
                        WHEN s.age_at_sale < 50 THEN '40s'
                        ELSE '50s and above'
                    END                             AS age_group,
                    bc.category_name,
                    COUNT(sd.sales_detail_id)       AS order_count,
                    SUM(sd.quantity)                AS total_quantity,
                    SUM(sd.subtotal)                AS total_revenue,
                    ROW_NUMBER() OVER (
                        PARTITION BY
                            CASE
                                WHEN s.age_at_sale < 20 THEN 'Under 20'
                                WHEN s.age_at_sale < 30 THEN '20s'
                                WHEN s.age_at_sale < 40 THEN '30s'
                                WHEN s.age_at_sale < 50 THEN '40s'
                                ELSE '50s and above'
                            END
                        ORDER BY SUM(sd.quantity) DESC
                    )                               AS rn
                FROM sales s
                JOIN sales_detail sd  ON s.sales_id = sd.sales_id
                JOIN product p        ON sd.product_id = p.product_id
                JOIN book_category bc ON p.category_id = bc.category_id
                GROUP BY age_group, bc.category_name
            ) ranked
            WHERE rn <= 3
            ORDER BY 
                CASE age_group
                    WHEN 'Under 20' THEN 1
                    WHEN  '20s' THEN  2
                    WHEN  '30s' THEN  3
                    WHEN  '40s' THEN  4
                    WHEN  '50s and above' THEN  5
                END,
                total_quantity DESC
            """;

        List<Map<String, Object>> result = new ArrayList<>();

        // [REQ10] PreparedStatement is used consistently for query execution.
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("age_group", rs.getString("age_group"));
                row.put("category_name", rs.getString("category_name"));
                row.put("order_count", rs.getInt("order_count"));
                row.put("total_quantity", rs.getInt("total_quantity"));
                row.put("total_revenue", rs.getBigDecimal("total_revenue"));
                result.add(row);
            }
        }
        return result;
    }

    // [REQ7] Shows aggregated total sales by product and category.
    public List<Map<String, Object>> getProductTotalSalesSummary(Connection conn) throws SQLException {
        String sql = """
                SELECT
                    bc.category_name,
                    p.product_id,
                    p.product_name,
                    p.author,
                    p.unit_price    AS current_price,
                    ts.total_quantity,
                    ts.total_revenue
                FROM total_sales ts
                JOIN product p        ON ts.product_id = p.product_id
                JOIN book_category bc ON p.category_id = bc.category_id
                ORDER BY ts.total_revenue DESC
                """;

        List<Map<String, Object>> result = new ArrayList<>();

        // [REQ10] PreparedStatement is used consistently for query execution.
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("category_name", rs.getString("category_name"));
                row.put("product_id", rs.getInt("product_id"));
                row.put("product_name", rs.getString("product_name"));
                row.put("author", rs.getString("author"));
                row.put("current_price", rs.getBigDecimal("current_price"));
                row.put("total_quantity", rs.getInt("total_quantity"));
                row.put("total_revenue", rs.getBigDecimal("total_revenue"));
                result.add(row);
            }
        }
        return result;
    }

    // [REQ7][REQ10][REQ13] Aggregates sales for each product price history period.
    public List<Map<String, Object>> analyzeSalesAroundPriceChange(Connection conn, int productId) throws SQLException {
        String sql = """
                SELECT
                    pph.price_history_id,
                    pph.unit_price                      AS price_in_period,
                    pph.start_date,
                    pph.end_date,
                    COUNT(DISTINCT sd.sales_id)         AS order_count,
                    COALESCE(SUM(sd.quantity),  0)      AS total_quantity,
                    COALESCE(SUM(sd.subtotal),  0)      AS total_revenue
                FROM product_price_history pph
                LEFT JOIN sales_detail sd
                       ON sd.product_id = pph.product_id
                      AND sd.unit_price_at_sale = pph.unit_price
                LEFT JOIN sales s
                       ON sd.sales_id = s.sales_id
                      AND s.sales_timestamp >= pph.start_date
                      AND (pph.end_date IS NULL OR s.sales_timestamp < pph.end_date)
                WHERE pph.product_id = ?
                GROUP BY pph.price_history_id, pph.unit_price, pph.start_date, pph.end_date
                ORDER BY pph.start_date
                """;

        List<Map<String, Object>> result = new ArrayList<>();

        // [REQ10] The product id from user input is bound to the analysis query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("price_history_id", rs.getInt("price_history_id"));
                    row.put("price_in_period", rs.getBigDecimal("price_in_period"));
                    row.put("start_date", rs.getTimestamp("start_date"));

                    Timestamp endDate = rs.getTimestamp("end_date");
                    row.put("end_date", rs.wasNull() ? "(Current)" : endDate.toString());

                    row.put("order_count", rs.getInt("order_count"));
                    row.put("total_quantity", rs.getInt("total_quantity"));
                    row.put("total_revenue", rs.getBigDecimal("total_revenue"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    // [REQ7][REQ10][REQ14] Aggregates customer sales by profile history periods.
    public List<Map<String, Object>> analyzePurchasesAroundProfileChange(Connection conn, int customerId) throws SQLException {
        String sql = """
                SELECT
                    cph.profile_id,
                    cph.city,
                    cph.membership_level,
                    cph.start_date,
                    cph.end_date,
                    COUNT(s.sales_id)                       AS order_count,
                    COALESCE(SUM(s.total_amount),  0)       AS total_amount,
                    COALESCE(AVG(s.total_amount),  0)       AS avg_order_amount
                FROM customer_profile_history cph
                LEFT JOIN sales s ON s.profile_id = cph.profile_id
                WHERE cph.customer_id = ?
                GROUP BY cph.profile_id, cph.city, cph.membership_level,
                         cph.start_date, cph.end_date
                ORDER BY cph.start_date
                """;

        List<Map<String, Object>> result = new ArrayList<>();

        // [REQ10] The logged-in customer id is bound to the demographic analysis query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("profile_id", rs.getInt("profile_id"));
                    row.put("city", rs.getString("city"));
                    row.put("membership_level", rs.getString("membership_level"));
                    row.put("start_date", rs.getTimestamp("start_date"));

                    Timestamp endDate = rs.getTimestamp("end_date");
                    row.put("end_date", rs.wasNull() ? "(Current)" : endDate.toString());

                    row.put("order_count", rs.getInt("order_count"));
                    row.put("total_amount", rs.getBigDecimal("total_amount"));
                    row.put("avg_order_amount", rs.getBigDecimal("avg_order_amount"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    // [REQ6][REQ10] Uses a view, joins, and customer input to retrieve purchase history.
    public List<Map<String, Object>> getCustomerPurchaseHistory(Connection conn, int customerId) throws SQLException {
        String sql = """
                SELECT
                    v.sales_id,
                    v.sales_timestamp,
                    v.total_amount,
                    s.age_at_sale,
                    v.product_id,
                    v.product_name,
                    p.author,
                    v.category_name,
                    v.quantity,
                    v.unit_price_at_sale,
                    v.subtotal
                FROM v_customer_purchase_history v
                JOIN sales s ON v.sales_id = s.sales_id
                JOIN product p ON v.product_id = p.product_id
                WHERE v.customer_id = ?
                ORDER BY v.sales_timestamp DESC, v.sales_id, v.product_id
                """;

        List<Map<String, Object>> result = new ArrayList<>();

        // [REQ10] The logged-in customer id is bound to the view-based SELECT query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("sales_id", rs.getInt("sales_id"));
                    row.put("sales_timestamp", rs.getTimestamp("sales_timestamp"));
                    row.put("total_amount", rs.getBigDecimal("total_amount"));
                    row.put("age_at_sale", rs.getInt("age_at_sale"));
                    row.put("product_id", rs.getInt("product_id"));
                    row.put("product_name", rs.getString("product_name"));
                    row.put("author", rs.getString("author"));
                    row.put("category_name", rs.getString("category_name"));
                    row.put("quantity", rs.getInt("quantity"));
                    row.put("unit_price_at_sale", rs.getBigDecimal("unit_price_at_sale"));
                    row.put("subtotal", rs.getBigDecimal("subtotal"));
                    result.add(row);
                }
            }
        }
        return result;
    }

    // [REQ15] Prints query results as a text-based table for the console UI.
    public void printResultTable(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            System.out.println("  (No results found)");
            return;
        }

        List<String> columns = new ArrayList<>(rows.get(0).keySet());

        // [REQ15] Calculates column widths so text-table output stays readable.
        Map<String, Integer> widths = new LinkedHashMap<>();
        for (String col : columns) {
            widths.put(col, col.length());
        }
        for (Map<String, Object> row : rows) {
            for (String col : columns) {
                String s = formatValue(row.get(col));
                widths.put(col, Math.max(widths.get(col), s.length()));
            }
        }

        // [REQ15] Builds the divider for the text-table output.
        StringBuilder divider = new StringBuilder("+");
        for (String col : columns) divider.append("-".repeat(widths.get(col) + 2)).append("+");

        // [REQ15] Prints the table header.
        System.out.println(divider);
        StringBuilder header = new StringBuilder("|");
        for (String col : columns)
            header.append(String.format(" %-" + widths.get(col) + "s |", col));
        System.out.println(header);
        System.out.println(divider);

        // [REQ15] Prints each result row.
        for (Map<String, Object> row : rows) {
            StringBuilder line = new StringBuilder("|");
            for (String col : columns)
                line.append(String.format(" %-" + widths.get(col) + "s |", formatValue(row.get(col))));
            System.out.println(line);
        }
        System.out.println(divider);
        System.out.printf("  Total %d books%n", rows.size());
    }

    // [REQ15] Formats NULL and BigDecimal values for text-table output.
    private String formatValue(Object val) {
        if (val == null) return "NULL";
        if (val instanceof BigDecimal bd) return String.format("%.2f", bd);
        return val.toString();
    }
}
