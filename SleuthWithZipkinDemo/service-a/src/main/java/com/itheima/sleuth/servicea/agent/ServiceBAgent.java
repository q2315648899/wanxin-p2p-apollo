package com.itheima.sleuth.servicea.agent;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "sleuth-with-zipkin-service-b")
public interface ServiceBAgent {
    @GetMapping("/service-b/service")
    String service();
}

