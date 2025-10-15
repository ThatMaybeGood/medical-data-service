package com.medical.util;

/**
 * @author Mine
 * @version 1.0
 * 描述:
 * @date 2025/10/14 20:06
 */
import org.bouncycastle.asn1.*;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

public class SM2SignatureUtil {

    private static final String SM2_CURVE_NAME = "sm2p256v1";

    /**
     * 生成SM2签名
     * @param privateKeyHex 私钥十六进制字符串
     * @param sourceData 待签名数据
     * @return 签名结果的十六进制字符串
     */
    public static String generateSM2Signature(String privateKeyHex, String sourceData) {
        try {
            // 获取SM2曲线参数
            ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec(SM2_CURVE_NAME);

            // 解析私钥
            BigInteger privateKey = new BigInteger(privateKeyHex, 16);
            ECPrivateKeyParameters privateKeyParams = new ECPrivateKeyParameters(
                    privateKey,
                    new ECDomainParameters(
                            sm2Spec.getCurve(),
                            sm2Spec.getG(),
                            sm2Spec.getN(),
                            sm2Spec.getH()
                    )
            );

            // 创建SM2签名器
            SM2Signer signer = new SM2Signer();
            signer.init(true, new ParametersWithRandom(privateKeyParams, new SecureRandom()));

            // 计算签名
            byte[] message = sourceData.getBytes("UTF-8");
            signer.update(message, 0, message.length);
            byte[] signature = signer.generateSignature();

            // 将签名结果转换为ASN.1编码
            return Hex.toHexString(signature);

        } catch (Exception e) {
            throw new RuntimeException("SM2签名生成失败", e);
        }
    }

    /**
     * 验证SM2签名
     * @param publicKeyHex 公钥十六进制字符串
     * @param sourceData 原始数据
     * @param signatureHex 签名十六进制字符串
     * @return 验证结果
     */
    public static boolean verifySM2Signature(String publicKeyHex, String sourceData, String signatureHex) {
        try {
            // 获取SM2曲线参数
            ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec(SM2_CURVE_NAME);

            // 解析公钥
            byte[] publicKeyBytes = Hex.decode(publicKeyHex);
            ECPoint publicKeyPoint = sm2Spec.getCurve().decodePoint(publicKeyBytes);
            ECPublicKeyParameters publicKeyParams = new ECPublicKeyParameters(
                    publicKeyPoint,
                    new ECDomainParameters(
                            sm2Spec.getCurve(),
                            sm2Spec.getG(),
                            sm2Spec.getN(),
                            sm2Spec.getH()
                    )
            );

            // 创建SM2签名器
            SM2Signer signer = new SM2Signer();
            signer.init(false, publicKeyParams);

            // 验证签名
            byte[] message = sourceData.getBytes("UTF-8");
            byte[] signature = Hex.decode(signatureHex);
            signer.update(message, 0, message.length);

            return signer.verifySignature(signature);

        } catch (Exception e) {
            throw new RuntimeException("SM2签名验证失败", e);
        }
    }

    /**
     * 根据示例格式生成签名
     */
    public static String generateSignatureByExample() {
        // 示例参数
        String privateKey = "a99d6978656355b570a19cc707b2ea68ed033c3fa436df9c0a15e84a06069c7b";
        String sm4Key = "726be1646879423e26eb7977c4d80c48";

        // 构建待签名字符串（注意：这里需要按照实际参数顺序拼接）
        String sourceData = "datasetCode=HDSD.YYJL&medHosCode=med_hos_code_001&medOrgCode=med_org_code_001&requestId=1408c07c-bfbb-4135-a516-9232ac0a3bbf&tradeCode=1001&" + sm4Key;

        System.out.println("待签名字符串: " + sourceData);

        // 生成签名
        String signature = generateSM2Signature(privateKey, sourceData);
        System.out.println("生成的签名: " + signature);

        return signature;
    }

//    public static void main(String[] args) {
//        // 测试示例
//        String signature = generateSignatureByExample();
//
//        // 验证签名
//        String publicKey = "04b674b6edfd08f754410b21cc3c8b522f318a1f1b27403bc63a290b7e1790d03d967d06c46e69357793967ff42a34d77ed5d7bb40d31b7f5ab0b0840017cff114";
//        String sm4Key = "726be1646879423e26eb7977c4d80c48";
//        String sourceData = "datasetCode=HDSD.YYJL&medHosCode=med_hos_code_001&medOrgCode=med_org_code_001&requestId=1408c07c-bfbb-4135-a516-9232ac0a3bbf&tradeCode=1001&" + sm4Key;
//
//        boolean isValid = verifySM2Signature(publicKey, sourceData, signature);
//        System.out.println("签名验证结果: " + isValid);
//    }
}
