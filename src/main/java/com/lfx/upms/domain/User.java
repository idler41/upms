package com.lfx.upms.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 表名：upms_user 备注：用户表
 *
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登陆密码
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 出生日期：时分秒默认为凌晨
     */
    private Long birthday;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 注册时间
     */
    private Long createTime;

    /**
     * 上次密码重置时间
     */
    private Long resetPwdTime;

    /**
     * 是否被冻结：0 否 1 是
     */
    private Integer frozen;

    /**
     * 是否被禁用：0 否 1 是
     */
    private Integer disable;

    private static final long serialVersionUID = 1L;
}