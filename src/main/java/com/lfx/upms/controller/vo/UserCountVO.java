package com.lfx.upms.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-06 13:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCountVO implements Serializable {

    /**
     * 用户总数统计
     */
    private Long userAmount;

    /**
     * 在线用户统计
     */
    private Integer onlineUser;

    /**
     * 昨日注册用户统计
     */
    private Integer registerUser;
}
