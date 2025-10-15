package com.medical.service;

/**
 * @author Mine
 * @version 1.0
 * 描述:Token管理服务
 * @date 2025/10/13 13:32
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.medical.util.CryptoUtils;
import com.medical.util.crypto.SM3Utils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TokenService {

    @Value("${medical.platform.token-url}")
    private String tokenUrl;

    @Value("${medical.app-key}")
    private String appKey;

    @Value("${medical.app-secret}")
    private String appSecret;

    private final RestTemplate restTemplate;
    private final CryptoUtils cryptoUtils;

    private String cachedToken;
    private long tokenExpireTime;

    public TokenService(RestTemplate restTemplate, CryptoUtils cryptoUtils) {
        this.restTemplate = restTemplate;
        this.cryptoUtils = cryptoUtils;
    }

    /**
     * 获取Token（带缓存）
     */
    public String getToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return cachedToken;
        }

        return refreshToken();
    }

    /**
     * 刷新Token
     */
    public synchronized String refreshToken() {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("app_key", appKey);
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            requestBody.put("timestamp", timestamp);

            // 生成签名 - 根据文档使用SM3_HMAC，这里先用简单方式处理
            String signContent = appKey + timestamp;

            // 根据文档说明，这里应该使用SM3_HMAC算法
            // 由于没有具体的SM3_HMAC实现，这里先用SM4加密作为临时方案
            String appSign;
            try {
                appSign = SM3Utils.hmac(appSecret,signContent);
            } catch (Exception e) {
                log.warn("SM4加密失败，使用简单哈希作为替代方案", e);
                // 如果SM4加密失败，使用简单哈希作为临时方案
                appSign = Integer.toHexString(signContent.hashCode());
            }

            requestBody.put("app_sign", appSign);

            log.info("Token请求参数: app_key={}, timestamp={}, app_sign={}",
                    appKey, timestamp, appSign);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            log.info("发送Token请求到: {}", tokenUrl);
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

            log.info("Token响应状态: {}, 响应体: {}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject responseBody = JSON.parseObject(response.getBody());
                if (responseBody.getInteger("code") == 200) {
                    JSONObject data = responseBody.getJSONObject("data");
                    cachedToken = data.getString("token");
                    tokenExpireTime = data.getLong("expire_in") * 1000;
                    log.info("Token获取成功，有效期至: {}", tokenExpireTime);
                    return cachedToken;
                } else {
                    log.error("Token获取失败，错误代码: {}, 错误信息: {}",
                            responseBody.getInteger("code"), responseBody.getString("message"));
                }
            }

            log.error("Token获取失败: {}", response.getBody());
            throw new RuntimeException("Token获取失败");

        } catch (Exception e) {
            log.error("Token获取异常", e);
            throw new RuntimeException("Token获取异常: " + e.getMessage(), e);
        }
    }

    /**
     * 清除缓存的Token
     */
    public void clearToken() {
        cachedToken = null;
        tokenExpireTime = 0;
    }

    @PostConstruct
    public void init() {
        // 应用启动时预获取Token
        try {

//            refreshToken();

        } catch (Exception e) {
            log.warn("启动时Token获取失败，将在首次使用时重试", e);
        }
    }
}