package cn.itcast.wanxinp2p.consumer;


import com.baidu.aip.ocr.AipOcr;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"org.dromara.hmily", "cn.itcast.wanxinp2p.consumer"})//等同于@SpringBootApplication加上@ComponentScan({"cn.itcast.wanxintx.hmilydemo.bank1","org.dromara.hmily"})
@EnableDiscoveryClient
@MapperScan("cn.itcast.wanxinp2p.consumer.mapper") //设置mapper接口的扫描包
@EnableFeignClients(basePackages = {"cn.itcast.wanxinp2p.consumer.agent"})
public class ConsumerService {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerService.class, args);
    }

    /**
     * 创建AipOcr对象Bean
     * @param appId 你的 App ID
     * @param apiKey 你的 Api Key
     * @param secretKey 你的 Secret Key
     * @return
     */
    @Bean
    public AipOcr aipOcr(@Value("${my.file.baidu.appid}") String appId, @Value("${my.file.baidu.ak}") String apiKey, @Value("${my.file.baidu.sk}") String secretKey) {
        AipOcr aipOcr = new AipOcr(appId, apiKey, secretKey);
        // 可选：设置网络连接参数
//        aipOcr.setConnectionTimeoutInMillis(2000);
//        aipOcr.setSocketTimeoutInMillis(60000);
        return aipOcr;
    }
}