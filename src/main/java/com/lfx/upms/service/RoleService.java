package com.lfx.upms.service;

import com.lfx.upms.domain.Role;
import com.lfx.upms.domain.RoleExample;
import com.lfx.upms.service.base.BaseService;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
public interface RoleService extends BaseService<Role, RoleExample, Long> {

    /**
     * 查询指定用户关联的所有角色id
     *
     * @param userId 用户id
     * @return 关联的角色id集合
     */
    List<Long> selectRoleIdsByUserId(Long userId);

    /**
     * 1. 删除与该角色有关的老的资源、权限数据
     * 2. 授权角色新的资源、权限数据
     * 3. 清除缓存中该角色关联所有用户的权限数据
     *
     * @param roleId 角色id
     * @param resourceIds 授权资源id集合
     * @param permissionIds 授权权限id集合
     * @param doGrantUser 授权人
     * @param doGrantTime 授权时间
     */
    void grant(Long roleId, List<Long> resourceIds, List<Long> permissionIds, Long doGrantUser, Long doGrantTime);

    /**
     * 是否有用户关联指定角色集合
     *
     * @param roleIds 角色id集合
     * @return true：至少有一个用户关联，false：没有任何一个用户关联
     */
    boolean isRelateToUser(Long... roleIds);

    /**
     * 是否有资源关联指定角色集合
     *
     * @param roleIds 角色id集合
     * @return true：至少有一个角色关联，false：没有任何一个角色关联
     */
    boolean isRelateToResource(Long... roleIds);

    /**
     * 是否有权限关联指定角色集合
     *
     * @param roleIds 角色id集合
     * @return true：至少有一个权限关联，false：没有任何一个权限关联
     */
    boolean isRelateToPermission(Long... roleIds);

}