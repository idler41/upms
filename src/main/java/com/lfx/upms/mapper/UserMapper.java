package com.lfx.upms.mapper;

import com.lfx.upms.domain.User;
import com.lfx.upms.domain.UserExample;
import com.lfx.upms.mapper.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Repository
public interface UserMapper extends BaseMapper<User, UserExample, Long> {

    /**
     * 统计指定时间段新增的用户数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 用户总数
     */
    Integer countByCreateTime(@Param("startTime") long startTime, @Param("endTime") long endTime);

    /**
     * 查询指定角色id关联的所有用户id
     *
     * @param roleId 角色id
     * @return 关联用户id集合
     */
    List<Long> selectIdsByRoleId(Long roleId);

    /**
     * 查询指定角色id关联的所有用户的username
     *
     * @param roleId 角色id
     * @return 关联username集合
     */
    List<String> selectUsernameByRoleId(Long roleId);

    /**
     * 删除指定用户id关联的所有角色
     *
     * @param userId 用户id
     * @return 影响行数
     */
    int deleteRoleByUserId(Long userId);

    /**
     * 授权用户: 指定用户id关联角色集合
     *
     * @param userId      用户id
     * @param roleIds     角色id集合
     * @param doGrantUser 授权人
     * @param doGrantTime 授权时间
     * @return 影响行数
     */
    int grant(
            @Param("userId") Long userId,
            @Param("roleIds") List<Long> roleIds,
            @Param("createUser") Long doGrantUser,
            @Param("createTime") Long doGrantTime
    );

}