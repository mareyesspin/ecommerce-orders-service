package com.techtrove.ecommerce.orders.models.request;

import lombok.Data;

import javax.persistence.Column;

@Data
public class PaymentRequest {

    private String numberCard;
    private String typeCard;
}
