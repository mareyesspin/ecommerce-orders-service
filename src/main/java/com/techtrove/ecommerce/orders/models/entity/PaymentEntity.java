package com.techtrove.ecommerce.orders.models.entity;

import com.techtrove.ecommerce.core.models.entity.EntityMasterAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="payment")
public class PaymentEntity extends EntityMasterAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentID;

    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "number_card")
    private String numberCard;
    @Column(name = "type_card")
    private String typeCard;

}
