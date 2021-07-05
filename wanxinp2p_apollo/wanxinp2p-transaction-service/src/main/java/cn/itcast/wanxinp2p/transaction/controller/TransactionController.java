package cn.itcast.wanxinp2p.transaction.controller;

import cn.itcast.wanxinp2p.api.transaction.TransactionApi;
import cn.itcast.wanxinp2p.api.transaction.model.ProjectDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wong
 * @Date: 2021/7/5
 */
@Api(value = "交易中心服务", tags = "transaction")
@RestController
public class TransactionController implements TransactionApi {

    @Override
    @ApiOperation("借款人发标")
    @ApiImplicitParam(name = "project", value = "标的信息", required = true,
            dataType = "Project", paramType = "body")
    @PostMapping("/my/projects")
    public RestResponse<ProjectDTO> createProject(@RequestBody ProjectDTO
                                                          projectDTO) {
        return null;
    }
}
