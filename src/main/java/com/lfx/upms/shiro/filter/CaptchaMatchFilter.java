package com.lfx.upms.shiro.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lfx.upms.captcha.CaptchaManager;
import com.lfx.upms.enums.ResultEnum;
import com.lfx.upms.result.ResultBuilder;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.http.MediaType;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-04-29 14:54
 */
public class CaptchaMatchFilter extends AccessControlFilter {

    private final boolean captchaEnabled;

    private final CaptchaManager captchaManager;

    private final ObjectMapper objectMapper;

    private final String captchaParamsName;

    public CaptchaMatchFilter(boolean captchaEnabled, CaptchaManager captchaManager, ObjectMapper objectMapper, String captchaParamsName) {
        this.captchaEnabled = captchaEnabled;
        this.captchaManager = captchaManager;
        this.objectMapper = objectMapper;
        this.captchaParamsName = captchaParamsName;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        boolean result = true;
        if (captchaEnabled) {
            String remoteHost = request.getRemoteHost();
            String captchaParams = request.getParameter(captchaParamsName);

            try {
                result = !captchaManager.needVerify(remoteHost) || captchaManager.verify(remoteHost, captchaParams);
            } catch (IllegalStateException ex) {
                request.setAttribute(reason, ResultEnum.CHECKED_CODE_NEED);
                result = false;
            }
        }

        return result;
    }

    private String reason = "error_reason";

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        ResultEnum resultEnum = (ResultEnum) request.getAttribute(reason);
        resultEnum = resultEnum == null ? ResultEnum.CHECKED_CODE_ERROR : resultEnum;
        String result = objectMapper.writeValueAsString(ResultBuilder.buildFailed(resultEnum));

        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter out = response.getWriter();
        out.println(result);
        out.flush();
        out.close();
        return false;
    }

}
