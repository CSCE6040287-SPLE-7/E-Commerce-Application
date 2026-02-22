package com.app.services;

import java.util.List;
import java.util.Map;

import com.app.payloads.OrderDTO;
import com.app.payloads.OrderResponse;
import com.app.payloads.PlaceOrderRequestDTO;

public interface OrderService {
	
	OrderDTO placeOrder(String email, Long cartId, PlaceOrderRequestDTO request);

    Map<String, String> getBankAccounts();

	Map<String, Integer> getPromoCodes();

	OrderDTO getOrder(String email, Long orderId);
	
	List<OrderDTO> getOrdersByUser(String email);
	
	OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	OrderDTO updateOrder(String email, Long orderId, String orderStatus);
}
