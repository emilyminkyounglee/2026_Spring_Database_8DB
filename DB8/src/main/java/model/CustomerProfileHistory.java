package model;
import java.time.LocalDateTime;

// [REQ14][REQ17] Model class representing customer demographic/profile history.
public class CustomerProfileHistory {
    private int profileId;
    private int customerId;
    private String city;
    private String membershipLevel;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // [REQ17] Default constructor used when creating model objects manually.
    public CustomerProfileHistory() {}

    // [REQ14][REQ17] Full constructor maps profile history columns to Java fields.
    public CustomerProfileHistory(int profileId, int customerId, String city,
                                  String membershipLevel, LocalDateTime startDate,
                                  LocalDateTime endDate) {
        this.profileId = profileId;
        this.customerId = customerId;
        this.city = city;
        this.membershipLevel = membershipLevel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // [REQ17] Standard getters and setters expose profile history fields.
    public int getProfileId() {
        return profileId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCity() {
        return city;
    }

    public String getMembershipLevel() {
        return membershipLevel;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setMembershipLevel(String membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }
}
