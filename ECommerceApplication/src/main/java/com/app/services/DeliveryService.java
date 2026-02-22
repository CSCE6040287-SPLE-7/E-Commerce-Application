package com.app.services;

import java.util.List;

import com.app.payloads.DeliveryServiceDTO;

public interface DeliveryService {
	
	List<DeliveryServiceDTO> getAllDeliveryServices();
	
	DeliveryServiceDTO getDeliveryServiceById(Long id);
}
