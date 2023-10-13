package com.techtrove.ecommerce.orders.models.request;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
public class OrderItemRequest {

    private Long orderItemId;
    private Long productoID;
    private String productDescription;
    private Integer cantidad;

}
