package com.techtrove.ecommerce.orders.api;


import com.techtrove.ecommerce.core.models.dto.Transaction;
import com.techtrove.ecommerce.orders.models.request.ProductPutRequest;
import com.techtrove.ecommerce.orders.models.response.ProductResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiProductConstants {


    @Headers("Accept: application/json")
    @GET(value = "products/{id}")
    Call<ProductResponse> getProduct(@Path(value = "id") Long id);

    @Headers("Accept: application/json")
    @PUT(value = "products/{id}")
    Call<Transaction> updateProduct(@Body ProductPutRequest productPutRequest, @Path(value = "id") Long id);

}