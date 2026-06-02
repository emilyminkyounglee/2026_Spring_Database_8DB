package model;
import java.time.LocalDate;

public class Review {
    //TODO: Review entity fields + getter/setter
    private int reviewId;
    private int customerId;
    private int productId;
    private int rating;
    private String reviewText;
    private LocalDate reviewDate;

    public Review() {}

    public Review(int reviewId, int customerId, int productId,
                  int rating, String reviewText, LocalDate reviewDate) {
        this.reviewId = reviewId;
        this.customerId = customerId;
        this.productId = productId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
    }

    public int getReviewId() {
        return reviewId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getProductId() {
        return productId;
    }

    public int getRating() {
        return rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }
}