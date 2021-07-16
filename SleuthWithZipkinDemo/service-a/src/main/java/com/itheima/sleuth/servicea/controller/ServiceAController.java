package com.itheima.sleuth.servicea.controller;


import com.itheima.sleuth.servicea.agent.ServiceBAgent;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@RestController
public class ServiceAController {

    @Autowired
    private  ServiceBAgent serviceBAgent;

    @GetMapping("service1")
    public String service1(){
       String serviceb= serviceBAgent.service();
       return "service-a-service1" + "--->" + serviceb;
    }

    @GetMapping("service2")
    public String service2(){
        return "service-a-service2" ;
    }

}
