package com.techtrove.ecommerce.orders.models.response;

import lombok.Data;

@Data
public class PaymentResponse {
    private String numberCard;
    private String typeCard;
}
