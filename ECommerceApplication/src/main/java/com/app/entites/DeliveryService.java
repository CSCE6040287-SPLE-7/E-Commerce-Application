package com.app.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryService {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(min = 3, message = "Delivery service name must contain at least 3 characters")
	@Column(nullable = false, unique = true)
	private String serviceName;

	@Min(value = 3, message = "Delivery cost percentage must be at least 3%")
	@Max(value = 7, message = "Delivery cost percentage must not exceed 7%")
	@Column(nullable = false)
	private Integer deliveryCostPercentage;

	@Min(value = 1, message = "Estimated delivery days must be at least 1 day")
	@Max(value = 14, message = "Estimated delivery days must not exceed 14 days")
	@Column(nullable = false)
	private Integer estimatedDeliveryDays;
}
