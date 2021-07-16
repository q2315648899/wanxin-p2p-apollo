package com.itheima.sleuth.serviceb;


import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringCloudApplication
@EnableFeignClients
public class ServiceBBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ServiceBBootstrap.class, args);
    }
}
