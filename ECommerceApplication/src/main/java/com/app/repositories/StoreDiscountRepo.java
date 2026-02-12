package com.app.repositories;

import com.app.entites.StoreDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreDiscountRepo extends JpaRepository<StoreDiscount, Long> {
    // Find store discount by name
    Optional<StoreDiscount> findByName(String name);

    // Find store discounts that are currently active
    @Query("SELECT sd FROM StoreDiscount sd WHERE sd.startDate <= CURRENT_TIMESTAMP AND sd.endDate >= CURRENT_TIMESTAMP AND sd.isActive = true")
    List<StoreDiscount> findCurrentlyActiveDiscounts();
}