package com.lfx.upms.controller.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lfx.upms.enums.ResourceEnum;
import com.lfx.upms.util.TreeNode;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 19:57
 */
@Data
public class ResourceNode implements TreeNode<ResourceNode>, Serializable {
    private Long id;

    /**
     * 父级资源Id
     */
    private Long parentId;
    /**
     * 父级资源节点
     */
    @JsonIgnore
    private ResourceNode parentNode;

    /**
     * 资源标识
     */
    private String resourceKey;

    /**
     * 资源描述
     */
    private String label;

    /**
     * 资源图标
     */
    private String icon;

    /**
     * 资源类型: 0 下拉目录 1 可点击目录 2 按钮或列表
     */
    private Integer type;

    @JsonIgnore
    private Integer resourceLevel;

    /**
     * 层级顺序
     */
    private Integer levelOrder;

    /**
     * 是否为叶子节点：0 否 1 是
     */
    @JsonIgnore
    private Integer leaf;

    private List<ResourceNode> children;

    private List<Long> permissionIds;

    @Override
    public Integer getLevel() {
        return resourceLevel;
    }

    public static Integer getRootType() {
        return ResourceEnum.DIRECTORY.getCode();
    }
}
