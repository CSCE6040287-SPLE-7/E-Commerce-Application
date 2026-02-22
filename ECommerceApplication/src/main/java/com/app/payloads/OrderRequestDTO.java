package com.app.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
	
	// Payment related
	private String bankName;
	private String accountNumber;
	private String promocode;
	private String membershipCode;
	
	// Delivery related (required when shippingMethod is "delivery")
	private Long deliveryServiceId;
	private String country;
	private String state;
	private String city;
	private String pincode;
	private String street;
	private String buildingName;
}
