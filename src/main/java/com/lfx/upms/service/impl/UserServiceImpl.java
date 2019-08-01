package com.lfx.upms.service.impl;

import com.lfx.upms.domain.User;
import com.lfx.upms.domain.UserExample;
import com.lfx.upms.mapper.UserMapper;
import com.lfx.upms.service.UserService;
import com.lfx.upms.service.base.AbstractBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@Transactional(rollbackFor = Exception.class)
@Service("userService")
@Slf4j
public class UserServiceImpl extends AbstractBaseService<UserMapper, User, UserExample, Long>
        implements UserService {

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        super(userMapper);
    }

    @Override
    public User selectOneByUsername(String username) {
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<User> userList = selectByExample(example);
        if (userList == null || userList.isEmpty()) {
            return null;
        }
        return userList.get(0);
    }

    @Cacheable(value = "defaultCache", key = "'user:' + #id")
    @Override
    public User selectByPrimaryKey(Long id) {
        return super.selectByPrimaryKey(id);
    }

    @CacheEvict(value = "defaultCache", key = "'user:' + #id")
    @Override
    public int deleteByPrimaryKey(Long id) {
        return super.deleteByPrimaryKey(id);
    }

    @CacheEvict(value = "defaultCache", key = "'user:' + #record.id", condition = "record.id!=null")
    @Override
    public int insert(User record) {
        return super.insert(record);
    }

    @CacheEvict(value = "defaultCache", key = "'user:' + #record.id", condition = "record.id!=null")
    @Override
    public int insertSelective(User record) {
        return super.insertSelective(record);
    }

    @CacheEvict(value = "defaultCache", key = "'user:' + #record.id", condition = "record.id!=null")
    @Override
    public int updateByPrimaryKeySelective(User record) {
        return super.updateByPrimaryKeySelective(record);
    }

    @CacheEvict(value = "defaultCache", key = "'user:' + #record.id", condition = "record.id!=null")
    @Override
    public int updateByPrimaryKey(User record) {
        return super.updateByPrimaryKey(record);
    }

    @Override
    public Integer countRegisteredUser(long startTime, long endTime) {
        return mapper.countByCreateTime(startTime, endTime);
    }

    @Override
    public List<Long> selectIdsByRoleId(Long roleId) {
        return mapper.selectIdsByRoleId(roleId);
    }

    @Override
    public List<String> selectUsernameByRoleId(Long roleId) {
        return mapper.selectUsernameByRoleId(roleId);
    }

    @Override
    public void grant(Long userId, List<Long> roleIds, Long doGrantUser, Long doGrantTime) {
        // 删除该用户老的角色
        mapper.deleteRoleByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            mapper.grant(userId, roleIds, doGrantUser, doGrantTime);
        }
    }
}