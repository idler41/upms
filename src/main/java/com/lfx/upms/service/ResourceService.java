package com.lfx.upms.service;

import com.lfx.upms.controller.vo.ResourceNode;
import com.lfx.upms.domain.Resource;
import com.lfx.upms.domain.ResourceExample;
import com.lfx.upms.service.base.BaseService;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
public interface ResourceService extends BaseService<Resource, ResourceExample, Long> {

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
    List<Long> selectLeafIdsByRoleIds(Long... roleIds);

    /**
     * 1. 插入新资源
     * 2. 如果当前资源的父节点原先没有任何子节点，则更新父节点leaf字段
     * 3. 授权资源: 指定资源id关联权限集合
     *
     * @param resourceToInsert 待添加的资源
     * @param permissionIds 待授权的权限集合
     * @param parentResource 待更新的父节点资源
     */
    void mixedInsert(Resource resourceToInsert, List<Long> permissionIds, Resource parentResource);

    /**
     *  1. 当前资源节点更新
     *  2. 关联权限更新
     *  3. 旧父节点/当前父节点/子节点 的资源批量更新leaf和resourceLevel字段
     *
     * @param listToUpdate 待更新的资源集合
     * @param doGrantUser 授权用户
     * @param doGrantTime 授权时间
     * @param permissionIds 关联权限id集合
     */
    void mixedUpdate(List<Resource> listToUpdate, Long doGrantUser, Long doGrantTime, List<Long> permissionIds);

    /**
     * 指定资源id是否有权限关联
     *
     * @param id 资源id
     * @return true: 至少有一个权限关联, false: 没有任何一个权限关联
     */
    boolean isRelateToPermission(Long id);

    /**
     * 指定资源id是否有角色关联
     *
     * @param id 资源id
     * @return true: 至少有一个角色关联, false: 没有任何一个角色关联
     */
    boolean isRelateToRole(Long id);

    /**
     * 查询所有资源以及资源关联的权限
     *
     * @return 以resourceLevel, levelOrder升序排序的 资源-权限 集合
     */
    List<ResourceNode> selectTreeWithPermission();

}