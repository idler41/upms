package com.lfx.upms.service.impl;

import com.lfx.upms.domain.Role;
import com.lfx.upms.domain.RoleExample;
import com.lfx.upms.mapper.RoleMapper;
import com.lfx.upms.service.RoleService;
import com.lfx.upms.service.base.AbstractBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Transactional(rollbackFor = Exception.class)
@Service("roleService")
@Slf4j
public class RoleServiceImpl extends AbstractBaseService<RoleMapper, Role, RoleExample, Long>
        implements RoleService {

    @Autowired
    public RoleServiceImpl(RoleMapper roleMapper) {
        super(roleMapper);
    }

    @Override
    public void deleteByExample(RoleExample example) {
        mapper.deleteByExample(example);
    }

    @Override
    public List<Long> selectRoleIdsByUserId(Long userId) {
        return mapper.selectRoleIdsByUserId(userId);
    }

    /**
     * 1. 删除与该角色有关的老的资源、权限数据
     * 2. 授权角色新的资源、权限数据
     * 3. 清除缓存中该角色关联所有用户的权限数据
     *
     * @param roleId        角色id
     * @param resourceIds   授权资源id集合
     * @param permissionIds 授权权限id集合
     * @param doGrantUser   授权人
     * @param doGrantTime   授权时间
     */
    @Override
    public void grant(Long roleId, List<Long> resourceIds, List<Long> permissionIds, Long doGrantUser, Long doGrantTime) {
        mapper.deleteResourceByRoleId(roleId);
        mapper.deletePermissionByRoleId(roleId);

        if (resourceIds != null && !resourceIds.isEmpty()) {
            mapper.grantResources(roleId, resourceIds, doGrantUser, doGrantTime);
        }
        if (permissionIds != null && !permissionIds.isEmpty()) {
            mapper.grantPermissions(roleId, permissionIds, doGrantUser, doGrantTime);
        }
    }

    @Override
    public boolean isRelateToUser(Long... roleIds) {
        Integer result = mapper.selectOneUser(roleIds);
        return result != null && result > 0;
    }

    @Override
    public boolean isRelateToResource(Long... roleIds) {
        Integer result = mapper.selectOneResource(roleIds);
        return result != null && result > 0;
    }

    @Override
    public boolean isRelateToPermission(Long... roleIds) {
        Integer result = mapper.selectOnePermission(roleIds);
        return result != null && result > 0;
    }
}