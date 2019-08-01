package com.lfx.upms.shiro.filter;

import com.google.common.net.HttpHeaders;
import com.lfx.upms.captcha.CaptchaManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.http.MediaType;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-04-29 14:54
 */
@Slf4j
public class CaptchaApiFilter extends PathMatchingFilter {

    private final CaptchaManager captchaManager;

    public CaptchaApiFilter(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    private String cacheControl = "no-store";
    private String pragma = "no-cache";

    @Override
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
        httpServletResponse.setHeader(HttpHeaders.PRAGMA, pragma);
        httpServletResponse.setDateHeader(HttpHeaders.EXPIRES, 0);
        httpServletResponse.setContentType(MediaType.IMAGE_JPEG_VALUE);

        String remoteHost = request.getRemoteHost();

        byte[] captchaChallengeAsJpeg = captchaManager.createImageBytes(remoteHost);

        ServletOutputStream responseOutputStream =
                httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();

        return false;
    }

}
