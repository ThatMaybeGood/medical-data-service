package com.medical.controller;

/**
 * @author Mine
 * @version 1.0
 * 描述:
 * @date 2025/10/13 16:08
 */

import com.medical.model.AppointmentRecord;
import com.medical.service.DataTransmissionService;
import com.medical.util.TestDataGenerator;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/dev")
public class DevTestController {

    private final DataTransmissionService dataService;
    private final TestDataGenerator testDataGenerator;

    public DevTestController(DataTransmissionService dataService,
                             TestDataGenerator testDataGenerator) {
        this.dataService = dataService;
        this.testDataGenerator = testDataGenerator;
    }

    /**
     * 测试数据上传（使用模拟Token）
     */
    @PostMapping("/test-upload")
    public Map<String, Object> testUpload(@RequestParam String medOrgCode,
                                          @RequestParam String medHosCode,
                                           @RequestParam String platformCode) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 生成测试数据
            AppointmentRecord testRecord = testDataGenerator.generateTestAppointment();

            log.info("测试上传数据: {}", testRecord);

            // 调用上传接口
            var response = dataService.uploadAppointmentRecord(medOrgCode, medHosCode, platformCode,testRecord);

            result.put("success", true);
            result.put("message", "测试数据上传完成");
            result.put("testData", testRecord);
            result.put("response", response);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "测试数据上传失败: " + e.getMessage());
            result.put("error", e.toString());
        }

        return result;
    }

    /**
     * 获取当前运行模式信息
     */
    @GetMapping("/mode-info")
    public Map<String, Object> getModeInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("mode", "development");
        info.put("status", "使用模拟Token进行开发测试");
        info.put("nextSteps", "请联系项目负责人获取正式的app-key和app-secret");
        info.put("contact", "重庆医疗健康智能体云陪诊项目组");
        return info;
    }
}