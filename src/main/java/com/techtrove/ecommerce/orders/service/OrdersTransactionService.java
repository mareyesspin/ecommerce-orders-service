package com.techtrove.ecommerce.orders.service;

import com.amazonaws.services.sqs.AmazonSQS;

import com.techtrove.ecommerce.core.enums.ParamsAuditEnum;
import com.techtrove.ecommerce.core.interfaces.TransaccionalService;
import com.techtrove.ecommerce.core.models.dto.ServiceModel;
import com.techtrove.ecommerce.core.models.dto.Transaction;
import com.techtrove.ecommerce.core.models.exception.ECommConflictException;
import com.techtrove.ecommerce.core.utils.DatesConverter;
import com.techtrove.ecommerce.core.utils.ParamsAuditUtils;
import com.techtrove.ecommerce.core.utils.RetrofitClient;
import com.techtrove.ecommerce.orders.api.ApiProductConstants;
import com.techtrove.ecommerce.orders.interfaces.ProductMapper;
import com.techtrove.ecommerce.orders.models.dto.OrdersProperties;

import com.techtrove.ecommerce.orders.models.entity.OrderItemEntity;
import com.techtrove.ecommerce.orders.models.entity.OrdersEntity;
import com.techtrove.ecommerce.orders.models.entity.PaymentEntity;
import com.techtrove.ecommerce.orders.models.request.OrderRequest;
import com.techtrove.ecommerce.orders.models.request.ProductPutRequest;
import com.techtrove.ecommerce.orders.models.response.OrderItemResponse;
import com.techtrove.ecommerce.orders.models.response.ProductResponse;
import com.techtrove.ecommerce.orders.repository.OrderItemRepository;
import com.techtrove.ecommerce.orders.repository.OrdersRepository;
import com.techtrove.ecommerce.orders.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@Qualifier("ordersTransactionService")
public class OrdersTransactionService implements TransaccionalService {

    public enum PROCESS {
        REGISTER_ORDER

    }

    private OrdersProperties ordersProperties;
    private AmazonSQS amazonSQS;
    private final OrdersRepository ordersRepository;

    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private ProductMapper productMapper;
    private RetrofitClient productServiceClient;
    private final ApiProductConstants apiProductConstants;


    public OrdersTransactionService(OrdersProperties ordersProperties,
                                    AmazonSQS amazonSQS,
                                    OrdersRepository ordersRepository,
                                    PaymentRepository paymentRepository,
                                    OrderItemRepository orderItemRepository,
                                    ProductMapper productMapper, RetrofitClient productServiceClient,
                                    ApiProductConstants apiProductConstants) {
        this.ordersProperties = ordersProperties;
        this.amazonSQS = amazonSQS;
        this.ordersRepository = ordersRepository;
        this.paymentRepository = paymentRepository;
        this.orderItemRepository = orderItemRepository;
        this.productMapper = productMapper;
        this.productServiceClient = productServiceClient;
        this.apiProductConstants = apiProductConstants;
    }





    /**
     *
     * @Author: Miguel A.R.S.
     * @Email: miguel.reyes@spinbyoxxo.com.mx
     * @Description: metodo para procesar cualquier proceso considerado como una transaccion.
     * @Date: 12/10/23
     * @param serviceModel
     * @return Optional<Transaction>
     */
    @Override
    public Optional<Transaction> procesaTransaccion(ServiceModel serviceModel) {
        Optional<Transaction> oTransaction;
        OrdersTransactionService.PROCESS processNumberEnum = OrdersTransactionService.PROCESS.valueOf(serviceModel.getProcessNumber());

        switch (processNumberEnum){
            case REGISTER_ORDER:
                    oTransaction = registerOrder(serviceModel);
                break;
            default:
                oTransaction = Optional.empty();
                break;
        }
        return oTransaction;
    }

    /**
     *
     * @Author: Miguel A.R.S.
     * @Email: miguel.reyes@spinbyoxxo.com.mx
     * @Description: Metodo para registrar una orden de compra
     * @Date: 12/10/23
     * @param serviceModel
     * @return Optional<Transaction>
     */

