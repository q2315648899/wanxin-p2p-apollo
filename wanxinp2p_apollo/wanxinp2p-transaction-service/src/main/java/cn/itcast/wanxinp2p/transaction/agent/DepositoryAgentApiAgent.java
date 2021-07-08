package cn.itcast.wanxinp2p.transaction.agent;

import cn.itcast.wanxinp2p.api.depository.model.UserAutoPreTransactionRequest;
import cn.itcast.wanxinp2p.api.transaction.model.ProjectDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 存管代理服务的代理
 * @Author: wong
 * @Date: 2021/7/6
 */
@FeignClient(value = "depository-agent-service")
public interface DepositoryAgentApiAgent {
    @PostMapping(value = "/depository-agent/l/createProject")
    public RestResponse<String> createProject(ProjectDTO projectDTO);

    @PostMapping("/depository-agent/l/user-auto-pre-transaction")
    RestResponse<String> userAutoPreTransaction(
            UserAutoPreTransactionRequest userAutoPreTransactionRequest);
}
