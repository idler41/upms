package com.lfx.upms.mapper;

import com.lfx.upms.domain.Role;
import com.lfx.upms.domain.RoleExample;
import com.lfx.upms.mapper.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Repository
public interface RoleMapper extends BaseMapper<Role, RoleExample, Long> {

    /**
     * 查询指定用户关联的所有角色id
     *
     * @param userId userId 用户id
     * @return 关联的角色id集合
     */
    List<Long> selectRoleIdsByUserId(Long userId);

    /**
     * 删除指定角色关联的所有资源
     *
     * @param roleId 角色id
     * @return 影响行数
     */
    int deleteResourceByRoleId(Long roleId);

    /**
     * 删除指定角色关联的所有权限
     *
     * @param roleId 角色id
     * @return 影响行数
     */
    int deletePermissionByRoleId(Long roleId);

    /**
     * 授权角色: 指定角色id关联资源集合与权限集合
     *
     * @param roleId      角色id
     * @param resourceIds 授权资源id集合
     * @param createUser  授权人
     * @param doGrantTime 授权时间
     * @return 影响行数
     */
    int grantResources(
            @Param("roleId") Long roleId, @Param("resourceIds") List<Long> resourceIds,
            @Param("createUser") Long createUser, @Param("createTime") Long doGrantTime
    );

    /**
     * 授权角色: 指定角色id关联权限集合
     *
     * @param roleId        角色id
     * @param permissionIds 授权权限id集合
     * @param doGrantUser    授权人
     * @param doGrantTime   授权时间
     * @return 影响行数
     */
    int grantPermissions(
            @Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds,
            @Param("createUser") Long doGrantUser, @Param("createTime") Long doGrantTime
    );

    /**
     * 指定角色集合是否有用户关联
     *
     * @param roleIds 角色id集合
     * @return 1: 至少有一个用户关联, null: 没有任何一个用户关联
     */
    Integer selectOneUser(@Param("roleIds") Long[] roleIds);

    /**
     * 指定角色集合是否有资源关联
     *
     * @param roleIds 角色id集合
     * @return 1: 至少有一个角色关联, null: 没有任何一个角色关联
     */
    Integer selectOneResource(@Param("roleIds") Long[] roleIds);

    /**
     * 指定角色集合是否有权限关联
     *
     * @param roleIds 角色id集合
     * @return 1: 至少有一个权限关联, null: 没有任何一个权限关联
     */
    Integer selectOnePermission(@Param("roleIds") Long[] roleIds);

}