package com.app.controllers;

import com.app.config.AppConstants;
import com.app.payloads.OrderDTO;
import com.app.payloads.OrderResponse;
import com.app.payloads.PlaceOrderRequestDTO;
import com.app.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
@Tag(name = "Order Management", description = "APIs for managing orders and payments")
public class OrderController {
	
	@Autowired
	public OrderService orderService;
	
	@Operation(
		summary = "Get Bank Transfer Accounts",
		description = "Retrieve list of all supported banks and their account numbers for bank transfer payments"
	)
	@GetMapping("/public/users/bankTransfer")
	public ResponseEntity<Map<String, String>> getBankAccounts() {
		Map<String, String> bankAccounts = orderService.getBankAccounts();
		return new ResponseEntity<>(bankAccounts, HttpStatus.OK);
	}

	@Operation(
		summary = "Get Promo Codes",
		description = "Retrieve list of all available promo codes and their discount percentages"
	)
	@GetMapping("/admin/promoCodes")
	public ResponseEntity<Map<String, Integer>> getPromoCodes() {
		Map<String, Integer> promoCodes = orderService.getPromoCodes();
		return new ResponseEntity<>(promoCodes, HttpStatus.OK);
	}

	@Operation(
		summary = "Place Order with Bank Transfer",
		description = "Create a new order using bank transfer payment method. Supports delivery with address or pickup with pickup location."
	)
	@PostMapping("/public/users/{email}/carts/{cartId}/payments/bankTransfer/order")
	public ResponseEntity<OrderDTO> orderProducts(
			@Parameter(description = "User email address") @PathVariable String email,
			@Parameter(description = "Cart ID") @PathVariable Long cartId,
			@Valid @RequestBody PlaceOrderRequestDTO request) {
		request.setPaymentMethod("bankTransfer");
		OrderDTO order = orderService.placeOrder(email, cartId, request);

		return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);
    }
	
	@PostMapping("/public/users/{email}/carts/{cartId}/payments/{paymentMethod}/order")
	public ResponseEntity<OrderDTO> orderProducts(
			@PathVariable String email, 
			@PathVariable Long cartId, 
			@PathVariable String paymentMethod,
			@Valid @RequestBody PlaceOrderRequestDTO request) {
		request.setPaymentMethod(paymentMethod);
		OrderDTO order = orderService.placeOrder(email, cartId, request);

		return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);
	}

	@GetMapping("/admin/orders")
	public ResponseEntity<OrderResponse> getAllOrders(
			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
		
		OrderResponse orderResponse = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);

		return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.FOUND);
	}
	
	@GetMapping("public/users/{email}/orders")
	public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable String email) {
		List<OrderDTO> orders = orderService.getOrdersByUser(email);
		
		return new ResponseEntity<List<OrderDTO>>(orders, HttpStatus.FOUND);
	}
	
	@GetMapping("public/users/{email}/orders/{orderId}")
	public ResponseEntity<OrderDTO> getOrderByUser(@PathVariable String email, @PathVariable Long orderId) {
		OrderDTO order = orderService.getOrder(email, orderId);
		
		return new ResponseEntity<OrderDTO>(order, HttpStatus.FOUND);
	}
	
	@PutMapping("admin/users/{email}/orders/{orderId}/orderStatus/{orderStatus}")
	public ResponseEntity<OrderDTO> updateOrderByUser(@PathVariable String email, @PathVariable Long orderId, @PathVariable String orderStatus) {
		OrderDTO order = orderService.updateOrder(email, orderId, orderStatus);
		
		return new ResponseEntity<OrderDTO>(order, HttpStatus.OK);
	}

}
