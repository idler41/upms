package com.lfx.upms.service.impl;

import com.lfx.upms.domain.Permission;
import com.lfx.upms.domain.PermissionExample;
import com.lfx.upms.mapper.PermissionMapper;
import com.lfx.upms.service.PermissionService;
import com.lfx.upms.service.base.AbstractBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Transactional(rollbackFor = Exception.class)
@Service("permissionService")
@Slf4j
public class PermissionServiceImpl extends AbstractBaseService<PermissionMapper, Permission, PermissionExample, Long>
        implements PermissionService {


    @Autowired
    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        super(permissionMapper);
    }

    @Override
    public void deleteByExample(PermissionExample example) {
        mapper.deleteByExample(example);
    }

    @Override
    public Set<String> selectByUserId(Long userId) {
        return mapper.selectByUserId(userId);
    }

    @Override
    public List<Long> selectIdsByRoleId(Long roleId) {
        return mapper.selectIdsByRoleId(roleId);
    }

    @Override
    public boolean isRelateToResource(Long... permissionIds) {
        Integer result = mapper.selectOneResource(permissionIds);
        return result != null && result > 0;
    }

    @Override
    public boolean isRelateToRole(Long... permissionIds) {
        Integer result = mapper.selectOneRole(permissionIds);
        return result != null && result > 0;
    }

}