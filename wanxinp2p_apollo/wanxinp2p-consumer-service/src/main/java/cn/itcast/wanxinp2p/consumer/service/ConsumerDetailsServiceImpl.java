package cn.itcast.wanxinp2p.consumer.service;

import cn.itcast.wanxinp2p.consumer.entity.ConsumerDetails;
import cn.itcast.wanxinp2p.consumer.mapper.ConsumerDetailsMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Author: wong
 * @Date: 2021/7/16
 */
@Service
public class ConsumerDetailsServiceImpl extends ServiceImpl<ConsumerDetailsMapper, ConsumerDetails> implements ConsumerDetailsService {
}
