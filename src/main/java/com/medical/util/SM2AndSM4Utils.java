package com.medical.util;


import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.symmetric.SM4;
import com.alibaba.fastjson.JSONObject;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;

import io.micrometer.common.util.StringUtils;
import java.util.*;

/**
 * @author Mine
 * @version 1.0
 * 描述:
 * @date 2025/10/15 10:46
 */
public class SM2AndSM4Utils {
        /**
         * 公钥常量
         */
       public static final String KEY_PUBLIC_KEY = "publicKey";
       /**
        * 私钥返回值常量
        */
       public static final String KEY_PRIVATE_KEY = "privateKey";


        public static void main(String [] args) throws Exception {
            // sm4秘钥
            String SM4_KEY = "726be1646879423e26eb7977c4d80c48";
            // sm2公钥
            String SM2_PUBLIC_KEY = "04b674b6edfd08f754410b21cc3c8b522f318a1f1b27403bc63a290b7e1790d03d967d06c46e69357793967ff42a34d77ed5d7bb40d31b7f5ab0b0840017cff114";
            // sm2私钥
            String SM2_PRIVATE_KEY = "a99d6978656355b570a19cc707b2ea68ed033c3fa436df9c0a15e84a06069c7b";

            // 数据集编码  预约记录
            String YYJL_DATASET_CODE = "HDSD.YYJL";
            // 数据集编码  挂号记录
            String GHJL_DATASET_CODE = "HDSD.GHJL";
            // 数据集编码  ⻔诊就诊记录
            String MZJZJL_DATASET_CODE = "HDSD.MZJZJL";
            // 交易编码  上传
            String INSERT_TRADE_CODE = "1001";
            // 交易编码  更新
            String UPDATE_TRADE_CODE = "1002";

            // 毫秒时间戳
            Long timeStamp = System.currentTimeMillis();
            // Header参数
            Map<String, Object> header = new HashMap<>();
            header.put("tradeCode", INSERT_TRADE_CODE);
            header.put("datasetCode", YYJL_DATASET_CODE);
            header.put("requestId", UUID.randomUUID().toString());
            header.put("medOrgCode", "med_org_code_001");
            header.put("medHosCode", "med_hos_code_001");
            header.put("platformCode", "platform_code_001");
            header.put("timestamp", timeStamp);
            // 预签名字符串
            String preSignature = buildPreSignature(header, SM4_KEY);
            System.out.println("预签名字符串：" + preSignature);


            // 签名  （ sm2私钥 ,预签名内容）
            String signature = SM2AndSM4Utils.sm2Sign(SM2_PRIVATE_KEY, preSignature);
            header.put("signature", signature);
            System.out.println("signature：" + signature);
            // 验签
            boolean verify = SM2AndSM4Utils.sm2Verify(SM2_PUBLIC_KEY, preSignature, signature);
            System.out.println("verify结果：" + verify);
            System.out.println("--------------------------------↑sm2加签↑---- ----------------------------");
            System.out.println("--------------------------------↓sm4加密↓---- ----------------------------");
            // 请求对象json
            String content = "{ \n" +
                    "\"requestBiz\":{\n" +
                    "\"cfbh\":\"2022082066316802\",\n" +
                    "\"loadTime\":\"20220101120809\"}\n" +
                    "}";

            System.out.println("sm4加密前字符串：" + content);
            // 加密
            String encryptBody = SM2AndSM4Utils.sm4EncryptBase64(content, SM4_KEY);
            System.out.println("sm4加密后字符串：" + encryptBody);
            // 解密
            String decryptoy = SM2AndSM4Utils.sm4DecryptBase64(encryptBody,SM4_KEY);
            System.out.println("sm4解密后字符串：" + decryptoy);
        }

