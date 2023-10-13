package com.techtrove.ecommerce.orders.service;

import com.techtrove.ecommerce.core.interfaces.ListService;
import com.techtrove.ecommerce.core.models.dto.ServiceModel;
import com.techtrove.ecommerce.core.models.exception.ECommResourceNotFoundException;
import com.techtrove.ecommerce.core.models.response.MasterQueryResponse;
import com.techtrove.ecommerce.orders.interfaces.ProductMapper;
import com.techtrove.ecommerce.orders.models.response.OrderItemResponse;
import com.techtrove.ecommerce.orders.models.response.OrderResponse;
import com.techtrove.ecommerce.orders.models.response.PaymentResponse;
import com.techtrove.ecommerce.orders.repository.OrderItemRepository;
import com.techtrove.ecommerce.orders.repository.OrdersRepository;
import com.techtrove.ecommerce.orders.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Qualifier("ordersListService")
public class OrdersListService implements ListService {




    public enum LIST {
        GET_ALL_ORDERS
    }

    private final OrdersRepository ordersRepository;

    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private ProductMapper productMapper;


    public OrdersListService(OrdersRepository ordersRepository,
                             PaymentRepository paymentRepository,
                             OrderItemRepository orderItemRepository,
                             ProductMapper productMapper) {
        this.ordersRepository = ordersRepository;
        this.paymentRepository = paymentRepository;
        this.orderItemRepository = orderItemRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<? extends MasterQueryResponse> list(ServiceModel serviceModel) {
        OrdersListService.LIST listEnum = OrdersListService.LIST.valueOf(serviceModel.getListNumber());

        if (listEnum == LIST.GET_ALL_ORDERS) {
            return getAllOrders(serviceModel);
        }
        return new ArrayList<>();
    }


    private List<OrderResponse> getAllOrders(ServiceModel serviceModel){

         return ordersRepository.findAll()
                .stream().map((ordersEntity) ->{
                     OrderResponse orderResponse1 = new OrderResponse();
                     orderResponse1.setOrderId(ordersEntity.getOrderID());
                     orderResponse1.setStatus(ordersEntity.getStatus());
                     orderResponse1.setTotal(ordersEntity.getTotal());
                     orderResponse1.setSendAdress(ordersEntity.getSendAdress());
                     orderResponse1.setTotalItems(ordersEntity.getTotalItems());

                     PaymentResponse paymentResponse = paymentRepository.findByOrderId(ordersEntity.getOrderID())
                             .map((paymentEntity) ->{
                                 PaymentResponse paymentResponse1 = new PaymentResponse();
                                 paymentResponse1.setNumberCard(paymentEntity.getNumberCard());
                                 paymentResponse1.setTypeCard(paymentEntity.getTypeCard());
                                 return paymentResponse1;
                             }).orElseThrow(()-> new ECommResourceNotFoundException("La orden con folio["+ ordersEntity.getOrderID() + "] no existe"));
                     orderResponse1.setPayment(paymentResponse);

                     // se obtienen los items
                     List<OrderItemResponse> listIems = orderItemRepository.findByorderId(ordersEntity.getOrderID())
                             .stream()
                             .map((orderItemEntity)->{
                                 OrderItemResponse orderItemResponse = new OrderItemResponse();
                                 orderItemResponse.setOrderItemId(orderItemEntity.getOrderItemId());
                                 orderItemResponse.setCantidad(orderItemEntity.getCantidad());
                                 orderItemResponse.setProductoID(orderItemEntity.getProductoID());
                                 orderItemResponse.setProductDescription(orderItemEntity.getProducName());
                                 orderItemResponse.setSubtotal(orderItemEntity.getSubtotal());
                                 return orderItemResponse;

                             }).collect(Collectors.toList());
                     orderResponse1.setOrderItems(listIems);

                        return orderResponse1;
                    }).collect(Collectors.toList());



    }

}
