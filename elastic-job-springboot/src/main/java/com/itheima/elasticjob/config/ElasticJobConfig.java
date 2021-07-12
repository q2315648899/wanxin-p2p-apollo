package com.itheima.elasticjob.config;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.itheima.elasticjob.job.MyJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticJobConfig {

    @Autowired
    private MyJob myJob;

    @Autowired
    private ZookeeperRegistryCenter zkRegistryCenterConfig;

    @Value("${myjob.count}")
    private int shardingCount;

    @Value("${myjob.cron}")
    private String cron;

    /**
     * 配置任务详细信息
     * @param jobClass 任务执行类
     * @param cron 执行策略
     * @param shardingTotalCount 分片数量
     * @return
     */
    private LiteJobConfiguration createJobConfiguration(final Class<? extends SimpleJob> jobClass,
                                                        final String cron,
                                                        final int shardingTotalCount){
        //创建JobCoreConfigurationBuilder
        JobCoreConfiguration.Builder jobCoreConfigurationBuilder=JobCoreConfiguration.newBuilder(jobClass.getName(),cron,shardingTotalCount);

        JobCoreConfiguration jobCoreConfiguration = jobCoreConfigurationBuilder.build();

        //创建SimpleJobConfiguration
        SimpleJobConfiguration simpleJobConfiguration=new SimpleJobConfiguration(jobCoreConfiguration,jobClass.getCanonicalName());

        //创建LiteJobConfiguration
        LiteJobConfiguration liteJobConfiguration=LiteJobConfiguration.newBuilder(simpleJobConfiguration).jobShardingStrategyClass("com.dangdang.ddframe.job.lite.api.strategy.impl.AverageAllocationJobShardingStrategy").overwrite(true).build();

        return liteJobConfiguration;

    }

    @Bean(initMethod = "init")
    public SpringJobScheduler initSimpleElasticJob() {
        SpringJobScheduler springJobScheduler=new SpringJobScheduler(myJob,zkRegistryCenterConfig,createJobConfiguration(myJob.getClass(),cron,shardingCount));
        return springJobScheduler;
    }

}
