package com.lfx.upms.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 表名：upms_role 备注：角色表
 *
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-08 14:47:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role implements Serializable {
    private Long id;

    /**
     * 角色标识
     */
    private String roleKey;

    /**
     * 角色标签
     */
    private String label;

    /**
     * 是否超级管理员：0 否 1 是
     */
    private Integer admin;

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