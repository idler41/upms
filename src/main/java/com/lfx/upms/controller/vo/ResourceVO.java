package com.lfx.upms.controller.vo;

import com.lfx.upms.controller.valid.DifferentValue;
import com.lfx.upms.controller.valid.InsertGroup;
import com.lfx.upms.controller.valid.UpdateGroup;
import com.lfx.upms.controller.valid.UpdateSelectiveGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.List;

/**
 * 表名：upms_resource 备注：前端资源表
 *
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Data
@DifferentValue(message = "id与parentId不能相同", filed = "id", matchFileds = {"parentId"}, groups = UpdateSelectiveGroup.class)
public class ResourceVO implements Serializable {

    @Null(groups = InsertGroup.class)
    @NotNull(groups = {UpdateGroup.class, UpdateSelectiveGroup.class})
    @Min(value = 0, groups = {UpdateGroup.class, UpdateSelectiveGroup.class})
    private Long id;

    /**
     * 父级资源ID
     */
    @NotNull(groups = {InsertGroup.class})
    @Min(value = 0, groups = {InsertGroup.class, UpdateGroup.class, UpdateSelectiveGroup.class})
    private Long parentId;

    /**
     * 资源标识
     */
    @Null(groups = {UpdateGroup.class, UpdateSelectiveGroup.class})
    @NotBlank(groups = {InsertGroup.class})
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
    @Range(min = 0, max = 2, groups = {InsertGroup.class, UpdateGroup.class, UpdateSelectiveGroup.class})
    @NotNull(groups = {InsertGroup.class})
    @Null(groups = {UpdateGroup.class, UpdateSelectiveGroup.class})
    private Integer type;

    private List<Long> permissionIds;

    private static final long serialVersionUID = 1L;
}