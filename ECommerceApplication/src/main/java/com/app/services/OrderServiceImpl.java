package com.app.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.app.entites.Address;
import com.app.entites.Cart;
import com.app.entites.CartItem;
import com.app.entites.Order;
import com.app.entites.OrderItem;
import com.app.entites.Payment;
import com.app.entites.Product;
import com.app.exceptions.APIException;
import com.app.exceptions.ResourceNotFoundException;
import com.app.payloads.OrderDTO;
import com.app.payloads.OrderItemDTO;
import com.app.payloads.OrderRequestDTO;
import com.app.payloads.OrderResponse;
import com.app.repositories.AddressRepo;
import com.app.repositories.CartItemRepo;
import com.app.repositories.CartRepo;
import com.app.repositories.DeliveryServiceRepo;
import com.app.repositories.OrderItemRepo;
import com.app.repositories.OrderRepo;
import com.app.repositories.PaymentRepo;
import com.app.repositories.UserRepo;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class OrderServiceImpl implements OrderService {

	private final Map<String, Integer> membershipCodes = new HashMap<>() {{
		put("BRONZE10", 10);
		put("SILVER15", 15);
		put("GOLD20", 20);
		put("PLATINUM25", 25);
		put("VIP30", 30);
	}};

	// HashMap untuk menyimpan daftar bank dan nomor rekening
	private final Map<String, String> bankAccounts = new HashMap<>() {{
		put("bca", "079123455123");
		put("jago", "123907412812");
		put("bri", "8241263128142");
		put("bni", "9123974123142");
		put("mandiri", "1123876423142");
	}};

	// HashMap untuk menyimpan kode promo dan persentase diskon (1-100)
	private final Map<String, Integer> promoCodes = new HashMap<>() {{
		put("DISKON10", 10);
		put("HEMAT20", 20);
		put("PROMO15", 15);
		put("SAVE25", 25);
		put("SPECIAL30", 30);
	}};

	@Autowired
	public UserRepo userRepo;

	@Autowired
	public AddressRepo addressRepo;

	@Autowired
	public CartRepo cartRepo;

	@Autowired
	public OrderRepo orderRepo;

	@Autowired
	private PaymentRepo paymentRepo;

	@Autowired
	public OrderItemRepo orderItemRepo;

	@Autowired
	public CartItemRepo cartItemRepo;

	@Autowired
	public DeliveryServiceRepo deliveryServiceRepo;

	@Autowired
	public UserService userService;

	@Autowired
	public CartService cartService;

	@Autowired
	public ModelMapper modelMapper;

	public Map<String, String> getBankAccounts() {
		return new HashMap<>(bankAccounts);
	}

	public Map<String, Integer> getPromoCodes() {
		return new HashMap<>(promoCodes);
	}

	@Override
	public OrderDTO placeOrder(String email, Long cartId, String paymentMethod, OrderRequestDTO orderRequest) {
		Cart cart = cartRepo.findCartByEmailAndCartId(email, cartId);

		if (cart == null) {
			throw new ResourceNotFoundException("Cart", "cartId", cartId);
		}

		// Validasi bahwa cart tidak kosong
		List<CartItem> cartItems = cart.getCartItems();
		if (cartItems == null || cartItems.size() == 0) {
			throw new APIException("Cart is empty");
		}

		Double totalAmount;
		Integer discountPercentage = 0;
		com.app.entites.DeliveryService deliveryService = null;
		String shippingMethod = "pickup"; // Default shipping method

		// Handle bank transfer payment validations
		if ("bankTransfer".equalsIgnoreCase(paymentMethod)) {
			// Validasi nama bank dan nomor rekening
			if (orderRequest.getBankName() == null || orderRequest.getBankName().trim().isEmpty()) {
				throw new APIException("Bank name is required for bank transfer");
			}
			if (orderRequest.getAccountNumber() == null || orderRequest.getAccountNumber().trim().isEmpty()) {
				throw new APIException("Account number is required for bank transfer");
			}

			String normalizedBankName = orderRequest.getBankName().toLowerCase().trim();
			if (!bankAccounts.containsKey(normalizedBankName)) {
				throw new APIException("Bank " + orderRequest.getBankName() + " is not supported. Supported banks: " + bankAccounts.keySet());
			}

			String expectedAccountNumber = bankAccounts.get(normalizedBankName);
			if (!expectedAccountNumber.equals(orderRequest.getAccountNumber().trim())) {
				throw new APIException("Invalid account number for bank " + orderRequest.getBankName());
			}
		}

		// Handle delivery service - independent of payment method
		// Check if delivery service is requested (by looking at deliveryServiceId)
		if (orderRequest.getDeliveryServiceId() != null) {
			shippingMethod = "delivery";
			
			// Validate address fields for delivery
			if (orderRequest.getCountry() == null || orderRequest.getCountry().trim().isEmpty()) {
				throw new APIException("Country is required for delivery orders");
			}
			if (orderRequest.getState() == null || orderRequest.getState().trim().isEmpty()) {
				throw new APIException("State is required for delivery orders");
			}
			if (orderRequest.getCity() == null || orderRequest.getCity().trim().isEmpty()) {
				throw new APIException("City is required for delivery orders");
			}
			if (orderRequest.getPincode() == null || orderRequest.getPincode().trim().isEmpty()) {
				throw new APIException("Pincode is required for delivery orders");
			}
			if (orderRequest.getStreet() == null || orderRequest.getStreet().trim().isEmpty()) {
				throw new APIException("Street is required for delivery orders");
			}
			if (orderRequest.getBuildingName() == null || orderRequest.getBuildingName().trim().isEmpty()) {
				throw new APIException("Building name is required for delivery orders");
			}

			// Get delivery service
			deliveryService = deliveryServiceRepo.findById(orderRequest.getDeliveryServiceId())
					.orElseThrow(() -> new ResourceNotFoundException("DeliveryService", "id", orderRequest.getDeliveryServiceId()));
		}

		// Calculate total amount based on promocode, membership code, or use cart price
		boolean isPromoApplied = orderRequest.getPromocode() != null && !orderRequest.getPromocode().trim().isEmpty();
		boolean isMembershipApplied = orderRequest.getMembershipCode() != null && !orderRequest.getMembershipCode().trim().isEmpty();

		if (isPromoApplied) {
			// Jika ada promocode: hitung dari harga asli produk
			String normalizedPromoCode = orderRequest.getPromocode().trim().toUpperCase();
			if (!promoCodes.containsKey(normalizedPromoCode)) {
				throw new APIException("Invalid promo code: " + orderRequest.getPromocode() + ".");
			}
			discountPercentage = promoCodes.get(normalizedPromoCode);

			// Hitung total dari harga asli (bukan harga diskon)
			totalAmount = 0.0;
			for (CartItem item : cartItems) {
				double originalPrice = item.getProduct().getPrice(); // Harga asli
				totalAmount += originalPrice * item.getQuantity();
			}

			// Terapkan diskon promo ke total harga asli
			totalAmount = totalAmount - (totalAmount * discountPercentage / 100.0);
		} else if (isMembershipApplied) {
			// Jika ada membership code: hitung dari harga asli produk
			String normalized = orderRequest.getMembershipCode().trim().toUpperCase();

			if (!membershipCodes.containsKey(normalized)) {
				throw new APIException("Invalid membership code");
			}

			discountPercentage = membershipCodes.get(normalized);

			totalAmount = 0.0;

			for (CartItem item : cartItems) {
				double originalPrice = item.getProduct().getPrice();
				totalAmount += originalPrice * item.getQuantity();
			}

			totalAmount -= totalAmount * discountPercentage / 100.0;
		} else {
			// Jika tidak ada promocode atau membership: gunakan harga diskon produk dari cart
			totalAmount = cart.getTotalPrice();
		}

		// Add delivery cost if delivery method
		if (deliveryService != null) {
			double deliveryCost = totalAmount * deliveryService.getDeliveryCostPercentage() / 100.0;
			totalAmount += deliveryCost;
		}

		// Create order
		Order order = new Order();
		order.setEmail(email);
		order.setOrderDate(LocalDate.now());
		order.setTotalAmount(totalAmount);
		order.setOrderStatus("Order Accepted !");
		order.setShippingMethod(shippingMethod);

		// Set delivery service and address if delivery is used
		if (deliveryService != null) {
			order.setDeliveryService(deliveryService);
			order.setCountry(orderRequest.getCountry());
			order.setState(orderRequest.getState());
			order.setCity(orderRequest.getCity());
			order.setPincode(orderRequest.getPincode());
			order.setStreet(orderRequest.getStreet());
			order.setBuildingName(orderRequest.getBuildingName());
		}

		// Create payment
		Payment payment = new Payment();
		payment.setOrder(order);
		payment.setPaymentMethod(paymentMethod);

		// Save bank transfer details if applicable
		if ("bankTransfer".equalsIgnoreCase(paymentMethod)) {
			payment.setBankName(orderRequest.getBankName().toLowerCase().trim());
			payment.setAccountNumber(orderRequest.getAccountNumber().trim());
			if (orderRequest.getPromocode() != null && !orderRequest.getPromocode().trim().isEmpty()) {
				payment.setPromocode(orderRequest.getPromocode().trim().toUpperCase());
			}
		}

		// Handle address for payment (if address is provided for non-delivery methods)
		if (orderRequest.getCountry() != null && orderRequest.getState() != null) {
			List<Address> addresses = addressRepo.findAllByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
					orderRequest.getCountry(), orderRequest.getState(), orderRequest.getCity(), 
					orderRequest.getPincode(), orderRequest.getStreet(), orderRequest.getBuildingName()
			);

			Address address;
			if (addresses.isEmpty()) {
				address = new Address();
				address.setCountry(orderRequest.getCountry());
				address.setState(orderRequest.getState());
				address.setCity(orderRequest.getCity());
				address.setPincode(orderRequest.getPincode());
				address.setStreet(orderRequest.getStreet());
				address.setBuildingName(orderRequest.getBuildingName());
				address = addressRepo.save(address);
			} else {
				address = addresses.get(0);
			}
			payment.setShippingAddress(address);
		}

		payment = paymentRepo.save(payment);
		order.setPayment(payment);

		Order savedOrder = orderRepo.save(order);

		// Create order items
		List<OrderItem> orderItems = new ArrayList<>();

		for (CartItem cartItem : cartItems) {
			OrderItem orderItem = new OrderItem();

			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());

			if (isPromoApplied || isMembershipApplied) {
				orderItem.setDiscount(discountPercentage);
				orderItem.setOrderedProductPrice(cartItem.getProduct().getPrice());
			} else {
				orderItem.setDiscount(cartItem.getDiscount());
				orderItem.setOrderedProductPrice(cartItem.getProductPrice());
			}

			orderItem.setOrder(savedOrder);
			orderItems.add(orderItem);
		}

		orderItems = orderItemRepo.saveAll(orderItems);

		// Clear cart and update product quantities
		cart.getCartItems().forEach(item -> {
			int quantity = item.getQuantity();
			Product product = item.getProduct();
			cartService.deleteProductFromCart(cartId, item.getProduct().getProductId());
			product.setQuantity(product.getQuantity() - quantity);
		});

		// Prepare order DTO
		OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
		orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

		return orderDTO;
	}

	@Override
	public List<OrderDTO> getOrdersByUser(String email) {
		List<Order> orders = orderRepo.findAllByEmail(email);

		List<OrderDTO> orderDTOs = orders.stream().map(order -> modelMapper.map(order, OrderDTO.class))
				.collect(Collectors.toList());

		if (orderDTOs.size() == 0) {
			throw new APIException("No orders placed yet by the user with email: " + email);
		}

		return orderDTOs;
	}

	@Override
	public OrderDTO getOrder(String email, Long orderId) {

		Order order = orderRepo.findOrderByEmailAndOrderId(email, orderId);

		if (order == null) {
			throw new ResourceNotFoundException("Order", "orderId", orderId);
		}

		return modelMapper.map(order, OrderDTO.class);
	}

	@Override
	public OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

		Page<Order> pageOrders = orderRepo.findAll(pageDetails);

		List<Order> orders = pageOrders.getContent();

		List<OrderDTO> orderDTOs = orders.stream().map(order -> modelMapper.map(order, OrderDTO.class))
				.collect(Collectors.toList());
		
		if (orderDTOs.size() == 0) {
			throw new APIException("No orders placed yet by the users");
		}

		OrderResponse orderResponse = new OrderResponse();
		
		orderResponse.setContent(orderDTOs);
		orderResponse.setPageNumber(pageOrders.getNumber());
		orderResponse.setPageSize(pageOrders.getSize());
		orderResponse.setTotalElements(pageOrders.getTotalElements());
		orderResponse.setTotalPages(pageOrders.getTotalPages());
		orderResponse.setLastPage(pageOrders.isLast());
		
		return orderResponse;
	}

	@Override
	public OrderDTO updateOrder(String email, Long orderId, String orderStatus) {

		Order order = orderRepo.findOrderByEmailAndOrderId(email, orderId);

		if (order == null) {
			throw new ResourceNotFoundException("Order", "orderId", orderId);
		}

		order.setOrderStatus(orderStatus);

		return modelMapper.map(order, OrderDTO.class);
	}

}
