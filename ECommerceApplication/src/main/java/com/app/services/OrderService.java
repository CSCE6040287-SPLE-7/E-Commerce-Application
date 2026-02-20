package com.app.services;

import java.util.List;
import java.util.Map;
import java.util.Map;

import com.app.payloads.OrderDTO;
import com.app.payloads.OrderResponse;

public interface OrderService {
	
	OrderDTO placeOrder(String email, Long cartId, String paymentMethod, String country, String state, String city, String pincode, String street, String buildingName, String membershipCode);

    Map<String, String> getBankAccounts();

	Map<String, Integer> getPromoCodes();

	OrderDTO placeOrder(String email, Long cartId, String paymentMethod, String bankName, String accountNumber, String promocode);

	OrderDTO getOrder(String email, Long orderId);
	
	List<OrderDTO> getOrdersByUser(String email);
	
	OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	OrderDTO updateOrder(String email, Long orderId, String orderStatus);
}
