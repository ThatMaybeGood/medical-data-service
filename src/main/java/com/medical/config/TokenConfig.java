package com.medical.config;

/**
 * @author Mine
 * @version 1.0
 * 描述:
 * @date 2025/10/13 15:58
 */

import com.medical.service.MockTokenService;
import com.medical.service.TokenService;
import com.medical.util.CryptoUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TokenConfig {

    @Bean
    @ConditionalOnProperty(name = "medical.mode", havingValue = "prod")
    public TokenService tokenService(RestTemplate restTemplate, CryptoUtils cryptoUtils) {
        return new TokenService(restTemplate, cryptoUtils);
    }

    @Bean
    @ConditionalOnMissingBean(TokenService.class)
    public MockTokenService mockTokenService() {
        return new MockTokenService();
    }
}