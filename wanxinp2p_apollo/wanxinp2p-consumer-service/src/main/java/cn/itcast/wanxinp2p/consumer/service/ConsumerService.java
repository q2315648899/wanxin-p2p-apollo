package cn.itcast.wanxinp2p.consumer.service;

import cn.itcast.wanxinp2p.api.consumer.model.*;
import cn.itcast.wanxinp2p.api.depository.model.DepositoryConsumerResponse;
import cn.itcast.wanxinp2p.api.depository.model.DepositoryRechargeResponse;
import cn.itcast.wanxinp2p.api.depository.model.DepositoryWithdrawResponse;
import cn.itcast.wanxinp2p.api.depository.model.GatewayRequest;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.consumer.entity.Consumer;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface ConsumerService extends IService<Consumer> {
    /**
     * 检测用户是否存在
     * @param mobile
     * @return
     */
    Integer checkMobile(String mobile);
    /**
     * 用户注册
     * @param consumerRegisterDTO
     * @return
     */
    void register(ConsumerRegisterDTO consumerRegisterDTO);

    /**
     生成开户数据
     @param consumerRequest
     @return
     */
    RestResponse<GatewayRequest> createConsumer(ConsumerRequest consumerRequest);

    /**
     * 更新开户结果
     * @param response
     * @return
     */
    Boolean modifyResult(DepositoryConsumerResponse response);

    /**
     * 更新充值结果
     * @param response
     * @return
     */
    Boolean modifyResult(DepositoryRechargeResponse response);

    /**
     * 更新提现结果
     * @param response
     * @return
     */
    Boolean modifyResult(DepositoryWithdrawResponse response);

    /**
     * 通过手机号获取当前用户信息
     * @param mobile
     * @return
     */
    ConsumerDTO getByMobile(String mobile);

    /**
     * 获取借款人基本信息
     * @param id
     * @return
     */
    BorrowerDTO getBorrower(Long id);

    /**
     * 生成充值请求数据
     * @param amount
     * @param callbackUrl
     * @return
     */
    RestResponse<GatewayRequest> createRechargeRecord(String amount, String callbackUrl);

    /**
     * 生成提现请求数据
     * @param amount
     * @param callbackUrl
     * @return
     */
    RestResponse<GatewayRequest> createWithdrawRecord(String amount, String callbackUrl);

    /**
     * 提交身份证图片给百度AI进行识别
     *
     * @param file 被上传的文件
     * @param flag 身份证正反面 取值front 或 back
     * @return Map集合 识别成功后把身份证上的姓名和身份证号存到map中返回
     */
    RestResponse<Map<String, String>> imageRecognition(MultipartFile file, String flag) throws IOException;

    /**
     * 保存用户详细信息，主要存储身份证文件标识
     * @param consumerDetailsDTO 用户详细信息
     * @return
     */
    RestResponse<String> saveConsumerDetails(ConsumerDetailsDTO consumerDetailsDTO);

}