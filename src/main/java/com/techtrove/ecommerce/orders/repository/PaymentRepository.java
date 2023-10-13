package com.techtrove.ecommerce.orders.repository;


import com.techtrove.ecommerce.orders.models.entity.PaymentEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PaymentRepository  extends CrudRepository<PaymentEntity, Long> {



    Optional<PaymentEntity>findByOrderId(Long orderId);
}
