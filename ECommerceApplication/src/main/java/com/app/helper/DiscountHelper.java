package com.app.helper;

import com.app.entites.Product;
import com.app.entites.StoreDiscount;
import com.app.payloads.StoreDiscountDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class DiscountHelper {

    public static record EffectiveDiscountResult(double effectiveDiscount, double specialPrice) {
    }

    /**
     * Calculates the effective discount (%) and resulting special price for a product.
     * <p>
     * Priority: if a non-null storeDiscount is active for the current date it is used,
     * otherwise the product's own discount is used.
     *
     * @param product       product with price and fallback discount
     * @param storeDiscount optional store-level discount that may override product discount
     * @return EffectiveDiscountResult containing the applied discount percentage and computed special price
     */
    public static EffectiveDiscountResult calculateEffectiveDiscountAndSpecialPrice(Product product, StoreDiscount storeDiscount) {
        double effectiveDiscount;

        if (Objects.nonNull(storeDiscount) && isStoreDiscountValid(storeDiscount)) {
            effectiveDiscount = storeDiscount.getDiscountPercentage();
            double specialPrice = product.getPrice() - ((effectiveDiscount * 0.01) * product.getPrice());
            return new EffectiveDiscountResult(effectiveDiscount, specialPrice);
        } else {
            effectiveDiscount = product.getDiscount();
            return new EffectiveDiscountResult(effectiveDiscount, product.getSpecialPrice());
        }
    }

    public static boolean isStoreDiscountValid(StoreDiscount storeDiscount) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(storeDiscount.getStartDate()) && now.isBefore(storeDiscount.getEndDate());
    }

    public static StoreDiscount mapStoreDiscountDtoToStoreDiscount(StoreDiscountDTO storeDiscountDTO) {
        return StoreDiscount.builder()
            .name(storeDiscountDTO.getName())
            .discountPercentage(storeDiscountDTO.getDiscountPercentage())
            .startDate(storeDiscountDTO.getStartDate())
            .endDate(storeDiscountDTO.getEndDate())
            .isActive(storeDiscountDTO.getIsActive())
            .build();
    }

    public static StoreDiscount updatedStoreDiscountFromDto(StoreDiscount existingDiscount, StoreDiscountDTO storeDiscountDTO) {
        existingDiscount.setName(storeDiscountDTO.getName());
        existingDiscount.setDiscountPercentage(storeDiscountDTO.getDiscountPercentage());
        existingDiscount.setStartDate(storeDiscountDTO.getStartDate());
        existingDiscount.setEndDate(storeDiscountDTO.getEndDate());
        existingDiscount.setIsActive(storeDiscountDTO.getIsActive());
        return existingDiscount;
    }
}
