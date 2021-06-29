package cn.itcast.wanxinp2p.account.service;

import cn.itcast.wanxinp2p.account.entity.Account;
import cn.itcast.wanxinp2p.common.domain.RestResponse;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author: wong
 * @Date: 2021/6/28
 */
public interface AccountService extends IService<Account> {

    /**
     * 获取手机验证码
     * @param mobile 手机号
     * @return
     */
    RestResponse getSMSCode(String mobile);

    /**
     * 校验手机号和验证码
     * @param mobile
     * @param key
     * @param code
     * @return
     */
    Integer checkMobile(String mobile,String key,String code);

}
