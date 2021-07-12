package com.itheima.elasticjob.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.elasticjob.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    @Select("select * from tb_user where MOD(id,#{shardingTotalCount})=#{shardingItem}")
    List<User> queryUserById(@Param("shardingTotalCount") int shardingTotalCount, @Param("shardingItem")int shardingItem);
}
