package com.medical.model;

/**
 * @author Mine
 * @version 1.0
 * 描述:通用请求模型
 * @date 2025/10/13 13:30
 */
import lombok.Data;

@Data
public class ApiRequest {
    private RequestHeader header;
    private String requestBiz;
}