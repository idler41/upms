package com.lfx.upms.service;

import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.lfx.upms.AbstractAppTest;
import com.lfx.upms.domain.Permission;
import com.lfx.upms.domain.PermissionExample;
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
public class PermissionServiceTest extends AbstractAppTest {

    @Autowired
    private PermissionService permissionService;

    private static Permission permission;

    private static MockConfig mockConfig;

    private static Long permissionId;

    @BeforeClass
    public static void setup() {
        mockConfig = new MockConfig().intRange(0, 1);
        permission = JMockData.mock(Permission.class, mockConfig);
    }

    @Test
    public void test100_insert() {
        permissionService.insert(permission);
        permissionId = permission.getId();
        Assert.assertNotNull(permissionId);
    }

    @Test
    public void test101_selectByPrimaryKey() {
        Assert.assertEquals(permission, permissionService.selectByPrimaryKey(permissionId));
    }

    @Test
    public void test102_selectByExample() {
        PermissionExample permissionExample = new PermissionExample();
        permissionExample.createCriteria().andIdEqualTo(permissionId);
        Assert.assertEquals(permission, permissionService.selectByExample(permissionExample).get(0));
    }

    @Test
    public void test103_countByExample() {
        PermissionExample permissionExample = new PermissionExample();
        permissionExample.createCriteria().andIdEqualTo(permissionId);
        Assert.assertEquals(1, permissionService.countByExample(permissionExample));
    }

    @Test
    public void test104_updateByExample() {
        permission = JMockData.mock(Permission.class, mockConfig);
        permission.setId(permissionId);
        permissionService.updateByPrimaryKey(permission);
    }

    @Test
    public void test105_deleteByPrimaryKey() {
        permissionService.deleteByPrimaryKey(permissionId);
    }

    @Test
    public void test106_insertSelective() {
        permission = JMockData.mock(Permission.class, mockConfig);
        permissionService.insertSelective(permission);
        permissionId = permission.getId();
        Assert.assertNotNull(permissionId);
    }

    @Test
    public void test107_updateByPrimaryKeySelective() {
        permissionService.updateByPrimaryKeySelective(permission);
    }

    @Test
    public void test108_deleteByExample() {
        PermissionExample permissionExample = new PermissionExample();
        permissionExample.createCriteria().andIdEqualTo(permissionId);
        permissionService.deleteByExample(permissionExample);
    }

}
