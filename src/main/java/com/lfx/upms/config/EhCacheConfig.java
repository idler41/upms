package com.lfx.upms.config;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-07-20 10:05
 */
@Configuration
public class EhCacheConfig {

    @Value("#{ @environment['ehcache.config'] ?: 'classpath:ehcache.xml' }")
    private String ehCachePath;

    @Bean
    public EhCacheManager ehCacheManager() {
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile(ehCachePath);
        return cacheManager;
    }

}
