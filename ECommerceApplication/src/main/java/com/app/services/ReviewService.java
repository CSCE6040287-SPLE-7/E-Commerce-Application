package com.app.services;

import java.util.List;

import com.app.entites.Review;
import com.app.payloads.ReviewDTO;

public interface ReviewService {
    ReviewDTO addReview(String email, Integer rating);

    List<ReviewDTO> getAllReviews();
    
    ReviewDTO getReviewById(Long reviewId);

    ReviewDTO updateReview(Long reviewId, Review review);

    String deleteReview(Long reviewId);

}
