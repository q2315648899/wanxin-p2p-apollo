package com.itheima.msa.sentinel.sentineldemo;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestService {

    @SentinelResource(value = "hello",blockHandler = "exceptionHandler")
    public String hello(){
        return "Hello Sentinel";
    }

    public String exceptionHandler(BlockException ex){
        ex.printStackTrace();
        return "系统繁忙，请稍候";
    }

    @PostConstruct
    public void initFlowRules(){

        List<FlowRule> rules=new ArrayList<>();

        //封装限流规则
        FlowRule rule=new FlowRule();
        rule.setResource("hello");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);// 限流 基于QPS
        rule.setCount(2);

        rules.add(rule);

        //加载限流规则，使其启用
        FlowRuleManager.loadRules(rules);

    }

}
