package com.lfx.upms.mapper;

import com.lfx.upms.domain.Resource;
import com.lfx.upms.domain.ResourceExample;
import com.lfx.upms.domain.ResourcePermission;
import com.lfx.upms.mapper.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Repository
public interface ResourceMapper extends BaseMapper<Resource, ResourceExample, Long> {

    /**
     * 查询指定用户id关联的所有资源
     *
     * @param userId 用户id
     * @return 关联资源集合
     */
    List<Resource> selectByUserId(Long userId);

    /**
     * 查询指定角色id集合关联的所有叶子节点资源id
     *
     * @param roleIds 角色id集合
     * @return 关联的叶子节点资源id集合
     */
    List<Long> selectLeafIdsByRoleIds(@Param("roleIds") Long... roleIds);


    /**
     * 指定资源id是否有权限关联
     *
     * @param resourceId 资源id
     * @return 1: 至少有一个权限关联, null: 没有任何一个权限关联
     */
    Integer selectOnePermission(Long resourceId);

    /**
     * 指定资源id是否有角色关联
     *
     * @param resourceId 资源id
     * @return 1: 至少有一个角色关联, null: 没有任何一个角色关联
     */
    Integer selectOneRole(Long resourceId);

    /**
     * 查询指定角色关联的所有资源id
     *
     * @param roleId 角色id
     * @return 关联的资源id集合
     */
    List<Long> selectIdsByRoleId(Long roleId);

    /**
     * 授权资源: 指定资源id关联权限集合
     *
     * @param resourceId    资源id
     * @param permissionIds 权限id集合
     * @param doGrantUser   授权人
     * @param doGrantTime   授权时间
     * @return 影响行数
     */
    int grant(
            @Param("resourceId") Long resourceId, @Param("permissionIds") List<Long> permissionIds,
            @Param("createUser") Long doGrantUser, @Param("createTime") Long doGrantTime
    );

    /**
     * 删除指定资源关联的所有权限
     *
     * @param resourceId 资源id
     * @return 影响行数
     */
    int deletePermission(Long resourceId);

    /**
     * 查询所有资源以及资源关联的权限
     *
     * @return 以resourceLevel, levelOrder升序排序的 资源-权限 集合
     */
    List<ResourcePermission> selectTreeWithPermission();

}