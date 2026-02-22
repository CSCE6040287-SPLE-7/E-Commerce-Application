package com.app.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequestDTO {
	
	@NotBlank(message = "Payment method is required")
	@Size(min = 3, message = "Payment method must contain at least 3 characters")
	private String paymentMethod;
	
	// For shipping method
	private String shippingMethod; // "delivery" or "pickup"
	
	// For bank transfer
	private String bankName;
	private String accountNumber;
	
	// For delivery address
	private String country;
	private String state;
	private String city;
	private String pincode;
	private String street;
	private String buildingName;
	
	// For pickup
	private Long pickupLocationId;
	
	// Optional codes
	private String promocode;
	private String membershipCode;

}
