package com.lfx.upms.shiro.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lfx.upms.enums.ResultEnum;
import com.lfx.upms.result.ResultBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-04-29 14:54
 */
@Slf4j
public class RestLoginFilter extends PathMatchingFilter {

    public static final String DEFAULT_USERNAME_PARAM = "username";
    public static final String DEFAULT_PASSWORD_PARAM = "password";
    public static final String DEFAULT_REMEMBER_ME_PARAM = "rememberMe";

    private String usernameParam = DEFAULT_USERNAME_PARAM;
    private String passwordParam = DEFAULT_PASSWORD_PARAM;
    private String rememberMeParam = DEFAULT_REMEMBER_ME_PARAM;

    private final ObjectMapper objectMapper;

    public RestLoginFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        try {
            Subject subject = SecurityUtils.getSubject();
            if (!subject.isAuthenticated()) {
                subject.login(createToken(request));
            }
            onLoginSuccess(subject, httpServletResponse);
        } catch (AuthenticationException e) {
            onLoginFailure(httpServletResponse, e);
        }

        return false;
    }

    protected void onLoginSuccess(Subject subject, HttpServletResponse httpServletResponse) {
        if (log.isInfoEnabled()) {
            log.info("用户【{}】登录成功", subject.getPrincipal());
        }
        httpServletResponse.setStatus(HttpStatus.OK.value());
    }

    protected void onLoginFailure(HttpServletResponse response, AuthenticationException e) throws Exception {
        ResultEnum resultEnum;
        if (e instanceof UnknownAccountException) {
            resultEnum = ResultEnum.UNKNOWN_ACCOUNT;
        } else if (e instanceof IncorrectCredentialsException) {
            resultEnum = ResultEnum.INCORRECT_CREDENTIALS;
        } else if (e instanceof LockedAccountException) {
            resultEnum = ResultEnum.LOCKED_ACCOUNT;
        } else if (e instanceof DisabledAccountException) {
            resultEnum = ResultEnum.DISABLED_ACCOUNT;
        } else {
            resultEnum = ResultEnum.UNKNOWN_ERROR;
        }

        String result = objectMapper.writeValueAsString(ResultBuilder.buildFailed(resultEnum));
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter pw = response.getWriter();
        pw.write(result);
        pw.flush();
    }

    protected AuthenticationToken createToken(ServletRequest request) {
        return new UsernamePasswordToken(getUsername(request), getPassword(request), isRememberMe(request), getHost(request));
    }

    protected String getHost(ServletRequest request) {
        return request.getRemoteHost();
    }

    protected boolean isRememberMe(ServletRequest request) {
        return WebUtils.isTrue(request, getRememberMeParam());
    }

    private String getUsername(ServletRequest request) {
        return WebUtils.getCleanParam(request, getUsernameParam());
    }

    private String getPassword(ServletRequest request) {
        return WebUtils.getCleanParam(request, getPasswordParam());
    }

    public String getUsernameParam() {
        return usernameParam;
    }

    public String getPasswordParam() {
        return passwordParam;
    }

    public String getRememberMeParam() {
        return rememberMeParam;
    }
}
