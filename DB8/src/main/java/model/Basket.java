package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Basket {
    private int basketId;
    private int customerId;
    private int productId;
    private int quantity;
    private LocalDateTime addedAt;
    private BigDecimal unitPriceAtAdded;

    public Basket() {
    }

    public Basket(int basketId, int customerId, int productId, int quantity,
                  LocalDateTime addedAt, BigDecimal unitPriceAtAdded) {
        this.basketId = basketId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.addedAt = addedAt;
        this.unitPriceAtAdded = unitPriceAtAdded;
    }

    public int getBasketId() {
        return basketId;
    }

    public void setBasketId(int basketId) {
        this.basketId = basketId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public BigDecimal getUnitPriceAtAdded() {
        return unitPriceAtAdded;
    }

    public void setUnitPriceAtAdded(BigDecimal unitPriceAtAdded) {
        this.unitPriceAtAdded = unitPriceAtAdded;
    }
}
