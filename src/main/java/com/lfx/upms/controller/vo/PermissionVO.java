package com.lfx.upms.controller.vo;

import com.lfx.upms.controller.valid.InsertGroup;
import com.lfx.upms.controller.valid.UpdateGroup;
import com.lfx.upms.controller.valid.UpdateSelectiveGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * 表名：upms_permission 备注：权限表
 *
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Data
public class PermissionVO implements Serializable {

    @NotNull(groups = {UpdateGroup.class, UpdateSelectiveGroup.class})
    @Null(groups = InsertGroup.class)
    private Long id;

    /**
     * 权限标识
     */
    @NotBlank(groups = {InsertGroup.class})
    @Null(groups = {UpdateGroup.class, UpdateSelectiveGroup.class})
    private String permissionKey;

    /**
     * 权限标签
     */
    private String label;

    /**
     * 创建时间
     */
    private Long createTime;

    private static final long serialVersionUID = 1L;
}