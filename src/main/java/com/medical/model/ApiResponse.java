package com.medical.model;

/**
 * @author Mine
 * @version 1.0
 * 描述:通用响应模型
 * @date 2025/10/13 13:30
 */
import lombok.Data;

@Data
public class ApiResponse {
    private Boolean success;
    private String errCode;
    private String errMsg;
    private String responseBiz;
    private String requestId;
    private String traceId;
}