package com.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entites.PickupLocation;

@Repository
public interface PickupLocationRepo extends JpaRepository<PickupLocation, Long> {

}
