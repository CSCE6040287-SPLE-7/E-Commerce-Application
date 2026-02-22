package com.app.services;

import java.util.List;

import com.app.payloads.PickupLocationDTO;

public interface PickupLocationService {
	
	List<PickupLocationDTO> getAllPickupLocations();

}
