package com.techtrove.ecommerce.orders.models.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private Long orderItemId;
    private Long productoID;
    private String productDescription;
    private Integer cantidad;
    private BigDecimal subtotal;

}
