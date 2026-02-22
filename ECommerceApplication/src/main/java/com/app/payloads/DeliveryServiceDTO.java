package com.app.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryServiceDTO {
	
	private Long id;
	private String serviceName;
	private Integer deliveryCostPercentage;
	private Integer estimatedDeliveryDays;
}
