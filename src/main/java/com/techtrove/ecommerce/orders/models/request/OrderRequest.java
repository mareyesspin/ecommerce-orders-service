package com.techtrove.ecommerce.orders.models.request;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {

    private String sendAdress;

    private Integer totalItems;
    private BigDecimal total;

    private List<OrderItemRequest> orderItems;

    private PaymentRequest payment;


}
