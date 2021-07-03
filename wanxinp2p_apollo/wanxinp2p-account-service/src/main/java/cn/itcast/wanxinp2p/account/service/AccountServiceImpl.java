package cn.itcast.wanxinp2p.account.service;

import cn.itcast.wanxinp2p.account.common.AccountErrorCode;
import cn.itcast.wanxinp2p.account.entity.Account;
import cn.itcast.wanxinp2p.account.mapper.AccountMapper;
import cn.itcast.wanxinp2p.api.account.model.AccountDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountLoginDTO;
import cn.itcast.wanxinp2p.api.account.model.AccountRegisterDTO;
import cn.itcast.wanxinp2p.common.domain.BusinessException;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import cn.itcast.wanxinp2p.common.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Author: wong
 * @Date: 2021/6/28
 */
@Service
@Slf4j
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
     *
     * @param accountRegisterDTO 注册信息
     * @return
     */
    //这里需要注意DTO和DO的区别，请参考“万信金融p2p项目开发规范.pdf”
    @Override
    @Hmily(confirmMethod = "confirmRegister", cancelMethod = "cancelRegister")
    public AccountDTO register(AccountRegisterDTO accountRegisterDTO) {
        Account account = new Account();
        account.setUsername(accountRegisterDTO.getUsername());
        account.setMobile(accountRegisterDTO.getMobile());
        account.setPassword(PasswordUtil.generate(accountRegisterDTO.getPassword()));
        if (smsEnable) {
            account.setPassword(PasswordUtil.generate(accountRegisterDTO.getMobile()));
        }
        account.setDomain("c");
        if(accountRegisterDTO.getMobile().equals("110")){  //测试用
            throw new RuntimeException("我是故意的");
        }
        save(account);
        return convertAccountEntityToDTO(account);
    }

    public void confirmRegister(AccountRegisterDTO registerDTO) {
        log.info("execute confirmRegister");
    }

    public void cancelRegister(AccountRegisterDTO registerDTO) {
        log.info("execute cancelRegister");
        //删除账号
        remove(Wrappers.<Account>lambdaQuery().eq(Account::getUsername,
                registerDTO.getUsername()));
    }


    @Override
    public AccountDTO login(AccountLoginDTO accountLoginDTO) {
        //1.根据用户名和密码进行一次查询
        //2.先根据用户名进行查询，然后再比对密码（因为密码y经过加密存到数据库的，所以使用此方法）
        Account account = null;
        if (accountLoginDTO.getDomain().equalsIgnoreCase("c")) {
            //如果是c端用户，用户名就是手机号
            account = getAccountByMobile(accountLoginDTO.getMobile());
        } else {
            //如果是b端用户，用户名就是账号
            account = getAccountByUsername(accountLoginDTO.getUsername());
        }
        if (account == null) {
            throw new BusinessException(AccountErrorCode.E_130104);// 用户不存在
        }

        AccountDTO accountDTO = convertAccountEntityToDTO(account);
        if (smsEnable) { //如果为true,表示采用短信验证码登录，无需比较密码
            return accountDTO;
        }

        if (PasswordUtil.verify(accountLoginDTO.getPassword(), account.getPassword())) {
            return accountDTO;
        }

        throw new BusinessException(AccountErrorCode.E_130105);
    }

    /**
     * 根据手机获取账户信息
     *
     * @param mobile 手机号
     * @return 账户实体
     */
    private Account getAccountByMobile(String mobile) {
        return getOne(new QueryWrapper<Account>().lambda().eq(Account::getMobile, mobile));
    }

    /**
     * 根据用户名获取账户信息
     *
     * @param username 用户名
     * @return 账户实体
     */
    private Account getAccountByUsername(String username) {
        return getOne(new QueryWrapper<Account>().lambda().eq(Account::getUsername, username));
    }

    /**
     * entity转为dto
     *
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
