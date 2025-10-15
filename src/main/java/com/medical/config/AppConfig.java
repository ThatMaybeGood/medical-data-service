package com.medical.config;

/**
 * @author Mine
 * @version 1.0
 * 描述:应用配置类
 * @date 2025/10/13 13:35
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
