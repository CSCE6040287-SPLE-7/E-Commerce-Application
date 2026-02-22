package com.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entites.DeliveryService;

@Repository
public interface DeliveryServiceRepo extends JpaRepository<DeliveryService, Long> {

	DeliveryService findByServiceName(String serviceName);
}
