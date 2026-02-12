package com.app.config;

public class EndpointPath {

    public static final String APPEND_API_PATH = "/api";
    public static final String APPEND_ADMIN_PATH = "/admin";
    public static final String APPEND_PUBLIC_PATH = "/public";

    public static final String APPEND_STORE_DISCOUNTS_PATH = "/store-discounts";
    public static final String APPEND_DISCOUNT_ID = "/{discountId}";
    public static final String APPEND_DISCOUNT_NAME = "/name/{name}";
    public static final String APPEND_ACTIVE_DISCOUNTS_PATH = "/active";

    public static final String ADMIN_BASE_STORE_DISCOUNTS_PATH = APPEND_ADMIN_PATH + APPEND_STORE_DISCOUNTS_PATH;
    public static final String PUBLIC_BASE_STORE_DISCOUNTS_PATH = APPEND_PUBLIC_PATH + APPEND_STORE_DISCOUNTS_PATH;

}
