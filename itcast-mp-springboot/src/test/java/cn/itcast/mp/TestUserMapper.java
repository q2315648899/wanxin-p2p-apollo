package cn.itcast.mp;

import cn.itcast.mp.mapper.UserMapper;
import cn.itcast.mp.pojo.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUserMapper {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testInsert() {
        User user = new User();
        user.setEmail("123@itcast.cn");
        user.setAge(301);
        user.setUserName("caocao");
        user.setName("曹操");
        user.setPassword("123456");

        //返回的result是受影响的行数，并不是自增后的id
        int result = userMapper.insert(user);
        System.out.println(result);

        //自增后的id会回填到对象中
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateById() {
        User user = new User();
        user.setId(5L); //主键
        user.setAge(21); //更新的字段
        //根据id更新，更新不为null的字段
        int result = userMapper.updateById(user);
        System.out.println(result);
    }

    @Test
    public void testDeleteById() {
        userMapper.deleteById(6);
    }

    @Test
    public void testEq() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //SELECT id,user_name,password,name,age,email FROM tb_user WHERE
        //password = ? AND age >= ? AND name IN (?,?,?)
        queryWrapper.eq("password", "123456").ge("age", 20).in("name", "李四", "王五", "赵六");
        List<User> list = userMapper.selectList(queryWrapper);

        for (User user : list) {
            System.out.println(user);
        }
    }

    @Test
    public void testLike() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", "张");
        List<User> list = userMapper.selectList(queryWrapper);

        for (User user : list) {
            System.out.println(user);
        }
    }

    @Test
    public void testOrderBy() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("age");

        List<User> list = userMapper.selectList(queryWrapper);
        for (User user : list) {
            System.out.println(user);
        }
    }

    @Test
    public void testOr() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        //SELECT id,user_name,password,name,age,email FROM tb_user WHERE
        //name = ? OR age = ?
        wrapper.eq("name", "李四").or().eq("age", 24);
        List<User> users = this.userMapper.selectList(wrapper);
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void testPage() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("age", 20);

        Page<User> page = new Page<>(1, 2);

        IPage<User> iPage = userMapper.selectPage(page, queryWrapper);
        System.out.println("数据总条数：" + iPage.getTotal());
        System.out.println("总页数：" + iPage.getPages());
        List<User> users = iPage.getRecords();
        for (User user : users) {
            System.out.println("user = " + user);
        }
    }


}
