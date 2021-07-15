package cn.itcast.wanxinp2p.consumer.service;

import cn.itcast.wanxinp2p.consumer.entity.WithdrawRecord;
import cn.itcast.wanxinp2p.consumer.mapper.WithdrawRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class WithdrawRecordServiceImpl extends ServiceImpl<WithdrawRecordMapper, WithdrawRecord> implements WithdrawRecordService {

    @Override
    public WithdrawRecord getByRequestNo(String requestNo) {
        return getOne(Wrappers.<WithdrawRecord>lambdaQuery().eq(WithdrawRecord::getRequestNo, requestNo));
    }

    @Override
    public WithdrawRecord getByConsumerId(Long consumerId) {
        WithdrawRecord withdrawRecord = getOne(new QueryWrapper<WithdrawRecord>().lambda().eq(WithdrawRecord::getConsumerId, consumerId));
        return withdrawRecord;
    }
}
