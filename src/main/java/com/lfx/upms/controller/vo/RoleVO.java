package com.lfx.upms.controller.vo;

import com.lfx.upms.controller.valid.InsertGroup;
import com.lfx.upms.controller.valid.UpdateGroup;
import com.lfx.upms.controller.valid.UpdateSelectiveGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * 表名：upms_role 备注：角色表
 *
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Data
public class RoleVO implements Serializable {

    @NotNull(groups = {UpdateGroup.class, UpdateSelectiveGroup.class})
    @Null(groups = InsertGroup.class)
    private Long id;

    /**
     * 角色标识
     */
    @NotBlank(groups = {InsertGroup.class})
    @Null(groups = {UpdateGroup.class, UpdateSelectiveGroup.class})
    private String roleKey;

    /**
     * 角色标签
     */
    private String label;

    /**
     * 是否超级管理员：0 否 1 是
     */
    @Range(min = 0, max = 1, groups = {UpdateGroup.class, UpdateSelectiveGroup.class})
    private Integer admin;

    /**
     * 创建时间
     */
    private Long createTime;

    private static final long serialVersionUID = 1L;
}