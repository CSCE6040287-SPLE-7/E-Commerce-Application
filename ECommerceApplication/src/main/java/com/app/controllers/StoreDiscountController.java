package com.app.controllers;

import java.util.List;

import com.app.payloads.StoreDiscountDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.entites.StoreDiscount;
import com.app.services.StoreDiscountService;

import jakarta.validation.Valid;

import static com.app.config.EndpointPath.*;

@RestController
@RequestMapping(APPEND_API_PATH)
@SecurityRequirement(name = "E-Commerce Application")
public class StoreDiscountController {

    @Autowired
    private StoreDiscountService storeDiscountService;

    // Add a new store discount
    @PostMapping(ADMIN_BASE_STORE_DISCOUNTS_PATH)
    public ResponseEntity<StoreDiscount> addStoreDiscount(@Valid @RequestBody StoreDiscountDTO storeDiscountDTO) {
        StoreDiscount addedStoreDiscount = storeDiscountService.addStoreDiscount(storeDiscountDTO);
        return new ResponseEntity<>(addedStoreDiscount, HttpStatus.CREATED);
    }

    // Get store discount by ID
    @GetMapping(PUBLIC_BASE_STORE_DISCOUNTS_PATH + APPEND_DISCOUNT_ID)
    public ResponseEntity<StoreDiscount> getStoreDiscountById(@PathVariable Long discountId) {
        StoreDiscount storeDiscount = storeDiscountService.getStoreDiscountById(discountId);
        return new ResponseEntity<>(storeDiscount, HttpStatus.OK);
    }

    // Get Store discount by name
    @GetMapping(PUBLIC_BASE_STORE_DISCOUNTS_PATH + APPEND_DISCOUNT_NAME)
    public ResponseEntity<StoreDiscount> getStoreDiscountByName(@PathVariable String name) {
        StoreDiscount storeDiscount = storeDiscountService.getStoreDiscountByName(name);
        return new ResponseEntity<>(storeDiscount, HttpStatus.OK);
    }

    // Update store discount
    @PutMapping(ADMIN_BASE_STORE_DISCOUNTS_PATH + APPEND_DISCOUNT_ID)
    public ResponseEntity<StoreDiscount> updateStoreDiscount(
            @PathVariable Long discountId, 
            @Valid @RequestBody StoreDiscountDTO storeDiscountDTO) {
        StoreDiscount updatedStoreDiscount = storeDiscountService.updateStoreDiscount(discountId, storeDiscountDTO);
        return new ResponseEntity<>(updatedStoreDiscount, HttpStatus.OK);
    }

    // Delete store discount
    @DeleteMapping(ADMIN_BASE_STORE_DISCOUNTS_PATH + APPEND_DISCOUNT_ID)
    public ResponseEntity<String> deleteStoreDiscount(@PathVariable Long discountId) {
        String message = storeDiscountService.deleteStoreDiscount(discountId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // Get currently active store discounts
    @GetMapping(PUBLIC_BASE_STORE_DISCOUNTS_PATH + APPEND_ACTIVE_DISCOUNTS_PATH)
    public ResponseEntity<List<StoreDiscount>> getCurrentlyActiveStoreDiscounts() {
        List<StoreDiscount> activeDiscounts = storeDiscountService.getCurrentlyActiveStoreDiscounts();
        return new ResponseEntity<>(activeDiscounts, HttpStatus.OK);
    }
}