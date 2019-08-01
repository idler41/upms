package com.lfx.upms.service;

import com.lfx.upms.domain.User;
import com.lfx.upms.domain.UserExample;
import com.lfx.upms.service.base.BaseService;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
public interface UserService extends BaseService<User, UserExample, Long> {

    /**
     * 查询指定登录用户名的用户信息
     *
     * @param username 登录名称
     * @return User
     */
    User selectOneByUsername(String username);

    /**
     * 统计指定时间的注册用户数
     *
     * @param startTime 开始时间戳
     * @param endTime   结束时间戳
     * @return 统计用户注册总数
     */
    Integer countRegisteredUser(long startTime, long endTime);

    /**
     * 查询指定角色Id关联的所有用户id
     *
     * @param roleId 角色id
     * @return 用户id集合
     */
    List<Long> selectIdsByRoleId(Long roleId);

    /**
     * 查询指定角色Id关联的所有用户username
     *
     * @param roleId 角色id
     * @return username集合
     */
    List<String> selectUsernameByRoleId(Long roleId);

    /**
     * 授权用户: 指定用户id关联角色集合
     *
     * @param userId      用户id
     * @param roleIds     角色Id集合
     * @param doGrantUser 授权人
     * @param doGrantTime 授权时间
     */
    void grant(Long userId, List<Long> roleIds, Long doGrantUser, Long doGrantTime);

}