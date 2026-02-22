package com.app.payloads;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
	
	private Long orderId;
	private String email;
	private List<OrderItemDTO> orderItems = new ArrayList<>();
	private LocalDate orderDate;
	private PaymentDTO payment;
	private DeliveryServiceDTO deliveryService;
	private String shippingMethod;
	private String country;
	private String state;
	private String city;
	private String pincode;
	private String street;
	private String buildingName;
	private Double totalAmount;
	private String orderStatus;

}
