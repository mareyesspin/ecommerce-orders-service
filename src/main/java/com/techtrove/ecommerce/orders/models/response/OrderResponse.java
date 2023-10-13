package com.techtrove.ecommerce.orders.models.response;

import com.techtrove.ecommerce.core.models.response.MasterQueryResponse;
import com.techtrove.ecommerce.orders.models.request.OrderItemRequest;
import com.techtrove.ecommerce.orders.models.request.PaymentRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class OrderResponse extends MasterQueryResponse {

    private Long orderId;
    private String sendAdress;

    private Integer totalItems;
    private BigDecimal total;
    private String status;

    private List<OrderItemResponse> orderItems;

    private PaymentResponse payment;

}
