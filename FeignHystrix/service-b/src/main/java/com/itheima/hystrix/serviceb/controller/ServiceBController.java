package com.itheima.hystrix.serviceb.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ServiceBController {
    @GetMapping("service")
    public String service(){
        return "Service B is running";
    }
}
