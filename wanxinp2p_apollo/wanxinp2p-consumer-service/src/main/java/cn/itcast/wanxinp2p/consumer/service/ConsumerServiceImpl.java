package cn.itcast.wanxinp2p.consumer.service;

import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountRegisterDTO;
import cn.itcast.wanxinp2p.api.consumer.model.*;
import cn.itcast.wanxinp2p.api.depository.model.*;
import cn.itcast.wanxinp2p.common.domain.*;
import cn.itcast.wanxinp2p.common.util.CodeNoUtil;
import cn.itcast.wanxinp2p.common.util.IDCardUtil;
import cn.itcast.wanxinp2p.consumer.agent.AccountApiAgent;
import cn.itcast.wanxinp2p.consumer.agent.DepositoryAgentApiAgent;
import cn.itcast.wanxinp2p.consumer.common.ConsumerErrorCode;
import cn.itcast.wanxinp2p.consumer.common.SecurityUtil;
import cn.itcast.wanxinp2p.consumer.entity.*;
import cn.itcast.wanxinp2p.consumer.mapper.ConsumerMapper;
import com.baidu.aip.ocr.AipOcr;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@Slf4j
public class ConsumerServiceImpl extends ServiceImpl<ConsumerMapper, Consumer> implements ConsumerService {

    @Autowired
    private AccountApiAgent accountApiAgent;

    @Autowired
    private BankCardService bankCardService;

    @Autowired
    private DepositoryAgentApiAgent depositoryAgentApiAgent;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @Autowired
    private WithdrawRecordService withdrawRecordService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private AipOcr aipOcr;

    @Autowired
    private ConsumerDetailsService consumerDetailsService;

    @Override
    public Integer checkMobile(String mobile) {
        return getByMobile(mobile) != null ? 1 : 0;
    }

    @Override
    public ConsumerDTO getByMobile(String mobile) {
        Consumer consumer = getOne(new QueryWrapper<Consumer>().lambda().eq(Consumer::getMobile, mobile));
        return convertConsumerEntityToDTO(consumer);
    }

    @Override
    public BorrowerDTO getBorrower(Long id) {
        ConsumerDTO consumerDTO=get(id);
        BorrowerDTO borrowerDTO=new BorrowerDTO();
        BeanUtils.copyProperties(consumerDTO,borrowerDTO);

        Map<String,String> cardInfo= IDCardUtil.getInfo(borrowerDTO.getIdNumber());
        borrowerDTO.setAge(new Integer(cardInfo.get("age")));
        borrowerDTO.setBirthday(cardInfo.get("birthday"));
        borrowerDTO.setGender(cardInfo.get("gender"));
        return borrowerDTO;
    }

    private ConsumerDTO get(Long id) {
        Consumer entity = getById(id);
        if (entity == null) {
            log.info("id???{}????????????????????????", id);
            throw new BusinessException(ConsumerErrorCode.E_140101);
        }
        return convertConsumerEntityToDTO(entity);
    }

    @Override
    @Hmily(confirmMethod = "confirmRegister", cancelMethod = "cancelRegister")
    public void register(ConsumerRegisterDTO consumerRegisterDTO) {
        if (checkMobile(consumerRegisterDTO.getMobile()) == 1) {
            throw new BusinessException(ConsumerErrorCode.E_140107);
        }
        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(consumerRegisterDTO, consumer);
        consumer.setUsername(CodeNoUtil.getNo(CodePrefixCode.CODE_NO_PREFIX));
        consumerRegisterDTO.setUsername(consumer.getUsername());
        consumer.setUserNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        consumer.setIsBindCard(0);
        save(consumer);

        //????????????account
        AccountRegisterDTO accountRegisterDTO = new AccountRegisterDTO();
        BeanUtils.copyProperties(consumerRegisterDTO, accountRegisterDTO);
        RestResponse<AccountDTO> restResponse = accountApiAgent.register(accountRegisterDTO);
        if (restResponse.getCode() != CommonErrorCode.SUCCESS.getCode()) {
            throw new BusinessException(ConsumerErrorCode.E_140106);
        }
    }

