package com.techtrove.ecommerce.orders.service;


import com.techtrove.ecommerce.core.interfaces.QueryService;
import com.techtrove.ecommerce.core.models.dto.ServiceModel;
import com.techtrove.ecommerce.core.models.exception.ECommResourceNotFoundException;
import com.techtrove.ecommerce.core.models.response.MasterQueryResponse;

import com.techtrove.ecommerce.core.utils.RetrofitClient;
import com.techtrove.ecommerce.orders.api.ApiProductConstants;
import com.techtrove.ecommerce.orders.interfaces.ProductMapper;
import com.techtrove.ecommerce.orders.models.response.OrderItemResponse;
import com.techtrove.ecommerce.orders.models.response.OrderResponse;
import com.techtrove.ecommerce.orders.models.response.PaymentResponse;
import com.techtrove.ecommerce.orders.repository.OrderItemRepository;
import com.techtrove.ecommerce.orders.repository.OrdersRepository;
import com.techtrove.ecommerce.orders.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Qualifier("ordersQueryService")
public class OrdersQueryService implements QueryService {



    public enum QUERYS {
        GET_ORDER_BY_ID,
    }


    private final OrdersRepository ordersRepository;

    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private ProductMapper productMapper;

    private RetrofitClient productServiceClient;
    private final ApiProductConstants apiProductConstants;


    public OrdersQueryService(OrdersRepository ordersRepository,
                              PaymentRepository paymentRepository,
                              OrderItemRepository orderItemRepository,
                              ProductMapper productMapper,
                              RetrofitClient productServiceClient,
                              ApiProductConstants apiProductConstants) {
        this.ordersRepository = ordersRepository;
        this.paymentRepository = paymentRepository;
        this.orderItemRepository = orderItemRepository;
        this.productMapper = productMapper;
        this.productServiceClient = productServiceClient;
        this.apiProductConstants = apiProductConstants;
    }
    @Override
    public Optional<? extends MasterQueryResponse> query(ServiceModel serviceModel) {

        OrdersQueryService.QUERYS querysEnum = OrdersQueryService.QUERYS.valueOf(serviceModel.getQueryNumber());

        if (querysEnum == QUERYS.GET_ORDER_BY_ID) {
            return Optional.of(getOrderById(serviceModel));
        }
        return Optional.empty();
    }



    private OrderResponse getOrderById(ServiceModel serviceModel){

        Long orderId = Long.valueOf(serviceModel.getAditionalField("orderId"));
        OrderResponse orderResponse = ordersRepository.findById(orderId)
                .map((ordersEntity) ->{
                    OrderResponse orderResponse1 = new OrderResponse();
                    orderResponse1.setOrderId(ordersEntity.getOrderID());
                    orderResponse1.setStatus(ordersEntity.getStatus());
                    orderResponse1.setTotal(ordersEntity.getTotal());
                    orderResponse1.setSendAdress(ordersEntity.getSendAdress());
                    orderResponse1.setTotalItems(ordersEntity.getTotalItems());


                    return  orderResponse1;
                }).orElseThrow(()-> new ECommResourceNotFoundException("La orden con folio["+ orderId + "] no existe"));
        // se consulta el Payment
        PaymentResponse paymentResponse = paymentRepository.findByOrderId(orderId)
                .map((paymentEntity) ->{
                    PaymentResponse paymentResponse1 = new PaymentResponse();
                    paymentResponse1.setNumberCard(paymentEntity.getNumberCard());
                    paymentResponse1.setTypeCard(paymentEntity.getTypeCard());
                    return paymentResponse1;
                }).orElseThrow(()-> new ECommResourceNotFoundException("La orden con folio["+ orderId + "] no existe"));

        // se obtienen los items
        List<OrderItemResponse> listIems = orderItemRepository.findByorderId(orderId)
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


        orderResponse.setOrderItems(listIems);
        orderResponse.setPayment(paymentResponse);
        return orderResponse;

    }


}
