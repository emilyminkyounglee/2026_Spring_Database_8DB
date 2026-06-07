package model;

import java.math.BigDecimal;

// [REQ17] Model class representing one product/book row.
public class Product {
    private int productId;
    private int categoryId;
    private String productName;
    private String author;
    private String publisher;
    private BigDecimal unitPrice;
    private int stockQuantity;

    // [REQ17] Standard getters expose product fields to DAO/menu code.
    public int getProductId() { return productId; }
    public int getCategoryId() { return categoryId; }
    public String getProductName() { return productName; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getStockQuantity() { return stockQuantity; }

    // [REQ17] Standard setters map SQL result columns into Product objects.
    public void setProductId(int productId) { this.productId = productId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
}
