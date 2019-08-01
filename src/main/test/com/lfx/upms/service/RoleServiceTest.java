package com.lfx.upms.service;

import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.lfx.upms.AbstractAppTest;
import com.lfx.upms.domain.Role;
import com.lfx.upms.domain.RoleExample;
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
public class RoleServiceTest extends AbstractAppTest {

    @Autowired
    private RoleService roleService;

    private static Role role;

    private static MockConfig mockConfig;

    private static Long roleId;

    @BeforeClass
    public static void setup() {
        mockConfig = new MockConfig().intRange(0, 1);
        role = JMockData.mock(Role.class, mockConfig);
    }

    @Test
    public void test100_insert() {
        roleService.insert(role);
        roleId = role.getId();
        Assert.assertNotNull(roleId);
    }

    @Test
    public void test101_selectByPrimaryKey() {
        Assert.assertEquals(role, roleService.selectByPrimaryKey(roleId));
    }

    @Test
    public void test102_selectByExample() {
        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andIdEqualTo(roleId);
        Assert.assertEquals(role, roleService.selectByExample(roleExample).get(0));
    }

    @Test
    public void test103_countByExample() {
        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andIdEqualTo(roleId);
        Assert.assertEquals(1, roleService.countByExample(roleExample));
    }

    @Test
    public void test104_updateByExample() {
        role = JMockData.mock(Role.class, mockConfig);
        role.setId(roleId);
        roleService.updateByPrimaryKey(role);
    }

    @Test
    public void test105_deleteByPrimaryKey() {
        roleService.deleteByPrimaryKey(roleId);
    }

    @Test
    public void test106_insertSelective() {
        role = JMockData.mock(Role.class, mockConfig);
        roleService.insertSelective(role);
        roleId = role.getId();
        Assert.assertNotNull(roleId);
    }

    @Test
    public void test107_updateByPrimaryKeySelective() {
        roleService.updateByPrimaryKeySelective(role);
    }

    @Test
    public void test108_deleteByExample() {
        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andIdEqualTo(roleId);
        roleService.deleteByExample(roleExample);
    }

}
