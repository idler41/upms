package com.lfx.upms.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 表名：upms_resource 备注：前端资源表
 *
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-08 14:47:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource implements Serializable {
    private Long id;

    /**
     * 父级资源ID
     */
    private Long parentId;

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
     * 资源层级
     */
    private Integer resourceLevel;

    /**
     * 层级顺序
     */
    private Integer levelOrder;

    /**
     * 是否为叶子节点：0 否 1 是
     */
    private Integer leaf;

    /**
     * 资源类型: 0 下拉目录 1 可点击目录 2 其它页面元素如按钮/列表等
     */
    private Integer type;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private Long createTime;

    private static final long serialVersionUID = 1L;
}