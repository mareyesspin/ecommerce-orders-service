package com.techtrove.ecommerce.orders.config;


import com.techtrove.ecommerce.core.utils.RetrofitBuild;
import com.techtrove.ecommerce.orders.api.ApiProductConstants;
import com.techtrove.ecommerce.orders.models.dto.OrdersProperties;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
@Getter
public class RetrofitConfig{

    private OrdersProperties ordersProperties;

    @Autowired
    public RetrofitConfig(OrdersProperties ordersProperties) {
        this.ordersProperties = ordersProperties;
    }

    @Bean
    public ApiProductConstants rbmProductService() throws NoSuchAlgorithmException, KeyManagementException {
        RetrofitBuild<ApiProductConstants> retrofitBuildModel = new RetrofitBuild<>();
        retrofitBuildModel.setApiKey("");
        retrofitBuildModel.setBaseURL(ordersProperties.urlProductService);
        retrofitBuildModel.setRepositoryClass(ApiProductConstants.class);
        return retrofitBuildModel.buildVendor();
    }



}

