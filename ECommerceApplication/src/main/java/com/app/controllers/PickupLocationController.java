package com.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.payloads.PickupLocationDTO;
import com.app.services.PickupLocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Pickup Location Management", description = "APIs for managing pickup locations")
public class PickupLocationController {
	
	@Autowired
	private PickupLocationService pickupLocationService;
	
	@Operation(
		summary = "Get All Pickup Locations",
		description = "Retrieve list of all available pickup locations with their addresses"
	)
	@GetMapping("/public/pickupLocations")
	public ResponseEntity<List<PickupLocationDTO>> getAllPickupLocations() {
		List<PickupLocationDTO> pickupLocations = pickupLocationService.getAllPickupLocations();
		return new ResponseEntity<>(pickupLocations, HttpStatus.OK);
	}

}
