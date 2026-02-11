package com.app.config;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.app.entites.Address;
import com.app.entites.Cart;
import com.app.entites.CartItem;
import com.app.entites.Category;
import com.app.entites.Order;
import com.app.entites.OrderItem;
import com.app.entites.Payment;
import com.app.entites.Product;
import com.app.entites.Role;
import com.app.entites.User;
import com.app.repositories.AddressRepo;
import com.app.repositories.CartItemRepo;
import com.app.repositories.CartRepo;
import com.app.repositories.CategoryRepo;
import com.app.repositories.OrderItemRepo;
import com.app.repositories.OrderRepo;
import com.app.repositories.PaymentRepo;
import com.app.repositories.ProductRepo;
import com.app.repositories.RoleRepo;
import com.app.repositories.UserRepo;
import com.github.javafaker.Faker;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataSeeder implements CommandLineRunner {

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private CategoryRepo categoryRepo;

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private AddressRepo addressRepo;

	@Autowired
	private CartRepo cartRepo;

	@Autowired
	private CartItemRepo cartItemRepo;

	@Autowired
	private PaymentRepo paymentRepo;

	@Autowired
	private OrderRepo orderRepo;

	@Autowired
	private OrderItemRepo orderItemRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final Faker faker = new Faker();
	private final Random random = new Random();

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		// Check if data already exists (check users instead of roles since roles are seeded by main app)
		if (userRepo.count() > 0) {
			log.info("Database already seeded. Skipping seeding process.");
			return;
		}

		log.info("Starting database seeding...");

		// 1. Get existing Roles (already seeded by ECommerceApplication)
		List<Role> roles = roleRepo.findAll();
		if (roles.isEmpty()) {
			log.error("Roles not found! Make sure ECommerceApplication has seeded roles first.");
			return;
		}
		log.info("Found {} existing roles", roles.size());

		// 2. Seed Categories
		List<Category> categories = seedCategories();
		log.info("Seeded {} categories", categories.size());

		// 3. Seed Addresses
		List<Address> addresses = seedAddresses(20);
		log.info("Seeded {} addresses", addresses.size());

		// 4. Seed Users with Roles and Addresses
		List<User> users = seedUsers(10, roles, addresses);
		log.info("Seeded {} users", users.size());

		// 5. Seed Carts for Users
		List<Cart> carts = seedCarts(users);
		log.info("Seeded {} carts", carts.size());

		// 6. Seed Products
		List<Product> products = seedProducts(50, categories);
		log.info("Seeded {} products", products.size());

		// 7. Seed Cart Items
		List<CartItem> cartItems = seedCartItems(carts, products);
		log.info("Seeded {} cart items", cartItems.size());

		// 8. Seed Payments
		List<Payment> payments = seedPayments(15);
		log.info("Seeded {} payments", payments.size());

		// 9. Seed Orders
		List<Order> orders = seedOrders(15, payments, users);
		log.info("Seeded {} orders", orders.size());

		// 10. Seed Order Items
		List<OrderItem> orderItems = seedOrderItems(orders, products);
		log.info("Seeded {} order items", orderItems.size());

		log.info("Database seeding completed successfully!");
		
		// Log sample credentials
		logSampleCredentials();
	}

	private List<Category> seedCategories() {
		List<Category> categories = new ArrayList<>();
		String[] categoryNames = {
			"Electronics", "Clothing", "Books", "Home & Kitchen",
			"Sports & Outdoors", "Toys & Games", "Beauty & Personal Care",
			"Automotive", "Health & Wellness", "Jewelry"
		};

		for (String name : categoryNames) {
			Category category = new Category();
			category.setCategoryName(name);
			categories.add(category);
		}

		return categoryRepo.saveAll(categories);
	}

	private List<Address> seedAddresses(int count) {
		List<Address> addresses = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			Address address = new Address();
			address.setStreet(faker.address().streetAddress());
			address.setBuildingName(faker.address().buildingNumber() + " " + faker.company().name() + " Building");
			address.setCity(faker.address().city());
			address.setState(faker.address().state());
			address.setCountry(faker.address().country());
			address.setPincode(faker.number().digits(6));
			addresses.add(address);
		}

		return addressRepo.saveAll(addresses);
	}

	private List<User> seedUsers(int count, List<Role> roles, List<Address> addresses) {
		List<User> users = new ArrayList<>();
		Role adminRole = roles.get(0);
		Role userRole = roles.get(1);

		// Create admin user with known credentials
		User admin = new User();
		admin.setFirstName("Admin");
		admin.setLastName("Administrator"); 
		admin.setEmail("admin@ecommerce.com");
		admin.setMobileNumber("1234567890");
		admin.setPassword(passwordEncoder.encode("Admin@123")); // Raw password: Admin@123
		Set<Role> adminRoles = new HashSet<>();
		adminRoles.add(adminRole);
		admin.setRoles(adminRoles);
		List<Address> adminAddresses = new ArrayList<>();
		adminAddresses.add(addresses.get(0));
		admin.setAddresses(adminAddresses);
		users.add(admin);

		// Create regular test user with known credentials
		User testUser = new User();
		testUser.setFirstName("TestUser");
		testUser.setLastName("Account");
		testUser.setEmail("user@ecommerce.com");
		testUser.setMobileNumber("9876543210");
		testUser.setPassword(passwordEncoder.encode("User@123")); // Raw password: User@123
		Set<Role> testUserRoles = new HashSet<>();
		testUserRoles.add(userRole);
		testUser.setRoles(testUserRoles);
		List<Address> testUserAddresses = new ArrayList<>();
		testUserAddresses.add(addresses.get(1));
		testUser.setAddresses(testUserAddresses);
		users.add(testUser);

		// Create random users
		for (int i = 2; i < count; i++) {
			User user = new User();
			
			// Generate firstName with proper validation (5-20 chars, letters only)
			String firstName = generateValidName(5, 20);
			String lastName = generateValidName(5, 20);
			
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(faker.internet().emailAddress());
			user.setMobileNumber(faker.number().digits(10));
			
			// Generate random password and encode it
			String rawPassword = "Pass@" + faker.number().numberBetween(1000, 9999);
			user.setPassword(passwordEncoder.encode(rawPassword));
			
			// Assign user role
			Set<Role> userRoles = new HashSet<>();
			userRoles.add(userRole);
			if (random.nextDouble() < 0.2) { // 20% chance to be admin
				userRoles.add(adminRole);
			}
			user.setRoles(userRoles);

			// Assign 1-3 addresses
			List<Address> userAddresses = new ArrayList<>();
			int addressCount = random.nextInt(3) + 1;
			for (int j = 0; j < addressCount && i + j < addresses.size(); j++) {
				userAddresses.add(addresses.get(i + j));
			}
			user.setAddresses(userAddresses);

			users.add(user);
		}

		return userRepo.saveAll(users);
	}

	/**
	 * Generate a valid name (letters only) with specified min and max length
	 */
	private String generateValidName(int minLength, int maxLength) {
		String name = faker.name().firstName().replaceAll("[^a-zA-Z]", "");
		
		// If too short, append letters until minimum length
		while (name.length() < minLength) {
			name += faker.lorem().characters(1, true, false).replaceAll("[^a-zA-Z]", "a");
		}
		
		// If too long, truncate to max length
		if (name.length() > maxLength) {
			name = name.substring(0, maxLength);
		}
		
		return name;
	}

	private List<Cart> seedCarts(List<User> users) {
		List<Cart> carts = new ArrayList<>();

		for (User user : users) {
			Cart cart = new Cart();
			cart.setUser(user);
			cart.setTotalPrice(0.0);
			carts.add(cart);
		}

		return cartRepo.saveAll(carts);
	}

	private List<Product> seedProducts(int count, List<Category> categories) {
		List<Product> products = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			Product product = new Product();
			product.setProductName(faker.commerce().productName());
			product.setDescription(faker.lorem().sentence(10));
			product.setImage("product_" + (i + 1) + ".jpg");
			product.setQuantity(faker.number().numberBetween(10, 500));
			
			double price = Double.parseDouble(faker.commerce().price().replace(",", ""));
			product.setPrice(price);
			
			double discount = random.nextDouble() * 30; // 0-30% discount
			product.setDiscount(discount);
			product.setSpecialPrice(price * (1 - discount / 100));

			// Assign random category
			product.setCategory(categories.get(random.nextInt(categories.size())));

			products.add(product);
		}

		return productRepo.saveAll(products);
	}

	private List<CartItem> seedCartItems(List<Cart> carts, List<Product> products) {
		List<CartItem> cartItems = new ArrayList<>();

		// Add items to some carts (about 60% of carts)
		for (Cart cart : carts) {
			if (random.nextDouble() < 0.6) {
				int itemCount = random.nextInt(5) + 1; // 1-5 items per cart
				double totalPrice = 0.0;

				for (int i = 0; i < itemCount; i++) {
					Product product = products.get(random.nextInt(products.size()));
					
					CartItem cartItem = new CartItem();
					cartItem.setCart(cart);
					cartItem.setProduct(product);
					cartItem.setQuantity(random.nextInt(5) + 1);
					cartItem.setDiscount(product.getDiscount());
					cartItem.setProductPrice(product.getSpecialPrice());
					
					totalPrice += cartItem.getProductPrice() * cartItem.getQuantity();
					cartItems.add(cartItem);
				}

				// Update cart total price
				cart.setTotalPrice(totalPrice);
				cartRepo.save(cart);
			}
		}

		return cartItemRepo.saveAll(cartItems);
	}

	private List<Payment> seedPayments(int count) {
		List<Payment> payments = new ArrayList<>();
		String[] paymentMethods = {
			"Credit Card", "Debit Card", "PayPal", "Bank Transfer", 
			"Cash on Delivery", "UPI Payment", "E-Wallet"
		};

		for (int i = 0; i < count; i++) {
			Payment payment = new Payment();
			payment.setPaymentMethod(paymentMethods[random.nextInt(paymentMethods.length)]);
			payments.add(payment);
		}

		return paymentRepo.saveAll(payments);
	}

	private List<Order> seedOrders(int count, List<Payment> payments, List<User> users) {
		List<Order> orders = new ArrayList<>();
		String[] orderStatuses = {
			"Pending", "Processing", "Shipped", "Delivered", "Cancelled"
		};

		for (int i = 0; i < count; i++) {
			Order order = new Order();
			
			// Assign random user email
			User user = users.get(random.nextInt(users.size()));
			order.setEmail(user.getEmail());
			
			// Random order date within last 90 days
			order.setOrderDate(
				faker.date()
					.past(90, TimeUnit.DAYS)
					.toInstant()
					.atZone(ZoneId.systemDefault())
					.toLocalDate()
			);
			
			order.setPayment(payments.get(i));
			order.setOrderStatus(orderStatuses[random.nextInt(orderStatuses.length)]);
			order.setTotalAmount(0.0); // Will be updated when adding order items
			
			orders.add(order);
		}

		return orderRepo.saveAll(orders);
	}

	private List<OrderItem> seedOrderItems(List<Order> orders, List<Product> products) {
		List<OrderItem> orderItems = new ArrayList<>();

		for (Order order : orders) {
			int itemCount = random.nextInt(5) + 1; // 1-5 items per order
			double totalAmount = 0.0;

			for (int i = 0; i < itemCount; i++) {
				Product product = products.get(random.nextInt(products.size()));
				
				OrderItem orderItem = new OrderItem();
				orderItem.setOrder(order);
				orderItem.setProduct(product);
				orderItem.setQuantity(random.nextInt(5) + 1);
				orderItem.setDiscount(product.getDiscount());
				orderItem.setOrderedProductPrice(product.getSpecialPrice());
				
				totalAmount += orderItem.getOrderedProductPrice() * orderItem.getQuantity();
				orderItems.add(orderItem);
			}

			// Update order total amount
			order.setTotalAmount(totalAmount);
			orderRepo.save(order);
		}

		return orderItemRepo.saveAll(orderItems);
	}

	private void logSampleCredentials() {
		log.info("=".repeat(80));
		log.info("SAMPLE USER CREDENTIALS FOR TESTING");
		log.info("=".repeat(80));
		log.info("");
		log.info("ADMIN ACCOUNT:");
		log.info("  Email: admin@ecommerce.com");
		log.info("  Password: Admin@123");
		log.info("");
		log.info("USER ACCOUNT:");
		log.info("  Email: user@ecommerce.com");
		log.info("  Password: User@123");
		log.info("");
		log.info("Note: Other randomly generated users have passwords in format: Pass@XXXX");
		log.info("      where XXXX is a random 4-digit number (1000-9999)");
		log.info("=".repeat(80));
	}
}
