package cn.itcast.wanxinp2p.consumer.service;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <P>
 * 本类用于获取配置文件中的配置, 封装成service方便调用
 * </p>
 */
@Service
@EnableApolloConfig
public class ConfigService {
    @ApolloConfig
    private Config config;

    /**
     * 平台佣金（提现手续费率）
     */
    public BigDecimal getCommission() {
        return new BigDecimal(config.getProperty("commission", "0.0001"));
    }

}
