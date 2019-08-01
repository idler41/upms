package com.lfx.upms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@SpringBootApplication(scanBasePackages = {"com.lfx.upms"})
@MapperScan(basePackages = {"com.lfx.upms.mapper"})
@EnableCaching
public class App extends SpringBootServletInitializer {

    public static void main(String[] args) {
//        for (int i = 11; i <= 2000; i++) {
////            System.out.println("(" + i + ",'user" + i + "','{bcrypt}$2a$10$Nftbz8YBxTV94dzEYHm5iuAWd9mTZPyCaW0xvkCkwEoIcLk1h53EC','用户" + i + "',unix_timestamp(now()) * 1000),");
//            System.out.println("(" + i + "," + (i % 2 == 0 ? 200 : 100) + ",1,unix_timestamp(now()) * 1000),");
//        }
//        for (int i = 1; i <= 2000; i++) {
//            System.out.println("user" + i + ",123456");
//        }
//        System.out.println(System.getProperty("java.io.tmpdir"));
        SpringApplication.run(App.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(App.class);
    }
}
