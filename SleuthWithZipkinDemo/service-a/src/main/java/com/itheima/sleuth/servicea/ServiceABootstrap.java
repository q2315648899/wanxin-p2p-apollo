package com.itheima.sleuth.servicea;


import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringCloudApplication
@EnableFeignClients
public class ServiceABootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ServiceABootstrap.class, args);
    }
}