    @Transactional
    private Optional<Transaction> registerOrder(ServiceModel serviceModel ){
        Transaction transaction;

        OrderRequest orderRequest = serviceModel.getModel(OrderRequest.class);

        // convertimos el request a un OrderEntity
        OrdersEntity ordersEntity = productMapper.toOrdersEntity(orderRequest);
        ordersEntity.setStatus("PROCESO");
        ordersEntity.setTraceID(ParamsAuditUtils.getParamAuditFromMdc(ParamsAuditEnum.AUDIT_TRACE_ID));
        // damos de alta la orden
        final OrdersEntity ordersEntityResult = ordersRepository.save(ordersEntity);
        //convertimos el reuqest a un PaymentEntity
        PaymentEntity paymentEntity = productMapper.toPaymentEntity(orderRequest.getPayment());
        paymentEntity.setTraceID(ParamsAuditUtils.getParamAuditFromMdc(ParamsAuditEnum.AUDIT_TRACE_ID));
        paymentEntity.setOrderId(ordersEntityResult.getOrderID());
        paymentRepository.save(paymentEntity);

        //damos de alta los items
        List<OrderItemEntity> listItemsEntity= orderRequest.getOrderItems().stream()
                .map((item)->{
                    ProductResponse productResponse = getProduct(item.getProductoID());
                    if(productResponse.getStock() <= item.getCantidad()){
                        throw new ECommConflictException("Orden no procesada, el producto["
                        +productResponse.getProductName()+" no cuenta con suficiente stock");
                    }
                    // actualizamos el stock
                    ProductPutRequest productPutRequest = productMapper.toProductPutRequest(productResponse);
                    productPutRequest.setStock(item.getCantidad() * -1L);
                    actualizaStock(productResponse.getProductID(),productPutRequest);
                    // damos de alta el item
                    OrderItemEntity orderItemEntity = productMapper.toOrderItemEntity(item);
                    orderItemEntity.setOrderId(ordersEntityResult.getOrderID());
                    orderItemEntity.setProducName(productResponse.getProductName());
                    orderItemEntity.setSubtotal(productResponse.getPriceProduct().multiply(BigDecimal.valueOf(item.getCantidad())));
                    orderItemEntity.setTraceID(ParamsAuditUtils.getParamAuditFromMdc(ParamsAuditEnum.AUDIT_TRACE_ID));

                    orderItemRepository.save(orderItemEntity);
                    return orderItemEntity;
                }).collect(Collectors.toList());

         BigDecimal total = listItemsEntity.stream()
                .map((OrderItemEntity::getSubtotal))
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        ordersEntity.setTotal(total);
        ordersEntity.setTotalItems(listItemsEntity.size());
        ordersRepository.save(ordersEntity);


         transaction = new Transaction("message", "Su orden ha sido registrada, en unos minutos le enviaremos la confirmacion de su compra");
         transaction.setDetail("folioOrden",ordersEntityResult.getOrderID());
         transaction.setDetail("fechaOperacion", DatesConverter.getFechaServidorDate(DatesConverter.FORMATO.AA__MM_DD_Guion_Minutos));
         return Optional.of(transaction);

    }


    /**
     *
     * @Author: Miguel A.R.S.
     * @Email: miguel.reyes@spinbyoxxo.com.mx
     * @Description: consulta un producto
     * @Date: 12/10/23
     * @param productId
     * @return ProductResponse
     */
    private ProductResponse getProduct(Long productId){

        // primero se consulta el producto que se desea ordenar
        Call<ProductResponse> callEndpointGetProduct = apiProductConstants.getProduct(productId);
        // Invocamos al servicio
        return  productServiceClient.executeService(callEndpointGetProduct);

    }

    /**
     *
     * @Author: Miguel A.R.S.
     * @Email: miguel.reyes@spinbyoxxo.com.mx
     * @Description: Actualiza el stock de un producto
     * @Date: 12/10/23
     * @param productId
     * @param productPutRequest
     * @return Transaction
     */
    private Transaction actualizaStock(Long productId, ProductPutRequest productPutRequest){
        Call<Transaction>callUpdatePRoduct = apiProductConstants.updateProduct(productPutRequest,productId);
        return productServiceClient.executeService(callUpdatePRoduct);
    }


}

