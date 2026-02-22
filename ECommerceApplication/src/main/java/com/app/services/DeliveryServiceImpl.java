package com.app.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.exceptions.ResourceNotFoundException;
import com.app.payloads.DeliveryServiceDTO;
import com.app.repositories.DeliveryServiceRepo;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class DeliveryServiceImpl implements DeliveryService {

	@Autowired
	private DeliveryServiceRepo deliveryServiceRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<DeliveryServiceDTO> getAllDeliveryServices() {
		List<com.app.entites.DeliveryService> deliveryServices = deliveryServiceRepo.findAll();
		
		return deliveryServices.stream()
				.map(service -> modelMapper.map(service, DeliveryServiceDTO.class))
				.collect(Collectors.toList());
	}

	@Override
	public DeliveryServiceDTO getDeliveryServiceById(Long id) {
		com.app.entites.DeliveryService deliveryService = deliveryServiceRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("DeliveryService", "id", id));
		
		return modelMapper.map(deliveryService, DeliveryServiceDTO.class);
	}
}
