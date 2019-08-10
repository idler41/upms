package com.lfx.upms.shiro.aop;

import com.lfx.upms.util.WebConstants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.aop.PermissionAnnotationHandler;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.web.subject.WebSubject;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-08-09 20:06
 */
public class NoSubjectPermissionAnnotationHandler extends PermissionAnnotationHandler {

    private PrincipalCollection emptySubjectPrincipal = new SimplePrincipalCollection();

    @Override
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if (!(a instanceof RequiresPermissions)) return;

        WebSubject webSubject = (WebSubject) SecurityUtils.getSubject();
        Long userId = (Long) webSubject.getServletRequest().getAttribute(WebConstants.CURRENT_USER_ID);
        if (userId == null) {
            throw new UnauthenticatedException();
        }

        RequiresPermissions rpAnnotation = (RequiresPermissions) a;
        String[] perms = getAnnotationValue(a);

        SecurityManager securityManager = SecurityUtils.getSecurityManager();

        if (perms.length == 1) {
            securityManager.checkPermission(emptySubjectPrincipal, perms[0]);
            return;
        }
        if (Logical.AND.equals(rpAnnotation.logical())) {
            securityManager.checkPermissions(emptySubjectPrincipal, perms);
            return;
        }
        if (Logical.OR.equals(rpAnnotation.logical())) {
            // Avoid processing exceptions unnecessarily - "delay" throwing the exception by calling hasRole first
            boolean hasAtLeastOnePermission = false;
            for (String permission : perms)
                if (securityManager.isPermitted(emptySubjectPrincipal, permission)) hasAtLeastOnePermission = true;
            // Cause the exception if none of the role match, note that the exception message will be a bit misleading
            if (!hasAtLeastOnePermission) securityManager.checkPermission(emptySubjectPrincipal, perms[0]);

        }
    }
}
