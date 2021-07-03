package cn.itcast.wanxinp2p.consumer.message;

import cn.itcast.wanxinp2p.api.depository.model.DepositoryConsumerResponse;
import cn.itcast.wanxinp2p.consumer.service.ConsumerService;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 消费者，用来接收存管代理服务的开户结果通知
 *
 * @Author: wong
 * @Date: 2021/7/3
 */
@Component
public class GatewayNotifyConsumer {

    @Autowired
    private ConsumerService consumerService;

    // 该构造函数两个注入的参数不可放到构造函数外作为当前类的属性。
    // 因为若把两个注入的参数放到构造函数外作为当前类的属性，当创建当前类的Bean实例时，
    // 会先初始化该构造函数再创建这两个类属性，但当初始化该构造函数使用这两个类属性，而此时类属性还未初始化，所以会出错。
    public GatewayNotifyConsumer(@Value("${rocketmq.consumer.group}") String consumerGroup,
                                 @Value("${rocketmq.name-server}") String mqNameServer) throws MQClientException {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(consumerGroup);
        defaultMQPushConsumer.setNamesrvAddr(mqNameServer);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        defaultMQPushConsumer.subscribe("TP_GATEWAY_NOTIFY_AGENT", "*");

        //注册监听器
        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                try {
                    Message message = msgs.get(0);
                    String topic = message.getTopic();
                    String tag = message.getTags();
                    String body = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("从存管代理服务那里发来信息：" + body);
                    if (tag.equals("PERSONAL_REGISTER")) {
                        DepositoryConsumerResponse response = JSON.parseObject(body, DepositoryConsumerResponse.class);
                        consumerService.modifyResult(response);
                    }
                    //if...
                } catch (Exception e) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;//尝试再次消费
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;//成功消费
            }
        });
        // 开始监听
        defaultMQPushConsumer.start();
    }
}