    @Override
    @Transactional
    public RestResponse<GatewayRequest> createConsumer(ConsumerRequest consumerRequest) {
        //1.????????????????????????????????????
        ConsumerDTO consumerDTO = getByMobile(consumerRequest.getMobile());
        if (consumerDTO.getIsBindCard() == 1) {
            throw new BusinessException(ConsumerErrorCode.E_140105);
        }

        //2.????????????????????????????????????????????????
        BankCardDTO bankCardDTO = bankCardService.getByCardNumber(consumerRequest.getCardNumber());
        if (bankCardDTO != null && bankCardDTO.getStatus() == StatusCode.STATUS_IN.getCode()) {
            throw new BusinessException(ConsumerErrorCode.E_140151);
        }

        //3.?????????????????????
        consumerRequest.setId(consumerDTO.getId());
        //????????????????????????????????????
        consumerRequest.setUserNo(CodeNoUtil.getNo(CodePrefixCode.CODE_CONSUMER_PREFIX));
        consumerRequest.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        //??????????????????????????????????????????
        UpdateWrapper<Consumer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(Consumer::getMobile, consumerDTO.getMobile());
        updateWrapper.lambda().set(Consumer::getUserNo, consumerRequest.getUserNo());
        updateWrapper.lambda().set(Consumer::getRequestNo, consumerRequest.getRequestNo());
        updateWrapper.lambda().set(Consumer::getFullname, consumerRequest.getFullname());
        updateWrapper.lambda().set(Consumer::getIdNumber, consumerRequest.getIdNumber());
        updateWrapper.lambda().set(Consumer::getAuthList, "ALL");
        update(updateWrapper);

        //4.?????????????????????
        BankCard bankCard = new BankCard();
        bankCard.setConsumerId(consumerDTO.getId());
        bankCard.setBankCode(consumerRequest.getBankCode());
        bankCard.setCardNumber(consumerRequest.getCardNumber());
        bankCard.setMobile(consumerRequest.getMobile());
        bankCard.setStatus(StatusCode.STATUS_OUT.getCode());
        BankCardDTO existBankCard = bankCardService.getByConsumerId(bankCard.getConsumerId());
        if (existBankCard != null) {
            bankCard.setId(existBankCard.getId());
        }
        bankCardService.saveOrUpdate(bankCard);

        //5.?????????????????????????????????????????????????????????????????????
        return depositoryAgentApiAgent.createConsumer(consumerRequest);
    }

    @Override
    @Transactional
    public Boolean modifyResult(DepositoryConsumerResponse response) {
        //1.????????????
        int status = DepositoryReturnCode.RETURN_CODE_00000.getCode().equals(response.getRespCode()) ? StatusCode.STATUS_IN.getCode() : StatusCode.STATUS_FAIL.getCode();
        //2.??????????????????
        Consumer consumer = getByRequestNo(response.getRequestNo());
        update(Wrappers.<Consumer>lambdaUpdate()
                .eq(Consumer::getId, consumer.getId())
                .set(Consumer::getIsBindCard, status)
                .set(Consumer::getStatus, status));
        //3.?????????????????????
        return bankCardService.update(Wrappers.<BankCard>lambdaUpdate()
                .eq(BankCard::getConsumerId, consumer.getId())
                .set(BankCard::getStatus, status)
                .set(BankCard::getBankCode, response.getBankCode())
                .set(BankCard::getBankName, response.getBankName()));
    }

