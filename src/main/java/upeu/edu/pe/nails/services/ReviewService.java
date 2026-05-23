package upeu.edu.pe.nails.services;

import upeu.edu.pe.nails.entities.Review;

import java.util.List;

public interface ReviewService {

    Review createReview(Review review);
    List<Review> getPublicReviews();
    void deleteReview(Long reviewId);

}