package com.medical.controller;

/**
 * @author Mine
 * @version 1.0
 * 描述:
 * @date 2025/10/13 15:38
 */

import com.medical.util.CryptoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    private final CryptoUtils cryptoUtils;

    public TestController(CryptoUtils cryptoUtils) {
        this.cryptoUtils = cryptoUtils;
    }

    /**
     * 测试加密功能
     */
    @GetMapping("/crypto")
    public Map<String, Object> testCrypto(@RequestParam(defaultValue = "test message") String message) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 测试SM4加密
            String encrypted = cryptoUtils.sm4EncryptWithDefaultKey(message);
            String decrypted = cryptoUtils.sm4DecryptWithDefaultKey(encrypted);

            result.put("original", message);
            result.put("encrypted", encrypted);
            result.put("decrypted", decrypted);
            result.put("success", true);
            result.put("message", "加密测试成功");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "加密测试失败: " + e.getMessage());
            result.put("error", e.toString());
        }

        return result;
    }
}