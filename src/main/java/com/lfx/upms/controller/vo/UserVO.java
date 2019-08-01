package com.lfx.upms.controller.vo;

import com.lfx.upms.controller.valid.InsertGroup;
import com.lfx.upms.controller.valid.UpdateGroup;
import com.lfx.upms.controller.valid.UpdateSelectiveGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 表名：upms_user 备注：用户表
 *
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Data
public class UserVO implements Serializable {

    @NotNull(groups = {UpdateGroup.class, UpdateSelectiveGroup.class})
    @Null(groups = InsertGroup.class)
    private Long id;

    /**
     * 用户名
     */
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]{4,16}$", message = "用户名必须以字母开头,允许4-16位长度,允许字母、数字、下划线、连接符", groups = {InsertGroup.class})
    @Null(groups = {UpdateGroup.class, UpdateSelectiveGroup.class})
    private String username;

    /**
     * 登陆密码
     */
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{5,16}$", message = "密码必须包含大小写字母和数字的组合,允许6-16位长度", groups = {InsertGroup.class, UpdateSelectiveGroup.class})
    @NotNull(groups = {InsertGroup.class})
    private String password;

    /**
     * 用户昵称
     */
    @Size(max = 12, groups = {InsertGroup.class, UpdateGroup.class, UpdateSelectiveGroup.class})
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
    @Range(min = 0, max = 1, groups = {InsertGroup.class, UpdateSelectiveGroup.class})
    private Integer gender;

    /**
     * 出生日期：时分秒默认为凌晨
     */
    private Long birthday;

    /**
     * 是否被冻结：0 否 1 是
     */
    @Range(min = 0, max = 1, groups = {UpdateGroup.class, UpdateSelectiveGroup.class})
    private Integer frozen;

    /**
     * 是否被禁用：0 否 1 是
     */
    @Range(min = 0, max = 1, groups = {UpdateGroup.class, UpdateSelectiveGroup.class})
    private Integer disable;

    /**
     * 注册时间
     */
    private Long createTime;

    private static final long serialVersionUID = 1L;
}