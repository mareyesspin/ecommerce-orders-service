package com.techtrove.ecommerce.orders.controllers;



import com.techtrove.ecommerce.core.interfaces.ListService;
import com.techtrove.ecommerce.core.interfaces.QueryService;
import com.techtrove.ecommerce.core.interfaces.TransaccionalService;
import com.techtrove.ecommerce.core.models.dto.ServiceModel;
import com.techtrove.ecommerce.core.models.dto.Transaction;
import com.techtrove.ecommerce.core.models.exception.ECommResourceNotFoundException;
import com.techtrove.ecommerce.orders.models.request.OrderRequest;
import com.techtrove.ecommerce.orders.models.response.OrderResponse;
import com.techtrove.ecommerce.orders.service.OrdersListService;
import com.techtrove.ecommerce.orders.service.OrdersQueryService;
import com.techtrove.ecommerce.orders.service.OrdersTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
public class OrdersController {

    @Qualifier("ordersTransactionService")
    private final TransaccionalService productsTransactionService;

    @Qualifier("ordersQueryService")
    private final QueryService productsQueryService;
    @Qualifier("ordersListService")
    private final ListService productsListService;


    @Autowired
    public OrdersController(TransaccionalService productsTransactionService,
                            QueryService productsQueryService,
                            ListService productsListService) {

        this.productsTransactionService = productsTransactionService;
        this.productsQueryService = productsQueryService;
        this.productsListService = productsListService;
    }


    @PostMapping(value = "orders",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Transaction> productRegister(@RequestBody OrderRequest orderRequest){

        Optional<Transaction> oTransaction;
        ServiceModel serviceModel = new ServiceModel();
        serviceModel.setModel(orderRequest);
        serviceModel.setProcessNumber(OrdersTransactionService.PROCESS.REGISTER_ORDER.name());

        Transaction transaction = productsTransactionService.procesaTransaccion(serviceModel).get();
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }


    /**
     *
     * @Author: Miguel A.R.S.
     * @Email: miguel.reyes@spinbyoxxo.com.mx
     * @Description: End Point para consultar una orden en especifico
     * @Date: 12/10/23
     * @param id
     * @return ResponseEntity<OrderResponse>
     */

    @GetMapping(value = "orders/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id){

        OrderResponse orderResponse;
        ServiceModel serviceModel = new ServiceModel();
        serviceModel.setAditionalField("orderId", String.valueOf(id));
        serviceModel.setQueryNumber(OrdersQueryService.QUERYS.GET_ORDER_BY_ID.name());
        orderResponse = (OrderResponse)productsQueryService.query(serviceModel)
                .orElseThrow(()-> new ECommResourceNotFoundException("la orden  con id["+ id + "] no existe"));
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }


    /**
     *
     * @Author: Miguel A.R.S.
     * @Email: miguel.reyes@spinbyoxxo.com.mx
     * @Description: EndPoint que devuelve todas las ordenes
     * @Date: 12/10/23
     * @return ResponseEntity<List<ProductResponse>>
     */
    @GetMapping(value = "orders",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponse>> getAllOrders(){

        List<OrderResponse> orderResponseList;
        ServiceModel serviceModel = new ServiceModel();
        serviceModel.setListNumber(OrdersListService.LIST.GET_ALL_ORDERS.name());
        orderResponseList =(List<OrderResponse>) productsListService.list(serviceModel);

        return new ResponseEntity<>(orderResponseList, HttpStatus.OK);
    }













}
