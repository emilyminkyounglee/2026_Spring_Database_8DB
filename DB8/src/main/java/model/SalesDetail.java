package model;

import java.math.BigDecimal;

public class SalesDetail {
    private int salesDetailId;
    private int salesId;
    private int productId;
    private int quantity;
    private BigDecimal unitPriceAtSale;
    private BigDecimal subtotal;

    public SalesDetail() {
    }

    public SalesDetail(int salesDetailId, int salesId, int productId, int quantity,
                       BigDecimal unitPriceAtSale, BigDecimal subtotal) {
        this.salesDetailId = salesDetailId;
        this.salesId = salesId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPriceAtSale = unitPriceAtSale;
        this.subtotal = subtotal;
    }

    public int getSalesDetailId() {
        return salesDetailId;
    }

    public void setSalesDetailId(int salesDetailId) {
        this.salesDetailId = salesDetailId;
    }

    public int getSalesId() {
        return salesId;
    }

    public void setSalesId(int salesId) {
        this.salesId = salesId;
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

    public BigDecimal getUnitPriceAtSale() {
        return unitPriceAtSale;
    }

    public void setUnitPriceAtSale(BigDecimal unitPriceAtSale) {
        this.unitPriceAtSale = unitPriceAtSale;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
