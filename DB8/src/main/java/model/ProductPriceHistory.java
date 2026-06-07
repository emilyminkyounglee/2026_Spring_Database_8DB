package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// [REQ13][REQ17] Model class representing product_price_history rows.
public class ProductPriceHistory {
    private int priceHistoryId;
    private int productId;
    private BigDecimal unitPrice;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // [REQ17] Standard getters expose product price history fields.
    public int getPriceHistoryId() { return priceHistoryId; }
    public int getProductId() { return productId; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }

    // [REQ17] Standard setters map SQL result columns into ProductPriceHistory objects.
    public void setPriceHistoryId(int priceHistoryId) { this.priceHistoryId = priceHistoryId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
}