    @Override
    @Transactional
    public Boolean modifyResult(DepositoryRechargeResponse response) {
        //1.????????????
        int status = DepositoryReturnCode.RETURN_CODE_00000.getCode().equals(response.getRespCode()) ? StatusCode.STATUS_IN.getCode() : StatusCode.STATUS_FAIL.getCode();
        //2.??????????????????
        RechargeRecord rechargeRecord = rechargeRecordService.getByRequestNo(response.getRequestNo());
        return rechargeRecordService.update(Wrappers.<RechargeRecord>lambdaUpdate()
                .eq(RechargeRecord::getId, rechargeRecord.getId())
                .set(RechargeRecord::getCallbackStatus, 1));
    }

    @Override
    @Transactional
    public Boolean modifyResult(DepositoryWithdrawResponse response) {
        //1.????????????
        int status = DepositoryReturnCode.RETURN_CODE_00000.getCode().equals(response.getRespCode()) ? StatusCode.STATUS_IN.getCode() : StatusCode.STATUS_FAIL.getCode();
        //2.??????????????????
        WithdrawRecord withdrawRecord = withdrawRecordService.getByRequestNo(response.getRequestNo());
        return withdrawRecordService.update(Wrappers.<WithdrawRecord>lambdaUpdate()
                .eq(WithdrawRecord::getId, withdrawRecord.getId())
                .set(WithdrawRecord::getCallbackStatus, 1));
    }

    @Override
    @Transactional
    public RestResponse<GatewayRequest> createRechargeRecord(String amount, String callbackUrl) {
        ConsumerDTO consumerDTO = getByMobile(SecurityUtil.getUser().getMobile());
        //1.??????????????????
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setAmount(new BigDecimal(amount));
        rechargeRecord.setConsumerId(consumerDTO.getId());
        rechargeRecord.setCreateDate(LocalDateTime.now());
        // ??????????????????????????????
        rechargeRecord.setUserNo(consumerDTO.getUserNo());
        rechargeRecord.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        rechargeRecord.setCallbackStatus(0);
        RechargeRecord existRechargeRecord = rechargeRecordService.getByConsumerId(rechargeRecord.getConsumerId());
        if (existRechargeRecord != null) {
            rechargeRecord.setId(existRechargeRecord.getId());
        }
        rechargeRecordService.saveOrUpdate(rechargeRecord);
        //2.??????????????????????????????
        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setId(consumerDTO.getId());
        rechargeRequest.setAmount(rechargeRecord.getAmount());
        rechargeRequest.setCallbackUrl(callbackUrl);
        rechargeRequest.setUserNo(rechargeRecord.getUserNo());
        rechargeRequest.setRequestNo(rechargeRecord.getRequestNo());
        //3.?????????????????????????????????????????????????????????????????????
        return depositoryAgentApiAgent.createRechargeRecord(rechargeRequest);
    }

    @Override
    @Transactional
    public RestResponse<GatewayRequest> createWithdrawRecord(String amount, String callbackUrl) {
        ConsumerDTO consumerDTO = getByMobile(SecurityUtil.getUser().getMobile());
        //1.??????????????????
        WithdrawRecord withdrawRecord = new WithdrawRecord();
        withdrawRecord.setAmount(new BigDecimal(amount));
        withdrawRecord.setConsumerId(consumerDTO.getId());
        withdrawRecord.setCreateDate(LocalDateTime.now());
        withdrawRecord.setCommission(new BigDecimal(amount).multiply(configService.getCommission()));
        // ??????????????????????????????
        withdrawRecord.setUserNo(consumerDTO.getUserNo());
        withdrawRecord.setRequestNo(CodeNoUtil.getNo(CodePrefixCode.CODE_REQUEST_PREFIX));
        withdrawRecord.setCallbackStatus(0);
        WithdrawRecord existWithdrawRecord = withdrawRecordService.getByConsumerId(withdrawRecord.getConsumerId());
        if (existWithdrawRecord != null) {
            withdrawRecord.setId(existWithdrawRecord.getId());
        }
        withdrawRecordService.saveOrUpdate(withdrawRecord);

        //2.??????????????????????????????
        //??????????????????????????????????????????
        BankCardDTO bankCardDTO = bankCardService.getByConsumerId(consumerDTO.getId());
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setId(consumerDTO.getId());
        withdrawRequest.setAmount(withdrawRecord.getAmount());
        withdrawRequest.setCallbackURL(callbackUrl);
        withdrawRequest.setUserNo(withdrawRecord.getUserNo());
        withdrawRequest.setRequestNo(withdrawRecord.getRequestNo());
        withdrawRequest.setCommission(withdrawRecord.getCommission());
        withdrawRequest.setCardNumber(bankCardDTO.getCardNumber());
        withdrawRequest.setMobile(bankCardDTO.getMobile());

        //3.?????????????????????????????????????????????????????????????????????
        return depositoryAgentApiAgent.createWithdrawRecord(withdrawRequest);
    }

