package cn.itcast.wanxintx.ensuredemo.bank2.service;

import cn.itcast.wanxintx.ensuredemo.bank2.dao.AccountInfoDao;
import cn.itcast.wanxintx.ensuredemo.bank2.model.AccountChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.listener.AbstractAuditListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

    @Autowired
    private AccountInfoDao accountInfoDao;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateAccountBalance(AccountChangeEvent accountChange) {
        System.out.println("bank2执行本地事务");
        int isExistsTx = accountInfoDao.isExistTx(accountChange.getTxNo());
        if(isExistsTx==0){
            accountInfoDao.updateAccountBalance(accountChange.getAccountNo(),accountChange.getAmount());
            accountInfoDao.addTx(accountChange.getTxNo());

        }
    }
}
