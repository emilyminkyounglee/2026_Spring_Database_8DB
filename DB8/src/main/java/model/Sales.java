package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// [REQ14][REQ17] Model class representing one sales row.
public class Sales {
    private int salesId;
    private int customerId;
    private LocalDateTime salesTimestamp;
    private BigDecimal totalAmount;
    private int profileId;
    private int ageAtSale;

    // [REQ17] Default constructor used when creating sales objects manually.
    public Sales() {
    }

    // [REQ14][REQ17] Full constructor includes profile_id and age_at_sale.
    public Sales(int salesId, int customerId, LocalDateTime salesTimestamp,
                 BigDecimal totalAmount, int profileId, int ageAtSale) {
        this.salesId = salesId;
        this.customerId = customerId;
        this.salesTimestamp = salesTimestamp;
        this.totalAmount = totalAmount;
        this.profileId = profileId;
        this.ageAtSale = ageAtSale;
    }

    // [REQ17] Standard getters and setters expose sales fields.
    public int getSalesId() {
        return salesId;
    }

    public void setSalesId(int salesId) {
        this.salesId = salesId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getSalesTimestamp() {
        return salesTimestamp;
    }

    public void setSalesTimestamp(LocalDateTime salesTimestamp) {
        this.salesTimestamp = salesTimestamp;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getAgeAtSale() {
        return ageAtSale;
    }

    public void setAgeAtSale(int ageAtSale) {
        this.ageAtSale = ageAtSale;
    }
}
