package com.itheima.hystrix.servicea;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class ServiceABootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ServiceABootstrap.class, args);
    }
}
