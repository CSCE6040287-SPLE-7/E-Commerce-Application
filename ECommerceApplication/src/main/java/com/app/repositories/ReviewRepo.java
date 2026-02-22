package com.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entites.Review;

@Repository
public interface ReviewRepo extends JpaRepository<Review, Long>{
    Review findByEmailUser(String email);
}
