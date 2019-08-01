package com.lfx.upms.service;

import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.lfx.upms.AbstractAppTest;
import com.lfx.upms.domain.Resource;
import com.lfx.upms.domain.ResourceExample;
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
public class ResourceServiceTest extends AbstractAppTest {

    @Autowired
    private ResourceService resourceService;

    private static Resource resource;

    private static MockConfig mockConfig;

    private static Long resourceId;

    @BeforeClass
    public static void setup() {
        mockConfig = new MockConfig().intRange(0, 1);
        resource = JMockData.mock(Resource.class, mockConfig);
    }

    @Test
    public void test100_insert() {
        resourceService.insert(resource);
        resourceId = resource.getId();
        Assert.assertNotNull(resourceId);
    }

    @Test
    public void test101_selectByPrimaryKey() {
        Assert.assertEquals(resource, resourceService.selectByPrimaryKey(resourceId));
    }

    @Test
    public void test102_selectByExample() {
        ResourceExample resourceExample = new ResourceExample();
        resourceExample.createCriteria().andIdEqualTo(resourceId);
        Assert.assertEquals(resource, resourceService.selectByExample(resourceExample).get(0));
    }

    @Test
    public void test103_countByExample() {
        ResourceExample resourceExample = new ResourceExample();
        resourceExample.createCriteria().andIdEqualTo(resourceId);
        Assert.assertEquals(1, resourceService.countByExample(resourceExample));
    }

    @Test
    public void test104_updateByExample() {
        resource = JMockData.mock(Resource.class, mockConfig);
        resource.setId(resourceId);
        resourceService.updateByPrimaryKey(resource);
    }

    @Test
    public void test105_deleteByPrimaryKey() {
        resourceService.deleteByPrimaryKey(resourceId);
    }

    @Test
    public void test106_insertSelective() {
        resource = JMockData.mock(Resource.class, mockConfig);
        resourceService.insertSelective(resource);
        resourceId = resource.getId();
        Assert.assertNotNull(resourceId);
    }

    @Test
    public void test107_updateByPrimaryKeySelective() {
        resourceService.updateByPrimaryKeySelective(resource);
    }

    @Test
    public void test108_deleteByExample() {
        ResourceExample resourceExample = new ResourceExample();
        resourceExample.createCriteria().andIdEqualTo(resourceId);
        resourceService.deleteByExample(resourceExample);
    }

}
