package com.app.services;

import java.util.List;

import com.app.payloads.OrderDTO;
import com.app.payloads.OrderResponse;

public interface OrderService {
	
	OrderDTO placeOrder(String email, Long cartId, String paymentMethod, String country, String state, String city, String pincode, String street, String buildingName, String membershipCode);
	
	OrderDTO getOrder(String email, Long orderId);
	
	List<OrderDTO> getOrdersByUser(String email);
	
	OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	OrderDTO updateOrder(String email, Long orderId, String orderStatus);
}