    @Override
    public RestResponse<Map<String, String>> imageRecognition(MultipartFile file, String flag) throws IOException {
        // ??????????????????????????????
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("detect_direction", "true");
        options.put("detect_risk", "false");

        String idCardSide = flag;

        // ????????????????????????????????????
        //????????????
        byte[] bytes = file.getBytes();
        JSONObject res = aipOcr.idcard(bytes, idCardSide, options);
        System.out.println(res.toString(2));
        JSONObject words_result = res.getJSONObject("words_result");
        Iterator iterator = words_result.keys();
        Map<String, String> map = new HashMap<>();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            if ("??????".equals(key)) {
                JSONObject jsonObject = words_result.getJSONObject(key);

                map.put("idName", jsonObject.getString("words"));
            }
            if ("??????????????????".equals(key)) {
                JSONObject jsonObject = words_result.getJSONObject(key);

                map.put("idCard", jsonObject.getString("words"));
            }
        }
        System.out.println("map??????" + map);
        return RestResponse.success(map);
    }

    @Override
    public RestResponse<String> saveConsumerDetails(ConsumerDetailsDTO consumerDetailsDTO) {
        //1.???????????????????????????????????????????????????????????????
        //???????????????????????????????????????
        Consumer consumer = getOne(new QueryWrapper<Consumer>().lambda()
                .eq(Consumer::getIdNumber, consumerDetailsDTO.getIdNumber())
                .eq(Consumer::getFullname, consumerDetailsDTO.getFullname()));
        if (consumer == null) {
            throw new BusinessException(ConsumerErrorCode.E_140108);
        }

        ConsumerDetails consumerDetails = convertConsumerDetailsDTOToEntity(consumerDetailsDTO);
        consumerDetails.setConsumerId(consumer.getId());
        consumerDetailsService.save(consumerDetails);
        return RestResponse.success();
    }

    private Consumer getByRequestNo(String requestNo) {
        return getOne(Wrappers.<Consumer>lambdaQuery().eq(Consumer::getRequestNo, requestNo));
    }


    public void confirmRegister(ConsumerRegisterDTO consumerRegisterDTO) {
        log.info("execute confirmRegister");
    }

    public void cancelRegister(ConsumerRegisterDTO consumerRegisterDTO) {
        log.info("execute cancelRegister");
        remove(Wrappers.<Consumer>lambdaQuery().eq(Consumer::getMobile,
                consumerRegisterDTO.getMobile()));
    }

    /**
     * entity??????dto
     *
     * @param entity
     * @return
     **/
    private ConsumerDTO convertConsumerEntityToDTO(Consumer entity) {
        if (entity == null) {
            return null;
        }
        ConsumerDTO dto = new ConsumerDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    /**
     * dto??????entity
     *
     * @param consumerDetailsDTO
     * @return
     **/
    private ConsumerDetails convertConsumerDetailsDTOToEntity(ConsumerDetailsDTO consumerDetailsDTO) {
        if (consumerDetailsDTO == null) {
            return null;
        }
        ConsumerDetails consumerDetails = new ConsumerDetails();
        BeanUtils.copyProperties(consumerDetailsDTO, consumerDetails);
        return consumerDetails;
    }

}
