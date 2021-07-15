package cn.itcast.wanxinp2p.consumer.service;

import cn.itcast.wanxinp2p.consumer.entity.RechargeRecord;
import cn.itcast.wanxinp2p.consumer.mapper.RechargeRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RechargeRecordServiceImpl extends ServiceImpl<RechargeRecordMapper, RechargeRecord> implements RechargeRecordService {
}
