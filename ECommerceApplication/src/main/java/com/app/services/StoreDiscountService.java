package com.app.services;

import java.util.List;

import com.app.entites.StoreDiscount;
import com.app.payloads.StoreDiscountDTO;

public interface StoreDiscountService {

    StoreDiscount addStoreDiscount(StoreDiscountDTO storeDiscountDTO);

    StoreDiscount getStoreDiscountById(Long discountId);

    StoreDiscount getStoreDiscountByName(String name);

    StoreDiscount updateStoreDiscount(Long discountId, StoreDiscountDTO storeDiscount);

    String deleteStoreDiscount(Long discountId);

    List<StoreDiscount> getCurrentlyActiveStoreDiscounts();
}