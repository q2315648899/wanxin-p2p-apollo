package cn.itcast.wanxinp2p.consumer.service;

import cn.itcast.wanxinp2p.consumer.entity.RechargeRecord;
import cn.itcast.wanxinp2p.consumer.mapper.RechargeRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RechargeRecordServiceImpl extends ServiceImpl<RechargeRecordMapper, RechargeRecord> implements RechargeRecordService {

    @Override
    public RechargeRecord getByRequestNo(String requestNo) {
        return getOne(Wrappers.<RechargeRecord>lambdaQuery().eq(RechargeRecord::getRequestNo, requestNo));
    }

    @Override
    public RechargeRecord getByConsumerId(Long consumerId) {
        RechargeRecord rechargeRecord = getOne(new QueryWrapper<RechargeRecord>().lambda().eq(RechargeRecord::getConsumerId, consumerId));
        return rechargeRecord;
    }
}
