package com.techtrove.ecommerce.orders.models.entity;

import com.techtrove.ecommerce.core.models.entity.EntityMasterAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="orders")
public class OrdersEntity extends EntityMasterAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderID;

    @Column(name = "send_adress")
    private String sendAdress;

    @Column(name = "total_items")
    private Integer totalItems;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "status")
    private String status;

}
