package com.itheima.elasticjob.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.itheima.elasticjob.dao.UserMapper;
import com.itheima.elasticjob.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据查询任务
 */
@Component
public class MyJob implements SimpleJob {

    @Autowired
    private UserMapper userMapper;

    //执行定时任务
    @Override
    public void execute(ShardingContext shardingContext) {
        //得到分片总数
        int count = shardingContext.getShardingTotalCount();

        //得到分片项
        int item = shardingContext.getShardingItem();

        //查询数据
        List<User> userList = userMapper.queryUserById(count,item);

        //输出结果
        userList.forEach(user -> {
            System.out.println("作业分片:"+item+"--->"+user);
        });
    }
}
