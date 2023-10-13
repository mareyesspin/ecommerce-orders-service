package com.techtrove.ecommerce.orders.repository;

import com.techtrove.ecommerce.orders.models.entity.OrdersEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrdersRepository extends CrudRepository<OrdersEntity, Long> {

    List<OrdersEntity> findAll();
}
