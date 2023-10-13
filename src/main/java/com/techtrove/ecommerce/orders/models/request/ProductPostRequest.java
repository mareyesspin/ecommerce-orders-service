package com.techtrove.ecommerce.orders.models.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPostRequest{
    private String productName;
    private String productSKU;
    private String productDetails;
    private Integer categoryId;
    private BigDecimal priceProduct;
    private Long stock;
}
