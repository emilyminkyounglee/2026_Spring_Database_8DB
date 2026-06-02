package model;

import java.math.BigDecimal;

public class TotalSales {
    private int totalSalesId;
    private int productId;
    private int totalQuantity;
    private BigDecimal totalRevenue;

    public TotalSales() {
    }

    public TotalSales(int totalSalesId, int productId, int totalQuantity, BigDecimal totalRevenue) {
        this.totalSalesId = totalSalesId;
        this.productId = productId;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

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