    /**
     * ⽣成SM2公私钥 *
     * @return */
    public static JSONObject generateSm2Key() {
        SM2 sm2 = new SM2();
        ECPublicKey publicKey = (ECPublicKey) sm2.getPublicKey();
        ECPrivateKey privateKey = (ECPrivateKey) sm2.getPrivateKey();
        // 获取公钥
        byte[] publicKeyBytes = publicKey.getQ().getEncoded(false);
        String publicKeyHex = HexUtil.encodeHexStr(publicKeyBytes);
        // 获取64位私钥
        String privateKeyHex = privateKey.getD().toString(16);
        // BigInteger转成16进制时，不—定⻓度为64，如果私钥⻓度⼩于64，则在前⽅补0
        StringBuilder privateKey64 = new StringBuilder(privateKeyHex);
        while (privateKey64.length() < 64) {
            privateKey64.insert(0, "0");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_PUBLIC_KEY, publicKeyHex);
        jsonObject.put(KEY_PRIVATE_KEY, privateKey64.toString());
        return jsonObject;
    }

    /**
     * SM2私钥签名 *
     * @param privateKey 私钥
     * @param content    待签名内容
     * @return 签名值 */
    public static String sm2Sign(String privateKey, String content) {
        // 确保私钥是有效的16进制字符串
        if (privateKey == null || privateKey.isEmpty()) {
            throw new IllegalArgumentException("Private key cannot be null or empty");
        }
        // 将16进制私钥字符串转换为字节数组
        byte[] privateKeyBytes = HexUtil.decodeHex(privateKey);

        SM2 sm2 = new SM2(privateKeyBytes, null);
        return sm2.signHex(HexUtil.encodeHexStr(content));
    }

    /**
     * SM2公钥验签果 */
    public static boolean sm2Verify(String publicKey, String content, String sign) {

        SM2 sm2 = new SM2(null, publicKey);
        return sm2.verifyHex(HexUtil.encodeHexStr(content), sign);
    }

    /**
     * SM2公钥加密 *
     * @param content   原⽂
     * @param publicKey SM2公钥
     * @return */
    public static String sm2EncryptBase64(String content, String publicKey) {
        SM2 sm2 = new SM2(null, publicKey);
        return sm2.encryptBase64(content, KeyType.PublicKey);
    }
    /**
     * SM2私钥解密 *
     * @param encryptStr SM2加密字符串
     * @param privateKey SM2私钥
     * @return */
    public static String sm2DecryptBase64(String encryptStr, String privateKey) {
        SM2 sm2 = new SM2(privateKey, null);
        return StrUtil.utf8Str(sm2.decrypt(encryptStr, KeyType.PrivateKey
        ));
    }

    /**
     * sm4密钥⽣成 *
     * @return sm4密钥 */
    public static String generateSm4Key() {
        SM4 sm4 = SmUtil.sm4();
        byte[] encoded = sm4.getSecretKey().getEncoded();

        return HexUtil.encodeHexStr(encoded);
    }

    /**
     * SM4加密 *
     * @param content 原⽂
     * @param key     SM4秘钥
     * @return */
    public static String sm4EncryptBase64(String content, String key) {
        byte[] sm4KeyByte = HexUtil.decodeHex(key);
        String encryptBody = SmUtil.sm4(sm4KeyByte).encryptHex(content);
        return encryptBody; }

    /**
     * SM4解密 *
     * @param content SM4加密字符串
     * @param key     SM4秘钥
     * @return */
    public static String sm4DecryptBase64(String content, String key) {
        byte[] sm4KeyByte = HexUtil.decodeHex(key);
        String decryptBody = SmUtil.sm4(sm4KeyByte).decryptStr(content);
        return decryptBody;
    }

    /**
     * ⽣成预签名字符串 *
     * @param header
     * @param sm4Key
     * @return */
    public static String buildPreSignature(Map<String, Object> header, String sm4Key) {
        if (null == header || StringUtils.isBlank(sm4Key)) {
            return null;
        }
        List<String> keys = new ArrayList<>(header.keySet());
        Collections.sort(keys); // 按ASCII升序排序
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            Object value = header.get(key);
            // 处理null和不同类型值
            String valueStr = (value != null) ? value.toString() : "";
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key).append("=").append(valueStr);
        }
        return sb.toString() + "&" + sm4Key;
    }
}

