package com.app.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pickup_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupLocation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(min = 3, message = "Location name must contain at least 3 characters")
	@Column(nullable = false, unique = true)
	private String name;

	@NotBlank
	@Size(min = 4, max = 10, message = "Location code must be between 4 and 10 characters")
	@Column(nullable = false, unique = true)
	private String code;

	@NotBlank
	@Size(min = 10, message = "Address must contain at least 10 characters")
	@Column(nullable = false, length = 500)
	private String address;

}
