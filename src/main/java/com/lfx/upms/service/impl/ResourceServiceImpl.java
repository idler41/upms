package com.lfx.upms.service.impl;

import com.lfx.upms.controller.vo.ResourceNode;
import com.lfx.upms.domain.Resource;
import com.lfx.upms.domain.ResourceExample;
import com.lfx.upms.domain.ResourcePermission;
import com.lfx.upms.mapper.ResourceMapper;
import com.lfx.upms.service.ResourceService;
import com.lfx.upms.service.base.AbstractBaseService;
import com.lfx.upms.util.BeanCopierUtil;
import com.lfx.upms.util.TreeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Transactional(rollbackFor = Exception.class)
@Service("resourceService")
@Slf4j
public class ResourceServiceImpl extends AbstractBaseService<ResourceMapper, Resource, ResourceExample, Long>
        implements ResourceService {

    @Autowired
    public ResourceServiceImpl(ResourceMapper resourceMapper) {
        super(resourceMapper);
    }

    @Override
    public List<Resource> selectByUserId(Long userId) {
        return mapper.selectByUserId(userId);
    }

    @Override
    public List<Long> selectLeafIdsByRoleIds(Long... roleIds) {
        return mapper.selectLeafIdsByRoleIds(roleIds);
    }

    @CacheEvict(value = "defaultCache", key = "'resource:tree'")
    @Override
    public int deleteByPrimaryKey(Long aLong) {
        return super.deleteByPrimaryKey(aLong);
    }

    @CacheEvict(value = "defaultCache", key = "'resource:tree'")
    @Override
    public int insert(Resource record) {
        return super.insert(record);
    }

    @CacheEvict(value = "defaultCache", key = "'resource:tree'")
    @Override
    public int insertSelective(Resource record) {
        return super.insertSelective(record);
    }

    @CacheEvict(value = "defaultCache", key = "'resource:tree'")
    @Override
    public int updateByPrimaryKeySelective(Resource record) {
        return super.updateByPrimaryKeySelective(record);
    }

    @CacheEvict(value = "defaultCache", key = "'resource:tree'")
    @Override
    public int updateByPrimaryKey(Resource record) {
        return super.updateByPrimaryKey(record);
    }

    @CacheEvict(value = "defaultCache", key = "'resource:tree'")
    @Override
    public void mixedInsert(Resource resourceToInsert, List<Long> permissionIds, Resource parentResource) {
        if (parentResource != null) {
            updateByPrimaryKey(parentResource);
        }
        insertSelective(resourceToInsert);

        // 因为是新增所以不需要删除旧资源权限
        Long resourceId = resourceToInsert.getId();
        Long doGrantUser = resourceToInsert.getCreateUser();
        Long doGrantTime = resourceToInsert.getCreateTime();
        if (permissionIds != null && !permissionIds.isEmpty()) {
            mapper.grant(resourceId, permissionIds, doGrantUser, doGrantTime);
        }
    }

    @CacheEvict(value = "defaultCache", key = "'resource:tree'")
    @Override
    public void mixedUpdate(List<Resource> listToUpdate, Long doGrantUser, Long doGrantTime, List<Long> permissionIds) {
        for (Resource resourceToUpdate : listToUpdate) {
            updateByPrimaryKeySelective(resourceToUpdate);
        }
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        Long resourceId = listToUpdate.get(listToUpdate.size() - 1).getId();
        mapper.deletePermission(resourceId);
        mapper.grant(resourceId, permissionIds, doGrantUser, doGrantTime);
    }

    @Override
    public boolean isRelateToPermission(Long id) {
        Integer result = mapper.selectOnePermission(id);
        return result != null && result == 1;
    }

    @Override
    public boolean isRelateToRole(Long id) {
        Integer result = mapper.selectOneRole(id);
        return result != null && result == 1;
    }

    @Cacheable(value = "defaultCache", key = "'resource:tree'")
    @SuppressWarnings("unchecked")
    @Override
    public List<ResourceNode> selectTreeWithPermission() {
        List<ResourcePermission> data = mapper.selectTreeWithPermission();
        return data.isEmpty() ? Collections.EMPTY_LIST : TreeUtil.buildTree(BeanCopierUtil.copyList(data, ResourceNode.class));
    }
}