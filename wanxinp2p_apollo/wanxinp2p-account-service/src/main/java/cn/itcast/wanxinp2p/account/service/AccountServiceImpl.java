package cn.itcast.wanxinp2p.account.service;

import cn.itcast.wanxinp2p.account.entity.Account;
import cn.itcast.wanxinp2p.account.mapper.AccountMapper;
import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountRegisterDTO;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.common.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Author: wong
 * @Date: 2021/6/28
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private SmsService smsService;

    @Value("${sms.enable}")
    private Boolean smsEnable;

    @Override
    public RestResponse getSMSCode(String mobile) {
        return smsService.getSMSCode(mobile);
    }

    @Override
    public Integer checkMobile(String mobile, String key, String code) {
        smsService.verifySmsCode(key, code);
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        //wrapper.eq("mobile",mobile);
        wrapper.lambda().eq(Account::getMobile, mobile);
        int count = count(wrapper);
        return count > 0 ? 1 : 0;
    }

    /**
     * 账户注册
     * @param accountRegisterDTO 注册信息
     * @return
     */
    //这里需要注意DTO和DO的区别，请参考“万信金融p2p项目开发规范.pdf”
    @Override
    public AccountDTO register(AccountRegisterDTO accountRegisterDTO) {
        Account account=new Account();
        account.setUsername(accountRegisterDTO.getUsername());
        account.setMobile(accountRegisterDTO.getMobile());
        account.setPassword(PasswordUtil.generate(accountRegisterDTO.getPassword()));
        if(smsEnable){
            account.setPassword(PasswordUtil.generate(accountRegisterDTO.getMobile()));
        }
        account.setDomain("c");
        save(account);
        return convertAccountEntityToDTO(account);
    }

    /**
     * entity转为dto
     * @param entity
     * @return
     */
    private AccountDTO convertAccountEntityToDTO(Account entity) {
        if (entity == null) {
            return null;
        }
        AccountDTO dto = new AccountDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
