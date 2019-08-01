package com.lfx.upms.util;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
public interface TreeNode<T extends TreeNode> {

    Integer ROOT_LEVEL = 0;

    Long ROOT_ID = 0L;

    /**
     * get 节点id
     *
     * @return 节点id
     */
    Long getId();

    /**
     * get 父节点id
     *
     * @return 父节点id
     */
    Long getParentId();

    /**
     * set 父节点
     *
     * @param treeNode 父节点
     */
    void setParentNode(T treeNode);

    /**
     * get 节点层级
     *
     * @return 节点层级
     */
    Integer getLevel();

    /**
     * get 子节点集合
     *
     * @return 子节点集合
     */
    List<T> getChildren();

    /**
     * set 子节点集合
     *
     * @param children 子节点集合
     */
    void setChildren(List<T> children);

    /**
     * 根据id判断是否根节点
     *
     * @param id 节点id
     * @return 判断该节点id是否为根节点id
     */
    static boolean isRootId(long id) {
        return ROOT_ID == id;
    }

    /**
     * 根据level判断是否为根节点层级
     *
     * @param level 层级
     * @return 判断该层级是否为根节点层级
     */
    static boolean isRootLevel(int level) {
        return ROOT_ID == level;
    }
}
