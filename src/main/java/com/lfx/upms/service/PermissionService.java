package com.lfx.upms.service;

import com.lfx.upms.domain.Permission;
import com.lfx.upms.domain.PermissionExample;
import com.lfx.upms.service.base.BaseService;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
public interface PermissionService extends BaseService<Permission, PermissionExample, Long> {

    /**
     * 查询userId关联的所有权限Key
     *
     * @param userId 指定用户Id
     * @return 权限Key集合
     */
    Set<String> selectByUserId(Long userId);

    /**
     * 查询角色Id关联的所有权限Id
     *
     * @param roleId 指定角色Id
     * @return 权限Id集合
     */
    List<Long> selectIdsByRoleId(Long roleId);

    /**
     * 查询指定权限集合关联的任意一个资源
     *
     * @param permissionIds 权限集合
     * @return true: 有权限关联, false: 无权限关联
     */
    boolean isRelateToResource(Long... permissionIds);

    /**
     * 查询指定权限集合关联的任意一个角色
     *
     * @param permissionIds 权限集合
     * @return true: 有角色关联, false: 无角色关联
     */
    boolean isRelateToRole(Long... permissionIds);
}