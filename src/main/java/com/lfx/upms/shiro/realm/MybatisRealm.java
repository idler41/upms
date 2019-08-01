package com.lfx.upms.shiro.realm;

import com.lfx.upms.domain.User;
import com.lfx.upms.service.PermissionService;
import com.lfx.upms.service.UserService;
import com.lfx.upms.util.WebConstants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.subject.WebSubject;

import java.util.Set;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-03-30 20:04
 */
public class MybatisRealm extends AuthorizingRealm {

    private final UserService userService;

    private final PermissionService permissionService;

    public MybatisRealm(UserService userService, PermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        WebSubject webSubject = (WebSubject) SecurityUtils.getSubject();
        Long userId = (Long) webSubject.getServletRequest().getAttribute(WebConstants.CURRENT_USER_ID);
        Set<String> permissionSet = permissionService.selectByUserId(userId);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setStringPermissions(permissionSet);
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        User user = userService.selectOneByUsername(username);
        if (user == null) {
            return null;
        }
        if (user.getDisable() == 1) {
            // 用户被禁用
            throw new DisabledAccountException();
        }

        if (user.getFrozen() == 1) {
            // 用户被锁定
            throw new LockedAccountException();
        }
        WebSubject webSubject = (WebSubject) SecurityUtils.getSubject();
        webSubject.getServletRequest().setAttribute(WebConstants.CURRENT_USER_ID, user.getId());
        return new SimpleAuthenticationInfo(username, user.getPassword(), getName());
    }

}
