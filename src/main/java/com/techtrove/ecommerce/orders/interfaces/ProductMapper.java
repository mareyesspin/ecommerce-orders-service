package com.techtrove.ecommerce.orders.interfaces;

import com.techtrove.ecommerce.orders.models.entity.OrderItemEntity;
import com.techtrove.ecommerce.orders.models.entity.OrdersEntity;
import com.techtrove.ecommerce.orders.models.entity.PaymentEntity;
import com.techtrove.ecommerce.orders.models.request.OrderItemRequest;
import com.techtrove.ecommerce.orders.models.request.OrderRequest;
import com.techtrove.ecommerce.orders.models.request.PaymentRequest;
import com.techtrove.ecommerce.orders.models.request.ProductPutRequest;
import com.techtrove.ecommerce.orders.models.response.ProductResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface ProductMapper {


    OrdersEntity toOrdersEntity(OrderRequest orderRequest);

    OrderItemEntity toOrderItemEntity(OrderItemRequest orderItemRequest);

    ProductPutRequest toProductPutRequest(ProductResponse productResponse);

    PaymentEntity toPaymentEntity(PaymentRequest paymentRequest);


}
