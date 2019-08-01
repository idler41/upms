package com.lfx.upms.shiro.listener;

import com.lfx.upms.util.WebConstants;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
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
@Slf4j
public class EhCacheKickoutListener implements AuthenticationListener {

    private final boolean kickoutEnable;

    private final Ehcache sessionCache;

    private final Ehcache kickoutCache;

    public EhCacheKickoutListener(boolean kickoutEnable, Ehcache sessionCache, Ehcache kickoutCache) {
        this.kickoutEnable = kickoutEnable;
        this.sessionCache = sessionCache;
        this.kickoutCache = kickoutCache;
    }

    @Override
    public void onSuccess(AuthenticationToken token, AuthenticationInfo info) {
        if (!kickoutEnable) {
            return;
        }

        WebSubject subject = (WebSubject) SecurityUtils.getSubject();
        HttpServletRequest request = (HttpServletRequest) subject.getServletRequest();
        Long userId = (Long) request.getAttribute(WebConstants.CURRENT_USER_ID);
        kickoutCache.acquireWriteLockOnKey(userId);
        try {
            // 1. 查询上一次登录的sessionId，如果有值则根据该sessionId删除缓存中的session
            Element element = kickoutCache.get(userId);
            if (element != null) {
                String sessionId = (String) element.getObjectValue();
                if (sessionId != null) {
                    sessionCache.remove(sessionId);
                }
            }

            // 2. 放入新的sessionId
            kickoutCache.put(new Element(userId, subject.getSession().getId()));
        } finally {
            kickoutCache.releaseWriteLockOnKey(userId);
        }
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
