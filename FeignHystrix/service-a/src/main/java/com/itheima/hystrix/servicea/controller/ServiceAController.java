package com.itheima.hystrix.servicea.controller;


import com.itheima.hystrix.servicea.agent.ServiceBAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ServiceAController {

    @Autowired
    private ServiceBAgent serviceBAgent;

    @GetMapping("service")
    public String service(){
        String serviceb = serviceBAgent.service();
        return serviceb;
    }

}
