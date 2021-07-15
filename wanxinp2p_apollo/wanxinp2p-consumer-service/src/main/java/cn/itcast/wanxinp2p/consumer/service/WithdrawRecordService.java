package cn.itcast.wanxinp2p.consumer.service;

import cn.itcast.wanxinp2p.consumer.entity.WithdrawRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户提现信息 服务类
 */
public interface WithdrawRecordService extends IService<WithdrawRecord> {

    /**
     * 根据请求流水号获取提现记录信息
     * @param requestNo
     * @return
     */
    WithdrawRecord getByRequestNo(String requestNo);

    /**
     * 根据用户标识获取提现记录信息
     * @param consumerId
     * @return
     */
    WithdrawRecord getByConsumerId(Long consumerId);
}
