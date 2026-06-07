package dao;
import java.sql.*;

public class ReviewDAO {
    // [REQ5][REQ10] Inserts a book review from user-entered rating and review text.
    public void writeReview(Connection conn,
                            int customerId,
                            int productId,
                            int rating,
                            String reviewText) throws SQLException {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        String sql = """
                INSERT INTO book_review
                (customer_id, product_id, rating, review_text, review_date)
                VALUES (?, ?, ?, ?, CURDATE())
                """;

        // [REQ10] Review values are bound through PreparedStatement.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, rating);
            pstmt.setString(4, reviewText);

            pstmt.executeUpdate();
            System.out.println("Review created.");
        }
    }

    // [REQ9][REQ10] Deletes a review only if it belongs to the logged-in customer.
    public void deleteReview(Connection conn,
                             int reviewId,
                             int customerId) throws SQLException {
        String sql = """
                DELETE FROM book_review
                WHERE review_id = ?
                AND customer_id = ?
                """;

        // [REQ10] Review id and customer id are bound to the delete query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            pstmt.setInt(2, customerId);

            int result = pstmt.executeUpdate();

            if (result > 0) {
                System.out.println("Review deleted.");
            } else {
                System.out.println("Review not found.");
            }
        }
    }

    // [REQ10] Retrieves reviews for a selected book.
    public void viewReviewsForBook(Connection conn,
                                   int productId) throws SQLException {
        String sql = """
                SELECT br.review_id,
                       br.customer_id,
                       br.rating,
                       br.review_text,
                       br.review_date
                FROM book_review br
                WHERE br.product_id = ?
                ORDER BY br.review_date DESC
                """;

        // [REQ10] Product id is bound to the SELECT query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {

                boolean found = false;

                while (rs.next()) {
                    found = true;
                    System.out.println(
                            "Review ID: " + rs.getInt("review_id")
                                    + " | Customer: " + rs.getInt("customer_id")
                                    + " | Rating: " + rs.getInt("rating")
                                    + " | Review: " + rs.getString("review_text")
                                    + " | Date: " + rs.getDate("review_date")
                    );
                }

                if (!found) {
                    System.out.println("No reviews found.");
                }
            }
        }
    }

    // [REQ10] Checks purchase history before allowing a customer to write a review.
    public boolean checkPurchaseBeforeReview(Connection conn,
                                             int customerId,
                                             int productId) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM sales s
                JOIN sales_detail sd
                    ON s.sales_id = sd.sales_id
                WHERE s.customer_id = ?
                AND sd.product_id = ?
                """;

        // [REQ10] Customer id and product id are bound to the validation query.
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
