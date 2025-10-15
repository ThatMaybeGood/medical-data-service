package com.medical.controller;

/**
 * @author Mine
 * @version 1.0
 * 描述:WebService控制器
 * @date 2025/10/13 13:34
 */

import com.medical.model.ApiResponse;
import com.medical.model.AppointmentRecord;
import com.medical.service.DataTransmissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/data")
public class DataTransmissionController {

    private final DataTransmissionService dataService;

    public DataTransmissionController(DataTransmissionService dataService) {
        this.dataService = dataService;
    }

    /**
     * 预约记录上传接口
     */
    @PostMapping("/appointment/upload")
    public ApiResponse uploadAppointment(@RequestParam String medOrgCode,
                                         @RequestParam String medHosCode,
                                         @RequestParam String platformCode,
                                         @RequestBody AppointmentRecord record) {
        log.info("接收到预约记录上传请求: {}", record);
        return dataService.uploadAppointmentRecord(medOrgCode, medHosCode,platformCode ,record);
    }

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Medical Data Service is running");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}