package com.lfx.upms.shiro.listener;

import com.lfx.upms.captcha.CaptchaManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.subject.WebSubject;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-04-27 18:35
 */
@Slf4j
public class CaptchaListener implements AuthenticationListener {

    private final boolean captchaEnabled;

    private final CaptchaManager captchaManager;

    public CaptchaListener(boolean captchaEnabled, CaptchaManager captchaManager) {
        this.captchaEnabled = captchaEnabled;
        this.captchaManager = captchaManager;
    }

    @Override
    public void onSuccess(AuthenticationToken token, AuthenticationInfo info) {

        if (captchaEnabled) {
            // 清空验证码
            WebSubject webSubject = (WebSubject) SecurityUtils.getSubject();
            String remoteHost = webSubject.getServletRequest().getRemoteHost();
            captchaManager.clear(remoteHost);
        }

    }

    @Override
    public void onFailure(AuthenticationToken token, AuthenticationException ae) {
        // 根据ip记录登录错误次数，超过指定次数需要输入校验码
        if (captchaEnabled) {
            // 验证码错误次数 + 1
            WebSubject webSubject = (WebSubject) SecurityUtils.getSubject();
            String remoteHost = webSubject.getServletRequest().getRemoteHost();

            captchaManager.increaseLoginFailedTimes(remoteHost);
        }
    }

    @Override
    public void onLogout(PrincipalCollection principals) {
        // do nothing
    }

}
