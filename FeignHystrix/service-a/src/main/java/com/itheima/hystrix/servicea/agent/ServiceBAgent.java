package com.itheima.hystrix.servicea.agent;

import feign.hystrix.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "feign-hystrix-service-b",fallbackFactory = ServiceBAgentHystrix.class)
public interface ServiceBAgent {
    @GetMapping("/service-b/service")
    String service();
}

@Component
class ServiceBAgentHystrix implements FallbackFactory<ServiceBAgent>{

    @Override
    public ServiceBAgent create(Throwable cause) {
        return new ServiceBAgent() {
            @Override
            public String service() {
                return "service-b熔断...";
            }
        };
    }
}