package com.medical.service;

/**
 * @author Mine
 * @version 1.0
 * 描述:支持离线开发的 TokenService 版本
 * @date 2025/10/13 15:56
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "medical.mode", havingValue = "dev", matchIfMissing = true)
public class MockTokenService {

    private String cachedToken;
    private long tokenExpireTime;

    /**
     * 模拟Token获取 - 用于开发测试
     */
    public String getToken() {
        if (cachedToken == null || System.currentTimeMillis() >= tokenExpireTime) {
            // 生成模拟Token
            cachedToken = "mock-token-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            tokenExpireTime = System.currentTimeMillis() + 23 * 60 * 60 * 1000; // 23小时有效期
            log.info("生成模拟Token: {}, 有效期至: {}", cachedToken, tokenExpireTime);
        }
        return cachedToken;
    }

    /**
     * 模拟刷新Token
     */
    public String refreshToken() {
        cachedToken = null;
        return getToken();
    }
}