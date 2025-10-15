package com.medical;



import com.medical.model.AppointmentRecord;
import com.medical.service.DataTransmissionService;
import com.medical.util.SM2SignatureUtil;
import lombok.var;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class MedicalDataServiceApplicationTests {

    @Autowired
    private DataTransmissionService dataService;

    @Test
    void testAppointmentUpload() {
        try {
            // 创建测试数据
            AppointmentRecord record = new AppointmentRecord();
            record.setSfzjlbdm("01");
            record.setSfzjhm("330110123456789012");
            record.setHzxm("测试患者");
            record.setYylsh("TEST202501010001");
            record.setJzrq("20250101");
            record.setJzsd("14:00-14:30");
            record.setJzksbzdm("TEST001");
            record.setJzksyynbmc("测试科室");
            record.setJzysxm("测试医生");
            record.setMzhylx("putonghao");
            record.setCzsj(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            record.setScbs("0");
            record.setTxnr("请按时就诊");
            record.setSfxyqh("1");
            record.setSjgxsj(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

            // 调用上传接口
            var response = dataService.uploadAppointmentRecord(
                    "med_org_code_001", "med_hos_code_001","platform_code_001", record);

            System.out.println("上传结果: " + response.getSuccess());
            System.out.println("错误代码: " + response.getErrCode());
            System.out.println("错误信息: " + response.getErrMsg());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    @Test
//    void testRegistrationUpload() {
//        // 测试挂号记录上传逻辑，此处省略具体实现细节...
//        // 测试示例
//        String signature = SM2SignatureUtil.generateSignatureByExample();
//
//        // 验证签名
//        String publicKey = "04b674b6edfd08f754410b21cc3c8b522f318a1f1b27403bc63a290b7e1790d03d967d06c46e69357793967ff42a34d77ed5d7bb40d31b7f5ab0b0840017cff114";
//        String sm4Key = "726be1646879423e26eb7977c4d80c48";
//        String sourceData = "datasetCode=HDSD.YYJL&medHosCode=med_hos_code_001&medOrgCode=med_org_code_001&requestId=1408c07c-bfbb-4135-a516-9232ac0a3bbf&tradeCode=1001&" + sm4Key;
//
//        boolean isValid = SM2SignatureUtil.verifySM2Signature(publicKey, sourceData, signature);
//        System.out.println("签名验证结果: " + isValid);
//    }

}