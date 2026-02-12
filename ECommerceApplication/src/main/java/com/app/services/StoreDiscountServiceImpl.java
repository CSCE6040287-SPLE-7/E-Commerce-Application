package com.app.services;

import java.util.List;

import com.app.payloads.StoreDiscountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entites.StoreDiscount;
import com.app.exceptions.ResourceNotFoundException;
import com.app.repositories.StoreDiscountRepo;

import jakarta.transaction.Transactional;

import static com.app.helper.DiscountHelper.*;

@Transactional
@Service
public class StoreDiscountServiceImpl implements StoreDiscountService {

    @Autowired
    private StoreDiscountRepo storeDiscountRepo;

    @Override
    public StoreDiscount addStoreDiscount(StoreDiscountDTO storeDiscountDTO) {
        if (storeDiscountDTO.getStartDate().isAfter(storeDiscountDTO.getEndDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        StoreDiscount storeDiscount = mapStoreDiscountDtoToStoreDiscount(storeDiscountDTO);

        storeDiscount.setIsActive(isStoreDiscountValid(storeDiscount));
        
        return storeDiscountRepo.save(storeDiscount);
    }

    @Override
    public StoreDiscount getStoreDiscountById(Long discountId) {
        return storeDiscountRepo.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Store Discount", "discountId", discountId));
    }

    @Override
    public StoreDiscount getStoreDiscountByName(String name) {
        return storeDiscountRepo.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Store Discount", "name", name));
    }

    @Override
    public StoreDiscount updateStoreDiscount(Long discountId, StoreDiscountDTO storeDiscountDTO) {
        StoreDiscount existingDiscount = getStoreDiscountById(discountId);

        if (storeDiscountDTO.getStartDate().isAfter(storeDiscountDTO.getEndDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        return storeDiscountRepo.save(updatedStoreDiscountFromDto(existingDiscount, storeDiscountDTO));
    }

    @Override
    public String deleteStoreDiscount(Long discountId) {
        StoreDiscount storeDiscount = getStoreDiscountById(discountId);
        storeDiscountRepo.delete(storeDiscount);
        return "Store discount with ID " + discountId + " deleted successfully!";
    }

    @Override
    public List<StoreDiscount> getCurrentlyActiveStoreDiscounts() {
        return storeDiscountRepo.findCurrentlyActiveDiscounts();
    }
}