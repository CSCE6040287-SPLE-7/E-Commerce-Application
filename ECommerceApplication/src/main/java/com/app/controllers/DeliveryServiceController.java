package com.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.payloads.DeliveryServiceDTO;
import com.app.services.DeliveryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Delivery Service Management", description = "APIs for viewing available delivery services")
public class DeliveryServiceController {

	@Autowired
	private DeliveryService deliveryService;

	@Operation(
		summary = "Get All Delivery Services",
		description = "Retrieve list of all available delivery services with their details including cost percentage and estimated delivery days"
	)
	@GetMapping("/public/deliveryServices")
	public ResponseEntity<List<DeliveryServiceDTO>> getAllDeliveryServices() {
		List<DeliveryServiceDTO> deliveryServices = deliveryService.getAllDeliveryServices();
		return new ResponseEntity<>(deliveryServices, HttpStatus.OK);
	}

	@Operation(
		summary = "Get Delivery Service by ID",
		description = "Retrieve a specific delivery service by its ID"
	)
	@GetMapping("/public/deliveryServices/{id}")
	public ResponseEntity<DeliveryServiceDTO> getDeliveryServiceById(
			@Parameter(description = "Delivery Service ID") @PathVariable Long id) {
		DeliveryServiceDTO deliveryServiceDTO = deliveryService.getDeliveryServiceById(id);
		return new ResponseEntity<>(deliveryServiceDTO, HttpStatus.OK);
	}
}
