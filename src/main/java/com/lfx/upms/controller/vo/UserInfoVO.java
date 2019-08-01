package com.lfx.upms.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-02-21 16:07:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO implements Serializable {

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户目录树集合
     */
    private List<ResourceNode> menus;

    /**
     * 用户权限集合
     */
    private Set<String> resourceKey;
}