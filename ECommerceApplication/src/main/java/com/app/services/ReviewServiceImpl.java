package com.app.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.payloads.CreateReviewDTO;
import com.app.payloads.ReviewDTO;
import com.app.repositories.ReviewRepo;
import com.app.entites.Review;
import com.app.repositories.UserRepo;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ReviewDTO addReview(CreateReviewDTO createReviewDTO) {
        String email = createReviewDTO.getEmailUser();
        Integer rating = createReviewDTO.getRating();
        String comment = createReviewDTO.getComment();

        userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));

        Review reviewByEmail = reviewRepo.findByEmailUser(email);
        if (reviewByEmail != null) {
            throw new RuntimeException("User with email " + email + " has already submitted a review");
        }

        Review review = new Review();
        review.setEmailUser(email);
        review.setRating(rating);
        review.setComment(comment);

        Review savedReview = reviewRepo.save(review);
        return modelMapper.map(savedReview, ReviewDTO.class);
    }

    @Override
    public List<ReviewDTO> getAllReviews() {
        List<Review> reviews = reviewRepo.findAll();
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO getReviewById(Long reviewId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review with id " + reviewId + " not found"));
        return modelMapper.map(review, ReviewDTO.class);
    }

    @Override
    public ReviewDTO updateReview(Long reviewId, Review updatedReview) {
        Review existingReview = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review with id " + reviewId + " not found"));

        if (updatedReview.getEmailUser() != null) {
            userRepo.findByEmail(updatedReview.getEmailUser())
                .orElseThrow(() -> new RuntimeException("User with email " + updatedReview.getEmailUser() + " not found"));

            existingReview.setEmailUser(updatedReview.getEmailUser());
        }

        if (updatedReview.getRating() != null) {
            existingReview.setRating(updatedReview.getRating());
        }

        if (updatedReview.getComment() != null) {
            existingReview.setComment(updatedReview.getComment());
        }

        Review savedReview = reviewRepo.save(existingReview);
        return modelMapper.map(savedReview, ReviewDTO.class);
    }

    @Override
    public String deleteReview(Long reviewId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review with id " + reviewId + " not found"));

        reviewRepo.delete(review);
        return "Review with id " + reviewId + " deleted successfully";
    }
}