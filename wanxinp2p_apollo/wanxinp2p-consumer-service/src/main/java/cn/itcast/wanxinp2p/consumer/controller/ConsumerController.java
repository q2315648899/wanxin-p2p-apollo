package cn.itcast.wanxinp2p.consumer.controller;

import cn.itcast.wanxinp2p.api.consumer.ConsumerAPI;
import cn.itcast.wanxinp2p.api.consumer.model.*;
import cn.itcast.wanxinp2p.api.depository.model.GatewayRequest;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.common.util.EncryptUtil;
import cn.itcast.wanxinp2p.consumer.common.SecurityUtil;
import cn.itcast.wanxinp2p.consumer.service.ConsumerService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@Api(value = "用户服务的Controller", tags = "Consumer", description = "用户服务API")
public class ConsumerController implements ConsumerAPI {

    @Value("${depository.url}")
    private String depositoryURL;

    @Value("${file.url}")
    private String fileURL;

    @Value("${my.file.origin}")
    private String origin;

    @Value("${my.file.bucket}")
    private String bucket;

    private OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();

    @Autowired
    private ConsumerService consumerService;

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
    @ApiOperation("用户注册")
    @ApiImplicitParam(name = "consumerRegisterDTO", value = "注册信息", required = true,
            dataType = "AccountRegisterDTO", paramType = "body")
    @PostMapping(value = "/consumers")
    public RestResponse register(@RequestBody ConsumerRegisterDTO consumerRegisterDTO) {
        consumerService.register(consumerRegisterDTO);
        return RestResponse.success();
    }

    @Override
    @ApiOperation("生成开户请求数据")
    @ApiImplicitParam(name = "consumerRequest", value = "开户信息", required = true,
            dataType = "ConsumerRequest", paramType = "body")
    @PostMapping("/my/consumers")
    public RestResponse<GatewayRequest> createConsumer(@RequestBody ConsumerRequest consumerRequest) {
        consumerRequest.setMobile(SecurityUtil.getUser().getMobile());
        return consumerService.createConsumer(consumerRequest);
    }

    @Override
    @ApiOperation("获取登录用户信息")
    @GetMapping("/l/currConsumer/{mobile}")
    public RestResponse<ConsumerDTO> getCurrConsumer(@PathVariable("mobile") String mobile) {
        ConsumerDTO consumerDTO = consumerService.getByMobile(mobile);
        return RestResponse.success(consumerDTO);
    }

    @Override
    @ApiOperation("获取登录用户信息")
    @GetMapping("/my/consumers")
    public RestResponse<ConsumerDTO> getMyConsumer() {
        ConsumerDTO consumerDTO = consumerService.getByMobile(SecurityUtil.getUser().getMobile());
        return RestResponse.success(consumerDTO);
    }

    @Override
    @ApiOperation("获取借款人用户信息")
    @ApiImplicitParam(name = "id", value = "用户标识", required = true,
            dataType = "Long", paramType = "path")
    @GetMapping("/my/borrowers/{id}")
    public RestResponse<BorrowerDTO> getBorrower(@PathVariable Long id) {
        return RestResponse.success(consumerService.getBorrower(id));
    }

    @Override
    @ApiOperation("获取借款人用户信息-供微服务访问")
    @ApiImplicitParam(name = "id", value = "用户标识", required = true,
            dataType = "Long", paramType = "path")
    @GetMapping("/l/borrowers/{id}")
    public RestResponse<BorrowerDTO> getBorrowerMobile(@PathVariable Long id) {
        return RestResponse.success(consumerService.getBorrower(id));
    }

    @Override
    @ApiOperation("获取用户可用余额")
    @ApiImplicitParam(name = "userNo", value = "用户编码", required = true,
            dataType = "String")
    @GetMapping("/l/balances/{userNo}")
    public RestResponse<BalanceDetailsDTO> getBalance(@PathVariable String userNo) {
        return getBalanceFromDepository(userNo);
    }

    @Override
    @GetMapping("/my/balances")
    public RestResponse<BalanceDetailsDTO> getMyBalance() {
        ConsumerDTO consumerDTO = consumerService
                .getByMobile(SecurityUtil.getUser().getMobile());
        return getBalanceFromDepository(consumerDTO.getUserNo());
    }

