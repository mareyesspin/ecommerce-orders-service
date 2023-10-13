package com.techtrove.ecommerce.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

import static com.techtrove.ecommerce.core.utils.DatesConverter.TIME_ZONE_DEFAULT;


@SpringBootApplication
@ComponentScan(basePackages = {"com.techtrove"})
public class OrdersServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(OrdersServiceApp.class, args);
    }
    @PostConstruct
    public void init(){
        // Setting Spring Boot  SetTimeZone.
        TimeZone tz = TimeZone.getTimeZone(TIME_ZONE_DEFAULT);
        TimeZone.setDefault(tz);
    }

}
