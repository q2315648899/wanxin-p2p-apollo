package com.itheima.msa.sleuthdemo1;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("service1")
    public String node1(){
        log.info("Sleuth1_Service1");
        String sleuth2_service1 =restTemplate.getForObject("http://localhost:58012/sleuth2/service1",String.class);
        return "sleuth1_service1--->"+sleuth2_service1 ;
    }

    @GetMapping("service2")
    public String node2(){
        log.info("sleuth1_service2");
        return "sleuth1_service2";
    }

}
