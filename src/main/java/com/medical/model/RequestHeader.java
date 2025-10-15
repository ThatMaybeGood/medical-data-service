package com.medical.model;

/**
 * @author Mine
 * @version 1.0
 * 描述:请求头模型
 * @date 2025/10/13 13:29
 */
import lombok.Data;

@Data
public class RequestHeader {
    private String contentType = "application/json;charset=UTF-8";
    private String tradeCode;
    private String datasetCode;
    private String requestId;
    private String platformCode;
    private String medOrgCode;
    private String medHosCode;
    private String timestamp;
    private String signature;
    private String xToken;


}