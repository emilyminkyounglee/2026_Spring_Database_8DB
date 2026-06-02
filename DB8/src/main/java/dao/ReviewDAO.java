package dao;
import java.sql.*;

public class ReviewDAO {
    //TODO: Write book review
    public void writeReview(Connection conn,
                            int reviewId,
                            int customerId,
                            int productId,
                            int rating,
                            String reviewText) throws SQLException {

        String sql = """
                INSERT INTO book_review
                (review_id, customer_id, product_id, rating, review_text, review_date)
                VALUES (?, ?, ?, ?, ?, CURDATE())
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            pstmt.setInt(2, customerId);
            pstmt.setInt(3, productId);
            pstmt.setInt(4, rating);
            pstmt.setString(5, reviewText);

            pstmt.executeUpdate();
            System.out.println("Review created.");
        }
    }

    //TODO: Delete my review
    public void deleteReview(Connection conn,
                             int reviewId,
                             int customerId) throws SQLException {
        String sql = """
                DELETE FROM book_review
                WHERE review_id = ?
                AND customer_id = ?
                """;

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

    //TODO: View reviews for a book
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

    //TODO: Check whether customer purchased the book before the review
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
