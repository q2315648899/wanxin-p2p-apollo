package com.itheima.sleuth.serviceb.controller;


import com.itheima.sleuth.serviceb.agent.ServiceAAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ServiceBController {

    @Autowired
    private ServiceAAgent serviceAAgent;

    @GetMapping("service")
    public String  service(){
        String service2=serviceAAgent.service2();
//        System.out.println(10/0);
        return "service-b-service"+"--->"+service2;
    }

}
