package com.lfx.upms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-07-22 20:04
 */
@Data
@ConfigurationProperties(value = "shiro.custom")
public class CustomShiroProperties {

    private boolean kickoutable;

    private Cache cache;

    private List<String> filterChain;

    @Data
    public static class Cache {
        private String authorizationName;
        private String sessionName;
        private String kickoutName;
    }

}
