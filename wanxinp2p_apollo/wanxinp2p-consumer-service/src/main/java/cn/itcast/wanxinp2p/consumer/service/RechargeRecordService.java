package cn.itcast.wanxinp2p.consumer.service;

import cn.itcast.wanxinp2p.consumer.entity.RechargeRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户充值信息 服务类
 */
public interface RechargeRecordService extends IService<RechargeRecord> {

    /**
     * 根据请求流水号获取充值记录信息
     * @param requestNo
     * @return
     */
    RechargeRecord getByRequestNo(String requestNo);

    /**
     * 根据用户标识获取充值记录信息
     * @param consumerId
     * @return
     */
    RechargeRecord getByConsumerId(Long consumerId);
}
