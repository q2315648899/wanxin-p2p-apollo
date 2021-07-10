package cn.itcast.wanxintx.ensuredemo.bank2.service;

import cn.itcast.wanxintx.ensuredemo.bank2.model.AccountChangeEvent;

public interface AccountInfoService {
    /**
     * 更新帐号余额
     * @param accountChange
     */
    void updateAccountBalance(AccountChangeEvent accountChange);
}
