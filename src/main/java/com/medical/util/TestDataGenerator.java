package com.medical.util;

/**
 * @author Mine
 * @version 1.0
 * 描述:创建测试数据生成器
 * @date 2025/10/13 16:08
 */

import com.medical.model.AppointmentRecord;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class TestDataGenerator {

    /**
     * 生成测试预约记录
     */
    public AppointmentRecord generateTestAppointment() {
        AppointmentRecord record = new AppointmentRecord();
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        record.setSfzjlbdm("01");
        record.setSfzjhm("330110199001011234");
        record.setHzxm("测试患者");
        record.setYylsh("TEST" + timestamp);
        record.setJzxh("A001");
        record.setJzrq(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        record.setJzsd("09:00-09:30");
        record.setJzksbzdm("KS001");
        record.setJzksyynbmc("内科");
        record.setJzkswz("1号楼2层内科诊区");
        record.setJzysxm("张医生");
        record.setMzhylx("putonghao");
        record.setCzsj(timestamp);
        record.setScbs("0");
        record.setStatus("");
        record.setTxnr("请按时就诊，提前30分钟到达");
        record.setQhcdsj("");
        record.setQhjzsj("");
        record.setYyddh("DD" + timestamp);
        record.setPbbh("PB001");
        record.setSfxyqh("1");
        record.setSjgxsj(timestamp);

        return record;
    }
}
