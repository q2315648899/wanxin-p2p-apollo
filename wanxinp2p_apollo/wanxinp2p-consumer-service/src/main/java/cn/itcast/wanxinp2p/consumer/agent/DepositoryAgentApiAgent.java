package cn.itcast.wanxinp2p.consumer.agent;

import cn.itcast.wanxinp2p.api.consumer.model.ConsumerRequest;
import cn.itcast.wanxinp2p.api.consumer.model.RechargeRequest;
import cn.itcast.wanxinp2p.api.depository.model.GatewayRequest;
import cn.itcast.wanxinp2p.api.depository.model.WithdrawRequest;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "depository-agent-service")
public interface DepositoryAgentApiAgent {
    @PostMapping("/depository-agent/l/consumers")
    RestResponse<GatewayRequest> createConsumer(@RequestBody ConsumerRequest consumerRequest);

    @PostMapping("/depository-agent/l/recharges")
    RestResponse<GatewayRequest> createRechargeRecord(@RequestBody RechargeRequest rechargeRequest);

    @PostMapping("/depository-agent/l/withdraw")
    RestResponse<GatewayRequest> createWithdrawRecord(@RequestBody WithdrawRequest withdrawRequest);
}
