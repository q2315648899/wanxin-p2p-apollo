package cn.itcast.wanxintx.ensuredemo.bank1.message;

import cn.itcast.wanxintx.ensuredemo.bank1.dao.AccountInfoDao;
import cn.itcast.wanxintx.ensuredemo.bank1.model.AccountChangeEvent;
import cn.itcast.wanxintx.ensuredemo.bank1.service.AccountInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQTransactionListener(txProducerGroup = "producer_ensure_transfer")
public class TransferTransactionListenerImpl implements RocketMQLocalTransactionListener {

    @Autowired
    private AccountInfoService accountInfoService;

    @Autowired
    private AccountInfoDao accountInfoDao;

    /**
     * 发送prepare消息成功后回调该方法用于执行本地事务
     * @param msg 回传的消息，利用事务消息Id即可获取到该消息的唯一Id
     * @param arg 调用send方法时传递的参数，当send时候若有额外的参数可以传递到send方法中这里能获取到
     * @return 返回事务状态，COMMIT：提交 ROLLBACK：回滚 UNKNOW：未知，需要回查
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        //1.接收并解析消息
        final JSONObject jsonObject = JSON.parseObject(new String((byte[])
                msg.getPayload()));
        AccountChangeEvent accountChangeEvent =
                JSONObject.parseObject(jsonObject.getString("accountChange"),AccountChangeEvent.
                        class);

        //2.执行本地事务
        Boolean isCommit = true;
        try {
            accountInfoService.doUpdateAccountBalance(accountChangeEvent);
        }catch (Exception e){
            isCommit = false;
        }

        //3.返回执行结果
        if(isCommit){
            return RocketMQLocalTransactionState.COMMIT;
        }else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }


    /**
     * 如果Broker无法收到确认结果或收到UNKNOW事务状态，会执行事务回查
    * @param msg 通过获取事务消息Id来判断这条消息的本地事务执行状态
    * @return 返回事务状态，COMMIT：提交 ROLLBACK：回滚 UNKNOW：未知，需要回查
        */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        System.out.println("事务回查");
        //1.接收并解析消息
        final JSONObject jsonObject = JSON.parseObject(new String((byte[])
                msg.getPayload()));
        AccountChangeEvent accountChangeEvent =
                JSONObject.parseObject(jsonObject.getString("accountChange"),AccountChangeEvent.
                        class);

        //2.查询de_duplication表
        int isExistTx = accountInfoDao.isExistTx(accountChangeEvent.getTxNo());

        //3.根据查询结果返回值
        if(isExistTx>0){
            return RocketMQLocalTransactionState.COMMIT;
        }else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
