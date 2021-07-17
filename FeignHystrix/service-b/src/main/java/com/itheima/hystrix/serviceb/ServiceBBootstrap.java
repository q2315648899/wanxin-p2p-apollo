package com.itheima.hystrix.serviceb;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class ServiceBBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ServiceBBootstrap.class, args);
    }
}
