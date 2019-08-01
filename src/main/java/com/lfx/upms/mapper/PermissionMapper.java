package com.lfx.upms.mapper;

import com.lfx.upms.domain.Permission;
import com.lfx.upms.domain.PermissionExample;
import com.lfx.upms.mapper.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Repository
public interface PermissionMapper extends BaseMapper<Permission, PermissionExample, Long> {

    /**
     * 查询用户id关联的所有权限Key
     *
     * @param userId 指定用户Id
     * @return 权限Key集合
     */
    Set<String> selectByUserId(Long userId);

    /**
     * 查询角色id关联的所有权限id
     *
     * @param roleId 指定角色id
     * @return 权限id集合
     */
    List<Long> selectIdsByRoleId(Long roleId);

    /**
     * 指定权限集合是否有资源关联
     *
     * @param permissionIds 权限集合
     * @return 1: 至少有一个用户关联, null: 没有任何一个用户关联
     */
    Integer selectOneResource(@Param("permissionIds") Long[] permissionIds);

    /**
     * 指定权限集合是否有角色关联
     *
     * @param permissionIds 权限集合
     * @return 1: 至少有一个用户关联其中一个权限id, null: 没有任何一个用户关联任意一个权限id
     */
    Integer selectOneRole(@Param("permissionIds") Long[] permissionIds);
}