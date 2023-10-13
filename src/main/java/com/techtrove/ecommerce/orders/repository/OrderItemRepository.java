package com.techtrove.ecommerce.orders.repository;

import com.techtrove.ecommerce.orders.models.entity.OrderItemEntity;
import com.techtrove.ecommerce.orders.models.entity.OrdersEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository  extends CrudRepository<OrderItemEntity, Long> {

    List<OrderItemEntity> findByorderId(Long orderId);

}
