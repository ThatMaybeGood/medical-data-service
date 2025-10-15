package com.medical.util;

/**
 * @author Mine
 * @version 1.0
 * 描述:加密工具类
 * @date 2025/10/13 13:32
 */
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.symmetric.SM4;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class CryptoUtils {

    @Value("${medical.sm4-key:726be1646879423e26eb7977c4d80c48}")
    private String sm4Key;

    @Value("${medical.sm2-private-key:a99d6978656355b570a19cc707b2ea68ed033c3fa436df9c0a15e84a06069c7b}")
    private String sm2PrivateKey;

    @Value("${medical.sm2-public-key:04b674b6edfd08f754410b21cc3c8b522f318a1f1b27403bc63a290b7e1790d03d967d06c46e69357793967ff42a34d77ed5d7bb40d31b7f5ab0b0840017cff114}")
    private String sm2PublicKey;

    /**
     * SM4加密 - 支持十六进制字符串和普通字符串密钥
     */
    public String sm4Encrypt(String content, String key) {
        try {
            byte[] sm4KeyByte;

            // 检查key是否为有效的十六进制字符串
            if (isValidHex(key)) {
                sm4KeyByte = HexUtil.decodeHex(key);
            } else {
                // 如果不是十六进制，直接使用字符串的字节
                sm4KeyByte = key.getBytes(StandardCharsets.UTF_8);
                // 如果长度不够16字节，进行填充
                if (sm4KeyByte.length < 16) {
                    byte[] paddedKey = new byte[16];
                    System.arraycopy(sm4KeyByte, 0, paddedKey, 0, sm4KeyByte.length);
                    sm4KeyByte = paddedKey;
                } else if (sm4KeyByte.length > 16) {
                    byte[] truncatedKey = new byte[16];
                    System.arraycopy(sm4KeyByte, 0, truncatedKey, 0, 16);
                    sm4KeyByte = truncatedKey;
                }
            }

            SM4 sm4 = SmUtil.sm4(sm4KeyByte);
            return sm4.encryptHex(content);
        } catch (Exception e) {
            log.error("SM4加密失败: content={}, key={}", content, key, e);
            throw new RuntimeException("SM4加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * SM4解密 - 支持十六进制字符串和普通字符串密钥
     */
    public String sm4Decrypt(String content, String key) {
        try {
            byte[] sm4KeyByte;

            // 检查key是否为有效的十六进制字符串
            if (isValidHex(key)) {
                sm4KeyByte = HexUtil.decodeHex(key);
            } else {
                // 如果不是十六进制，直接使用字符串的字节
                sm4KeyByte = key.getBytes(StandardCharsets.UTF_8);
                // 如果长度不够16字节，进行填充
                if (sm4KeyByte.length < 16) {
                    byte[] paddedKey = new byte[16];
                    System.arraycopy(sm4KeyByte, 0, paddedKey, 0, sm4KeyByte.length);
                    sm4KeyByte = paddedKey;
                } else if (sm4KeyByte.length > 16) {
                    byte[] truncatedKey = new byte[16];
                    System.arraycopy(sm4KeyByte, 0, truncatedKey, 0, 16);
                    sm4KeyByte = truncatedKey;
                }
            }

            SM4 sm4 = SmUtil.sm4(sm4KeyByte);
            return sm4.decryptStr(content);
        } catch (Exception e) {
            log.error("SM4解密失败: content={}, key={}", content, key, e);
            throw new RuntimeException("SM4解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查字符串是否为有效的十六进制
     */
    private boolean isValidHex(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        // 十六进制字符串应该只包含0-9, a-f, A-F，且长度为偶数
        return str.matches("^[0-9a-fA-F]+$") && str.length() % 2 == 0;
    }

    /**
     * SM2私钥签名
     */
    public String sm2Sign(String privateKey, String content) {
        try {
            SM2 sm2 = new SM2(privateKey, null);
            return sm2.signHex(HexUtil.encodeHexStr(content));
        } catch (Exception e) {
            log.error("SM2签名失败", e);
            throw new RuntimeException("SM2签名失败", e);
        }
    }

    /**
     * SM2公钥验签
     */
    public boolean sm2Verify(String publicKey, String content, String sign) {
        try {
            SM2 sm2 = new SM2(null, publicKey);
            return sm2.verifyHex(HexUtil.encodeHexStr(content), sign);
        } catch (Exception e) {
            log.error("SM2验签失败", e);
            return false;
        }
    }

    /**
     * 生成预签名字符串
     */
    public String buildPreSignature(Map<String, Object> header, String sm4Key) {
        if (header == null || StrUtil.isBlank(sm4Key)) {
            return null;
        }

        List<String> keys = new ArrayList<>(header.keySet());
        Collections.sort(keys); // 按ASCII升序排序

        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if ("signature".equals(key) || "X-Token".equals(key) || "Content-Type".equals(key)) {
                continue; // 跳过不参与签名的字段
            }

            Object value = header.get(key);
            if (value != null && StrUtil.isNotBlank(value.toString())) {
                sb.append(key).append("=").append(value).append("&");
            }
        }

        // 去掉最后一个&并拼接SM4密钥
//        if (sb.length() > 0) {
//            sb.deleteCharAt(sb.length() - 1);
//        }
        // 拼接SM4密钥，此处去掉注释的代码，直接在最后追加sm4Key
        sb.append(sm4Key);

        return sb.toString();
    }

    /**
     * 使用默认密钥的SM4加密
     */
    public String sm4EncryptWithDefaultKey(String content) {
        return sm4Encrypt(content, this.sm4Key);
    }

    /**
     * 使用默认密钥的SM4解密
     */
    public String sm4DecryptWithDefaultKey(String content) {
        return sm4Decrypt(content, this.sm4Key);
    }
}