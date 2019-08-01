package com.lfx.upms.shiro.listener;

import com.lfx.upms.util.WebConstants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.subject.WebSubject;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-04-27 18:35
 */
public class SessionListener implements AuthenticationListener {

    @Override
    public void onSuccess(AuthenticationToken token, AuthenticationInfo info) {
        WebSubject currentSubject = (WebSubject) SecurityUtils.getSubject();
        HttpServletRequest request = (HttpServletRequest) currentSubject.getServletRequest();
        Long userId = (Long) request.getAttribute(WebConstants.CURRENT_USER_ID);
        currentSubject.getSession().setAttribute(WebConstants.CURRENT_USER_ID, userId);
    }

    @Override
    public void onFailure(AuthenticationToken token, AuthenticationException ae) {
        // do nothing
    }

    @Override
    public void onLogout(PrincipalCollection principals) {
        // do nothing
    }
}
