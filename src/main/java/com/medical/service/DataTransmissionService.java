package com.medical.service;

/**
 * @author Mine
 * @version 1.0
 * 描述:数据服务实现
 * @date 2025/10/13 13:33
 */

import com.alibaba.fastjson.JSON;
import com.medical.constant.DataCodeConstants;
import com.medical.constant.TradeCodeConstants;
import com.medical.model.ApiRequest;
import com.medical.model.ApiResponse;
import com.medical.model.RequestHeader;
import com.medical.util.CryptoUtils;
import com.medical.util.SM2AndSM4Utils;
import com.medical.util.SM2SignatureUtil;
import com.medical.util.crypto.SM3Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class DataTransmissionService {

    @Value("${medical.platform.base-url}")
    private String baseUrl;

    @Value("${medical.sm4-key}")
    private String sm4Key;

    @Value("${medical.sm2-private-key}")
    private String sm2PrivateKey;

    @Value("${medical.sm2-public-key}")
    private String sm2PublicKey;




    private final RestTemplate restTemplate;
    private final CryptoUtils cryptoUtils;

    //临时模拟的token
    private final MockTokenService tokenService;

    //    private final TokenService tokenService;


    public DataTransmissionService(RestTemplate restTemplate, CryptoUtils cryptoUtils, MockTokenService tokenService) {
        this.restTemplate = restTemplate;
        this.cryptoUtils = cryptoUtils;
        this.tokenService = tokenService;
    }

    /**
     * 数据上传服务
     */
    public ApiResponse uploadData(String tradeCode, String datasetCode, String medOrgCode,String medHosCode,String platformCode, Object businessData) {
        try {
            // 1. 构建请求头
            RequestHeader header = buildRequestHeader(tradeCode, datasetCode, medOrgCode, medHosCode,platformCode);

            // 2. 加密业务数据
            String requestBizJson = JSON.toJSONString(businessData);
            log.info(requestBizJson);

            // 2. 使用SM3算法对业务数据进行加密，
//            String encryptedRequestBiz = cryptoUtils.sm4Encrypt(requestBizJson, sm4Key);
            String encryptedRequestBiz = SM3Utils.hmac(sm4Key, requestBizJson);

            // 3. 构建请求体
            ApiRequest apiRequest = new ApiRequest();
            apiRequest.setHeader(header);
            apiRequest.setRequestBiz(encryptedRequestBiz);
            log.info(JSON.toJSONString(apiRequest));

            /*:
            应用程序后台通过 AppKey 及 AppSecret，通过 SM3 算法签名后调用平台接口，获取临时应用授权 Token
            */

            // 4. 发送请求
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            // 添加token头信息,此处用的是mock的token服务，实际应用中应该换成真实的token获取方式
            httpHeaders.set("X-Token", tokenService.getToken());

            HttpEntity<ApiRequest> request = new HttpEntity<>(apiRequest, httpHeaders);

            ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                    baseUrl, request, ApiResponse.class);


            if (response.getStatusCode() == HttpStatus.OK) {
                ApiResponse apiResponse = response.getBody();
                if (apiResponse != null && apiResponse.getSuccess()) {
                    // 解密响应业务数据
                    String decryptedResponse = cryptoUtils.sm4Decrypt(
                            apiResponse.getResponseBiz(), sm4Key);
                    log.info("响应业务数据: {}", decryptedResponse);
                }
                return apiResponse;
            }

            log.error("数据上传失败: {}", response.getStatusCode());
            throw new RuntimeException("数据上传失败");

        } catch (Exception e) {
            log.error("数据上传异常", e);
            throw new RuntimeException("数据上传异常", e);
        }
    }

    /**
     * 构建请求头并签名
     */
    private RequestHeader buildRequestHeader(String tradeCode, String datasetCode,
                                             String medOrgCode, String medHosCode,String platformCode) {
        RequestHeader header = new RequestHeader();
        header.setTradeCode(tradeCode);
        header.setDatasetCode(datasetCode);
        header.setRequestId(UUID.randomUUID().toString().replace("-", ""));
        header.setMedOrgCode(medOrgCode);
        header.setMedHosCode(medHosCode);
        header.setPlatformCode(platformCode);
        header.setTimestamp(String.valueOf(System.currentTimeMillis()));


        // 生成签名
        Map<String, Object> headerMap = new HashMap<>();
//        headerMap.put("contentType", header.getContentType());
        headerMap.put("tradeCode", header.getTradeCode());
        headerMap.put("datasetCode", header.getDatasetCode());
        headerMap.put("requestId", header.getRequestId());
        headerMap.put("medOrgCode", header.getMedOrgCode());
        headerMap.put("medHosCode", header.getMedHosCode());
        headerMap.put("platformCode", header.getPlatformCode());
        headerMap.put("timestamp", header.getTimestamp());

//        String signature = cryptoUtils.sm2Sign(sm2PrivateKey, preSignature);

//        String preSignature = SM2AndSM4Utils.buildPreSignature(headerMap, sm4Key);
//        log.info("===============签名前数据: {}", preSignature);
        // 生成签名
//        String signature = SM2SignatureUtil.generateSM2Signature(sm2PrivateKey, preSignature);
//        // 验证签名
//        boolean isValid = SM2SignatureUtil.verifySM2Signature(sm2PublicKey, preSignature, signature);

        /**
        *使用提供的签名
        *加解密代码示例
         *
         */
        // 1、构建预签名字符串
        String preSignature = SM2AndSM4Utils.buildPreSignature(headerMap, sm4Key);
        log.info("===============签名前数据: {}", preSignature);

        //2、通过私钥签名
        String signature = SM2AndSM4Utils.sm2Sign(sm2PrivateKey, preSignature);
        //添加到signature字段
        header.setSignature(signature);
        log.info("signature：" + signature);

        // 公钥验证签名
        boolean isValid = SM2AndSM4Utils.sm2Verify(sm2PublicKey, preSignature, signature);
        log.info("===============签名验证结果================: " + isValid);

        log.info("--------------------------------↑sm2加签↑---------------------------------");


        return header;
    }

    /**
     * 上传预约记录
     */
    public ApiResponse uploadAppointmentRecord(String medOrgCode, String medHosCode,String platformCode,
                                               Object appointmentData) {
        return uploadData(TradeCodeConstants.UP_INSERT_TRADE_CODE, DataCodeConstants.YYJL_DATASET_CODE,
                medOrgCode, medHosCode,platformCode, appointmentData);
    }

    /**
     * 上传挂号记录
     */
    public ApiResponse uploadRegistrationRecord(String medOrgCode, String medHosCode,String platformCode,
                                                Object registrationData) {
        return uploadData(TradeCodeConstants.UP_INSERT_TRADE_CODE, DataCodeConstants.GHJL_DATASET_CODE, medOrgCode, medHosCode,platformCode, registrationData);
    }





}