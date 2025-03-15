package com.example.common;

import org.springframework.web.filter.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 允许前端 Vue 的地址（推荐使用前端的具体域名，而不是 "*"）
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedHeader("*"); // 允许所有请求头
        corsConfiguration.addAllowedMethod("*"); // 允许所有请求方法（GET, POST, PUT, DELETE, OPTIONS）

        // 允许跨域携带 Cookie
        corsConfiguration.setAllowCredentials(true);

        // 允许 Content-Type 为 multipart/form-data
        corsConfiguration.addExposedHeader("Content-Type");

        source.registerCorsConfiguration("/**", corsConfiguration); // 应用于所有请求
        return new CorsFilter(source);
    }
}
