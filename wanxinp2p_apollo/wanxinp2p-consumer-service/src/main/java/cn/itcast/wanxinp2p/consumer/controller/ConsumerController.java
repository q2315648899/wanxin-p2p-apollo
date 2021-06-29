package cn.itcast.wanxinp2p.consumer.controller;

import cn.itcast.wanxinp2p.api.consumer.ConsumerAPI;
import cn.itcast.wanxinp2p.api.consumer.model.ConsumerRegisterDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "用户服务的Controller", tags = "Consumer", description = "用户服务API")
public class ConsumerController implements ConsumerAPI {

    @ApiOperation("测试hello")
    @GetMapping(path = "/hello")
    public String hello() {
        return "hello";
    }

    @ApiOperation("测试hi")
    @PostMapping(path = "/hi")
    @ApiImplicitParam(name = "name", value = "姓名", required = true, dataType = "String")
    public String hi(String name) {
        return "hi," + name;
    }

    @Override
    public RestResponse register(@RequestBody ConsumerRegisterDTO consumerRegisterDTO) {
        return null;
    }
}
