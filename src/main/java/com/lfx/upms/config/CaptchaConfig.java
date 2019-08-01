package com.lfx.upms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.lfx.upms.captcha.CaptchaManager;
import com.lfx.upms.captcha.EhcacheCaptchaManager;
import com.lfx.upms.shiro.filter.CaptchaApiFilter;
import com.lfx.upms.shiro.filter.CaptchaMatchFilter;
import com.lfx.upms.shiro.listener.CaptchaListener;
import net.sf.ehcache.Cache;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Properties;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-07-20 10:04
 */
@Configuration
@AutoConfigureAfter(EhCacheConfig.class)
public class CaptchaConfig {

    private final ObjectMapper objectMapper;

    @Autowired
    @Lazy
    public CaptchaConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "no");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.session.key", "code");
        properties.setProperty("kaptcha.textproducer.font.color", "red");
        properties.setProperty("kaptcha.noise.color", "red");
        properties.setProperty("kaptcha.textproducer.char.space", "4");
        properties.setProperty("kaptcha.image.width", "116");
        properties.setProperty("kaptcha.image.height", "41");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }

    /**
     * 是否开启验证码
     */
    @Value("#{ @environment['captcha.enabled'] ?: true }")
    private boolean captchaEnabled;

    /**
     * 小于该阀值，则不需要验证码校验
     */
    @Value("#{ @environment['captcha.showLoginFailedTimes'] ?: 6 }")
    private int showLoginFailedTimes;

    /**
     * 是否开启验证码
     */
    @Value("#{ @environment['captcha.cache.name'] ?: 'checkCodeCache' }")
    private String cacheName;

    /**
     * 是否开启验证码
     */
    @Value("#{ @environment['captcha.img.format'] ?: 'jpg' }")
    private String format;

    /**
     * 验证码参数name
     */
    @Value("#{ @environment['captcha.request.name'] ?: 'checkCode' }")
    private String captchaParamsName;

    @Bean
    public CaptchaManager ehCacheCaptchaManager(DefaultKaptcha defaultKaptcha, EhCacheManager ehCacheManager) {
        Cache cache = ehCacheManager.getCacheManager().getCache(cacheName);
        if (cache == null) {
            throw new RuntimeException("初始化EhcacheCaptchaManager发生异常: 缓存" + cacheName + "不存在");
        }
        return new EhcacheCaptchaManager(defaultKaptcha, cache, showLoginFailedTimes, format);
    }

    @Bean
    public CaptchaApiFilter captchaApiFilter(DefaultKaptcha defaultKaptcha, CaptchaManager captchaManager) {
        return new CaptchaApiFilter(captchaManager);
    }

    @Bean
    public CaptchaMatchFilter captchaMatchFilter(DefaultKaptcha defaultKaptcha, CaptchaManager captchaManager) {
        return new CaptchaMatchFilter(captchaEnabled, captchaManager, objectMapper, captchaParamsName);
    }

    @Bean
    public CaptchaListener captchaListener(CaptchaManager captchaManager) {
        return new CaptchaListener(captchaEnabled, captchaManager);
    }
}
