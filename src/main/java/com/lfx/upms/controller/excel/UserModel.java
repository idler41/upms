package com.lfx.upms.controller.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.lfx.upms.domain.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-04-28 09:27
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserModel extends BaseRowModel {

    /**
     * 用户名
     */
    @ExcelProperty(value = "用户名", index = 0)
    private String username;

    /**
     * 用户昵称
     */
    @ExcelProperty(value = "用户昵称", index = 1)
    private String nickname;

    /**
     * 手机号码
     */
    @ExcelProperty(value = "手机号码", index = 2)
    private String mobile;

    /**
     * 邮箱地址
     */
    @ExcelProperty(value = "邮箱", index = 3)
    private String email;

    /**
     * 性别
     */
    @ExcelProperty(value = "性别", index = 4)
    private String gender;

    /**
     * 出生日期：时分秒默认为凌晨
     */
    @ExcelProperty(value = "出生日期", format = "yyyy-MM-dd", index = 5)
    private Date birthday;

    /**
     * 注册时间
     */
    @ExcelProperty(value = "注册时间", format = "yyyy-MM-dd", index = 6)
    private Date createTime;

    /**
     * 是否被冻结：0 否 1 是
     */
    @ExcelProperty(value = {"用户状态", "冻结"}, index = 7)
    private String frozen;

    /**
     * 是否被禁用：0 否 1 是
     */
    @ExcelProperty(value = {"用户状态", "禁用"}, index = 8)
    private String disable;

    public UserModel(User user, String[] gender, String[] frozen, String[] disable) {
        setUsername(user.getUsername());
        setNickname(user.getNickname());
        setEmail(user.getEmail());
        setGender(user.getGender() == null ? null : gender[user.getGender()]);
        setBirthday(user.getBirthday() == null ? null : new Date(user.getBirthday()));
        setCreateTime(user.getCreateTime() == null ? null : new Date(user.getCreateTime()));
        setFrozen(frozen[user.getFrozen()]);
        setDisable(disable[user.getDisable()]);
    }
}
