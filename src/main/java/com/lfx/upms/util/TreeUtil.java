package com.lfx.upms.util;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@UtilityClass
public class TreeUtil {

    /**
     * @param list 根据level升序排序的集合
     * @return 树形对象集合
     */
    @SuppressWarnings("unchecked")
    public static <T extends TreeNode> List<T> buildTree(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<T> result = new ArrayList<>();

        int upLevel = TreeNode.ROOT_LEVEL;
        Map<Long, TreeNode> upperLevelMap = new HashMap<>(list.size() / 2);
        Map<Long, TreeNode> currentLevelMap = new HashMap<>(list.size() / 2);

        TreeNode rootNode = null;
        for (T treeNode : list) {

            Long id = treeNode.getId();
            Long parentId = treeNode.getParentId();

            if (TreeNode.isRootId(parentId)) {
                result.add(treeNode);
                currentLevelMap.put(id, treeNode);
                continue;
            }

            Integer currentLevel = treeNode.getLevel();
            if (upLevel < currentLevel) {
                // 交换upperLevelMap与currentLevelMap引用
                Map<Long, TreeNode> temp = upperLevelMap;
                upperLevelMap = currentLevelMap;
                currentLevelMap = temp;

                currentLevelMap.clear();
                upLevel = currentLevel;
            }
            currentLevelMap.put(id, treeNode);

            TreeNode upperNode = upperLevelMap.get(parentId);
            treeNode.setParentNode(upperNode);

            List<TreeNode> upperChildren = upperNode.getChildren();
            if (upperChildren == null) {
                upperChildren = new ArrayList<>();
                upperNode.setChildren(upperChildren);
            }
            upperChildren.add(treeNode);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends TreeNode> T getTreeNode(List<T> tree, Long id) {
        if (id == null || TreeNode.isRootId(id)) {
            return null;
        }
        for (T treeNode : tree) {
            if (id.equals(treeNode.getId())) {
                return treeNode;
            }
            List<T> children = treeNode.getChildren();
            if (children != null && !children.isEmpty()) {
                T result = getTreeNode(children, id);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

}
