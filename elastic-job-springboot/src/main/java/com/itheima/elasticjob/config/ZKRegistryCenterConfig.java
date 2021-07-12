package com.itheima.elasticjob.config;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZKRegistryCenterConfig {

    //zookeeper服务器地址
    @Value("${zookeeper.connString}")
    private String ZOOKEEPER_CONNECTION_STRING;

    //定时任务的名称空间
    @Value("${myjob.namespace}")
    private String JOB_NAMESPACE;

    //zk的配置及创建注册中心
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter setUpRegistryCenter(){
        //zk配置
        ZookeeperConfiguration zookeeperConfiguration=new ZookeeperConfiguration(ZOOKEEPER_CONNECTION_STRING,JOB_NAMESPACE);

        //创建注册中心
        ZookeeperRegistryCenter zookeeperRegistryCenter=new ZookeeperRegistryCenter(zookeeperConfiguration);

        return zookeeperRegistryCenter;
    }
}
