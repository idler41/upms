package com.lfx.upms.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 表名：upms_permission 备注：权限表
 *
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-08 14:47:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission implements Serializable {
    private Long id;

    /**
     * 权限标识
     */
    private String permissionKey;

    /**
     * 权限标签
     */
    private String label;

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