package com.app.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entites.PickupLocation;
import com.app.payloads.PickupLocationDTO;
import com.app.repositories.PickupLocationRepo;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class PickupLocationServiceImpl implements PickupLocationService {

	@Autowired
	private PickupLocationRepo pickupLocationRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public List<PickupLocationDTO> getAllPickupLocations() {
		List<PickupLocation> pickupLocations = pickupLocationRepo.findAll();
		
		return pickupLocations.stream()
				.map(location -> modelMapper.map(location, PickupLocationDTO.class))
				.collect(Collectors.toList());
	}

}
