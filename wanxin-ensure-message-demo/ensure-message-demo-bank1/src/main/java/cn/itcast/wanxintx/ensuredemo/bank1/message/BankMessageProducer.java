package cn.itcast.wanxintx.ensuredemo.bank1.message;

import cn.itcast.wanxintx.ensuredemo.bank1.model.AccountChangeEvent;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class BankMessageProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendAccountChangeEvent(AccountChangeEvent accountChangeEvent) {
        //构造消息
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("accountChange",accountChangeEvent);
        Message<String> msg= MessageBuilder.withPayload( jsonObject.toJSONString()).build();

        //发送消息
        rocketMQTemplate.sendMessageInTransaction("producer_ensure_transfer",
                "topic_ensure_transfer",
                msg, null);

    }
}
