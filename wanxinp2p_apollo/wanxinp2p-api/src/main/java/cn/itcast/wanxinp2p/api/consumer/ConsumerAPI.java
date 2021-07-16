package cn.itcast.wanxinp2p.api.consumer;

import cn.itcast.wanxinp2p.api.consumer.model.*;
import cn.itcast.wanxinp2p.api.depository.model.GatewayRequest;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 用户中心接口API
 */
public interface ConsumerAPI {

    /**
     * 用户注册  保存用户信息
     * @param consumerRegisterDTO
     * @return
     */
   RestResponse register(ConsumerRegisterDTO consumerRegisterDTO);

    /**
     * 生成开户请求数据
     * @param consumerRequest 开户信息
     * @return
     */
    RestResponse<GatewayRequest> createConsumer(ConsumerRequest consumerRequest);

    /**
     * 获得当前登录用户
     * @return
     */
    RestResponse<ConsumerDTO> getCurrConsumer(String mobile);

    /**
     * 获取当前登录用户
     * @return
     */
    RestResponse<ConsumerDTO> getMyConsumer();

    /**
     * 获取借款人用户信息
     * @param id
     * @return
     */
    RestResponse<BorrowerDTO> getBorrower(Long id);

    /**
     * 获取借款人用户信息-供微服务访问
     * @param id 用户标识
     * @return
     */
    RestResponse<BorrowerDTO> getBorrowerMobile(Long id);

    /**
     获取当前登录用户余额信息
     @param userNo 用户编码
     @return
     */
    RestResponse<BalanceDetailsDTO> getBalance(String userNo);

    /**
     * 获取当前登录用户余额信息
     * @return
     */
    RestResponse<BalanceDetailsDTO> getMyBalance();

    /**
     * 生成充值请求数据
     * @param amount 充值金额
     * @param callbackUrl 回调地址
     * @return
     */
    RestResponse<GatewayRequest> createRechargeRecord(String amount, String callbackUrl);

    /**
     * 生成用户提现数据
     * @param amount 提现金额
     * @param callbackUrl 回调地址
     * @return
     */
    RestResponse<GatewayRequest> createWithdrawRecord(String amount, String callbackUrl);

    /**
     * 提交身份证图片给百度AI进行识别
     * @param file 被上传的文件
     * @param flag 身份证正反面 取值front 或 back
     * @return Map集合 识别成功后把身份证上的姓名和身份证号存到map中返回
     */
    RestResponse<Map<String, String>> imageRecognition(MultipartFile file, String flag) throws IOException;

    /**
     * 申请获得上传凭证
     * @return
     */
    RestResponse<String> applyUploadCertificate();

}