    @Override
    @ApiOperation("生成充值请求数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "amount", value = "金额", required = true,
                    dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "callbackUrl", value = "通知结果回调Url", required = true,
                    dataType = "String", paramType = "query")})
    @GetMapping("/my/recharge-records")
    public RestResponse<GatewayRequest> createRechargeRecord(@RequestParam String amount, @RequestParam String callbackUrl) {
        return consumerService.createRechargeRecord(amount, callbackUrl);
    }

    @Override
    @ApiOperation("生成用户提现数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "amount", value = "金额", required = true,
                    dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "callbackUrl", value = "通知结果回调Url",
                    required = true,
                    dataType = "String", paramType = "query")})
    @GetMapping("/my/withdraw-records")
    public RestResponse<GatewayRequest> createWithdrawRecord(@RequestParam String amount, @RequestParam String callbackUrl) {
        return consumerService.createWithdrawRecord(amount, callbackUrl);
    }

    @Override
    @ApiOperation("提交身份证图片给百度AI进行识别")
    @PostMapping("/my/imageRecognition")
    public RestResponse<Map<String, String>> imageRecognition(@RequestParam("file") MultipartFile file, String flag) throws IOException {
        return consumerService.imageRecognition(file, flag);
    }

    @Override
    @ApiOperation("申请获得上传凭证")
    @GetMapping("/my/applyUploadCertificate")
    public RestResponse<String> applyUploadCertificate() {
        String url = fileURL + "/generatetoken/?origin=" + origin;
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        ApplyCertificateRequest applyCertificateRequest = new ApplyCertificateRequest();
        applyCertificateRequest.setTokenType("1");//1表示获得上传凭证
        applyCertificateRequest.setScope(bucket);
        applyCertificateRequest.setDeadline(System.currentTimeMillis() / 1000 + 3600);//上传开始时间 + 3600s
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, JSON.toJSONString(applyCertificateRequest));
        RestResponse<String> restResponse;
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                restResponse = JSON.parseObject(responseBody,
                        new TypeReference<RestResponse<String>>() {
                        });
                return restResponse;
            }
        } catch (IOException e) {
            log.warn("调用文件服务{}获取凭证失败 ", url, e);
        }
        return RestResponse.validfail("获取凭证失败");
    }

    /**
     * 远程调用存管系统获取用户余额信息
     *
     * @param userNo 用户编码
     * @return
     */
    //不用大家编码实现，直接复制使用即可
    private RestResponse<BalanceDetailsDTO> getBalanceFromDepository(String userNo) {
        String url = depositoryURL + "/balance-details/" + userNo;
        BalanceDetailsDTO balanceDetailsDTO;
        Request request = new Request.Builder().url(url).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                balanceDetailsDTO = JSON.parseObject(responseBody,
                        BalanceDetailsDTO.class);
                return RestResponse.success(balanceDetailsDTO);
            }
        } catch (IOException e) {
            log.warn("调用存管系统{}获取余额失败 ", url, e);
        }
        return RestResponse.validfail("获取失败");
    }

    @ApiOperation("过网关受保护资源，进行认证拦截测试")
    @ApiImplicitParam(name = "jsonToken", value = "访问令牌", required = true,
            dataType = "String")
    @GetMapping(value = "/m/consumers/test")
    public RestResponse<String> testResources(String jsonToken) {
        return RestResponse.success(EncryptUtil.decodeUTF8StringBase64(jsonToken));
    }

    @Override
    @ApiOperation(value="保存用户详细信息", notes="主要存储身份证文件标识")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "consumerDetailsDTO", value = "用户详细信息",
                    dataType = "ConsumerDetailsDTO", paramType = "body")})
    @PostMapping("/my/saveConsumerDetails")
    public RestResponse<String> saveConsumerDetails(@RequestBody ConsumerDetailsDTO consumerDetailsDTO){
        consumerService.saveConsumerDetails(consumerDetailsDTO);
        return RestResponse.success("保存成功");
    }

}
