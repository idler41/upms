package com.lfx.upms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lfx.upms.service.PermissionService;
import com.lfx.upms.service.UserService;
import com.lfx.upms.shiro.filter.CaptchaApiFilter;
import com.lfx.upms.shiro.filter.CaptchaMatchFilter;
import com.lfx.upms.shiro.filter.RestLoginFilter;
import com.lfx.upms.shiro.filter.RestLogoutFilter;
import com.lfx.upms.shiro.filter.RestUserFilter;
import com.lfx.upms.shiro.listener.CaptchaListener;
import com.lfx.upms.shiro.listener.EhCacheKickoutListener;
import com.lfx.upms.shiro.listener.SessionListener;
import com.lfx.upms.shiro.matcher.PasswordEncoderMatcher;
import com.lfx.upms.shiro.realm.MybatisRealm;
import com.lfx.upms.shiro.session.WebSessionManager;
import net.sf.ehcache.Cache;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.SimpleSessionFactory;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-04-29 21:19
 */
@Configuration
@AutoConfigureAfter(CaptchaConfig.class)
@EnableConfigurationProperties(CustomShiroProperties.class)
public class ShiroConfig {

    private final ObjectMapper objectMapper;

    private final UserService userService;

    private final PermissionService permissionService;

    @Autowired
    @Lazy
    public ShiroConfig(ObjectMapper objectMapper, UserService userService, PermissionService permissionService) {
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthorizingRealm realm(PasswordEncoder passwordEncoder, CustomShiroProperties customShiroProperties) {
        MybatisRealm realm = new MybatisRealm(userService, permissionService);
        realm.setCredentialsMatcher(new PasswordEncoderMatcher(passwordEncoder));
        realm.setCachingEnabled(true);
        realm.setAuthenticationCachingEnabled(false);
        realm.setAuthorizationCachingEnabled(true);
        realm.setAuthorizationCacheName(customShiroProperties.getCache().getAuthorizationName());
        return realm;
    }

    @Bean
    public SessionDAO sessionDAO(EhCacheManager ehCacheManager, CustomShiroProperties customShiroProperties) {
        EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
        sessionDAO.setCacheManager(ehCacheManager);
        sessionDAO.setActiveSessionsCacheName(customShiroProperties.getCache().getSessionName());
        return sessionDAO;
    }

    @Bean
    public SessionManager sessionManager(SessionDAO sessionDAO) {
        WebSessionManager webSessionManager = new WebSessionManager();
        webSessionManager.setSessionIdCookieEnabled(sessionIdCookieEnabled);
        webSessionManager.setSessionIdUrlRewritingEnabled(sessionIdUrlRewritingEnabled);
        webSessionManager.setSessionIdCookie(sessionCookie());
        webSessionManager.setSessionFactory(new SimpleSessionFactory());
        webSessionManager.setSessionDAO(sessionDAO);
        webSessionManager.setGlobalSessionTimeout(globalSessionTimeout);
        webSessionManager.setDeleteInvalidSessions(sessionManagerDeleteInvalidSessions);
        webSessionManager.setSessionValidationSchedulerEnabled(validationSchedulerEnabled);
        webSessionManager.setSessionValidationInterval(validationInterval);
        return webSessionManager;
    }

    @Bean
    public SessionListener sessionListener() {
        return new SessionListener();
    }

    @Bean
    public EhCacheKickoutListener ehCacheKickoutListener(EhCacheManager ehCacheManager, CustomShiroProperties customShiroProperties) {
        String kickoutCacheName = customShiroProperties.getCache().getKickoutName();
        Cache kickoutCache = ehCacheManager.getCacheManager().getCache(kickoutCacheName);
        if (kickoutCache == null) {
            throw new RuntimeException("初始化EhCacheKickoutListener发生异常: 缓存" + kickoutCacheName + "不存在");
        }

        String sessionCacheName = customShiroProperties.getCache().getSessionName();
        Cache sessionCache = ehCacheManager.getCacheManager().getCache(sessionCacheName);
        if (sessionCache == null) {
            throw new RuntimeException("初始化EhCacheKickoutListener发生异常: 缓存" + sessionCacheName + "不存在");
        }
        boolean kickoutable = customShiroProperties.isKickoutable();
        return new EhCacheKickoutListener(kickoutable, sessionCache, kickoutCache);
    }

    @Bean
    public SessionsSecurityManager securityManager(
            AuthorizingRealm realm, SessionManager sessionManager, EhCacheManager ehCacheManager,
            SessionListener sessionListener, EhCacheKickoutListener ehCacheKickoutListener, CaptchaListener captchaListener
    ) {

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        ModularRealmAuthenticator authenticator = (ModularRealmAuthenticator) securityManager.getAuthenticator();

        // 主要: sessionListener必须放在ehCacheKickoutListener前面
        // 因为ehCacheKickoutListener需要从cache中读取userId，而userId是在sessionListener中设置的
        List<AuthenticationListener> listenerList = Arrays.asList(captchaListener, sessionListener, ehCacheKickoutListener);
        authenticator.setAuthenticationListeners(listenerList);
        securityManager.setRealm(realm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(ehCacheManager);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(
            SessionsSecurityManager securityManager, EhCacheManager ehCacheManager,
            CaptchaApiFilter captchaApiFilter, CaptchaMatchFilter captchaMatchFilter,
            CustomShiroProperties customShiroProperties

    ) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        shiroFilterFactoryBean.setSecurityManager(securityManager);

        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        customShiroProperties.getFilterChain().forEach(filterChainDefinition -> {
            String[] definitionElement = filterChainDefinition.split("=");
            filterChainDefinitionMap.put(definitionElement[0], definitionElement[1]);
        });

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("user", new RestUserFilter());
        filters.put("restLogin", new RestLoginFilter(objectMapper));
        filters.put("restLogout", new RestLogoutFilter());
        filters.put("captchaApiFilter", captchaApiFilter);
        filters.put("captchaMatchFilter", captchaMatchFilter);
        shiroFilterFactoryBean.setFilters(filters);
        return shiroFilterFactoryBean;
    }

    @Value("#{ @environment['shiro.sessionManager.globalSessionTimeout'] ?: 3600000 }")
    private long globalSessionTimeout;

    @Value("#{ @environment['shiro.sessionManager.validation.scheduler.enabled'] ?: true }")
    private boolean validationSchedulerEnabled;

    @Value("#{ @environment['shiro.sessionManager.validation.interval'] ?: 3600000 }")
    private long validationInterval;

    @Value("#{ @environment['shiro.sessionManager.deleteInvalidSessions'] ?: true }")
    private boolean sessionManagerDeleteInvalidSessions;

    /**
     * Session Cookie info
     */
    @Value("#{ @environment['shiro.sessionManager.sessionIdCookieEnabled'] ?: true }")
    private boolean sessionIdCookieEnabled;

    @Value("#{ @environment['shiro.sessionManager.sessionIdUrlRewritingEnabled'] ?: true }")
    private boolean sessionIdUrlRewritingEnabled;

    @Value("#{ @environment['shiro.sessionManager.cookie.name'] ?: T(org.apache.shiro.web.servlet.ShiroHttpSession).DEFAULT_SESSION_ID_NAME }")
    private String sessionIdCookieName;

    @Value("#{ @environment['shiro.sessionManager.cookie.maxAge'] ?: T(org.apache.shiro.web.servlet.SimpleCookie).DEFAULT_MAX_AGE }")
    private int sessionIdCookieMaxAge;

    @Value("#{ @environment['shiro.sessionManager.cookie.domain'] ?: null }")
    private String sessionIdCookieDomain;

    @Value("#{ @environment['shiro.sessionManager.cookie.path'] ?: null }")
    private String sessionIdCookiePath;

    @Value("#{ @environment['shiro.sessionManager.cookie.secure'] ?: false }")
    private boolean sessionIdCookieSecure;

    @Value("#{ @environment['shiro.sessionManager.cookie.httpOnly'] ?: true }")
    private boolean httpOnly;

    private Cookie sessionCookie() {
        Cookie cookie = new SimpleCookie(sessionIdCookieName);
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(sessionIdCookieMaxAge);
        cookie.setPath(sessionIdCookiePath);
        cookie.setDomain(sessionIdCookieDomain);
        cookie.setSecure(sessionIdCookieSecure);
        return cookie;
    }

}