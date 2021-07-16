package com.itheima.sleuth.serviceb.agent;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "sleuth-with-zipkin-service-a")
public interface ServiceAAgent {
    @GetMapping("/service-a/service2")
    String service2();
}

