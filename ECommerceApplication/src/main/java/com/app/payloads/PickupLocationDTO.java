package com.app.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickupLocationDTO {
	
	private Long id;
	private String name;
	private String code;
	private String address;

}
