package model;

import java.math.BigDecimal;

// [REQ17] Model class representing one total_sales summary row.
public class TotalSales {
    private int totalSalesId;
    private int productId;
    private int totalQuantity;
    private BigDecimal totalRevenue;

    // [REQ17] Default constructor used when creating total sales objects manually.
    public TotalSales() {
    }

    // [REQ17] Full constructor maps total_sales columns to Java fields.
    public TotalSales(int totalSalesId, int productId, int totalQuantity, BigDecimal totalRevenue) {
        this.totalSalesId = totalSalesId;
        this.productId = productId;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    // [REQ17] Standard getters and setters expose total_sales fields.
    public int getTotalSalesId() {
        return totalSalesId;
    }

    public void setTotalSalesId(int totalSalesId) {
        this.totalSalesId = totalSalesId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
