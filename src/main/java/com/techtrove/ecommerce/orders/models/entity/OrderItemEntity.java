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
@Table(name="order_item")
public class OrderItemEntity extends EntityMasterAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "producto_id")
    private Long productoID;
    @Column(name = "produc_name")
    private String producName;
    @Column(name = "cantidad")
    private Integer cantidad;
    @Column(name = "subtotal")
    private BigDecimal subtotal;

}
