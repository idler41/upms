package com.lfx.upms.shiro.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Locale;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-01 20:26
 */
@Slf4j
public class RestLogoutFilter extends LogoutFilter {

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) {
        Subject subject = getSubject(request, response);

        // Check if POST only logout is enabled
        if (isPostOnlyLogout()) {

            // check if the current request's method is a POST, if not redirect
            if (!HttpMethod.POST.matches(WebUtils.toHttp(request).getMethod().toUpperCase(Locale.ENGLISH))) {
                return onLogoutRequestNotAPost(request, response);
            }
        }

        //try/catch added for SHIRO-298:
        try {
            subject.logout();
        } catch (SessionException ise) {
            log.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        }

        if (log.isInfoEnabled()) {
            log.info("用户【{}】登出成功", subject.getPrincipal());
        }
        return false;
    }
}
