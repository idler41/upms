package com.lfx.upms.service;

import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.lfx.upms.AbstractAppTest;
import com.lfx.upms.domain.User;
import com.lfx.upms.domain.UserExample;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceTest extends AbstractAppTest {

    @Autowired
    private UserService userService;

    private static User user;

    private static MockConfig mockConfig;

    private static Long userId;

    @BeforeClass
    public static void setup() {
        mockConfig = new MockConfig().intRange(0, 1);
        user = JMockData.mock(User.class, mockConfig);
    }

    @Test
    public void test100_insert() {
        userService.insert(user);
        userId = user.getId();
        Assert.assertNotNull(userId);
    }

    @Test
    public void test101_selectByPrimaryKey() {
        Assert.assertEquals(user, userService.selectByPrimaryKey(userId));
    }

    @Test
    public void test102_selectByExample() {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdEqualTo(userId);
        Assert.assertEquals(user, userService.selectByExample(userExample).get(0));
    }

    @Test
    public void test103_countByExample() {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdEqualTo(userId);
        Assert.assertEquals(1, userService.countByExample(userExample));
    }

    @Test
    public void test104_updateByExample() {
        user = JMockData.mock(User.class, mockConfig);
        user.setId(userId);
        userService.updateByPrimaryKey(user);
    }

    @Test
    public void test105_deleteByPrimaryKey() {
        userService.deleteByPrimaryKey(userId);
    }

    @Test
    public void test106_insertSelective() {
        user = JMockData.mock(User.class, mockConfig);
        userService.insertSelective(user);
        userId = user.getId();
        Assert.assertNotNull(userId);
    }

    @Test
    public void test107_updateByPrimaryKeySelective() {
        userService.updateByPrimaryKeySelective(user);
    }

    @Test
    public void test108_deleteByExample() {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdEqualTo(userId);
        userService.deleteByExample(userExample);
    }

}
