package com.app.services;

import java.util.List;

import com.app.entites.Review;
import com.app.payloads.CreateReviewDTO;
import com.app.payloads.ReviewDTO;

public interface ReviewService {
    ReviewDTO addReview(CreateReviewDTO createReviewDTO);

    List<ReviewDTO> getAllReviews();
    
    ReviewDTO getReviewById(Long reviewId);

    ReviewDTO updateReview(Long reviewId, Review review);

    String deleteReview(Long reviewId);

}
