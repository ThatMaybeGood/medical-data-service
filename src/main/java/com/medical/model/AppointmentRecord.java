package com.medical.model;

/**
 * @author Mine
 * @version 1.0
 * 描述:预约记录请求模型
 * @date 2025/10/13 13:30
 */

import lombok.Data;

@Data
public class AppointmentRecord {
    private String sfzjlbdm;  // 身份证件类别代码
    private String sfzjhm;    // 身份证件号码
    private String hzxm;      // 患者姓名
    private String yylsh;     // 预约流水号
    private String jzxh;      // 就诊序号
    private String jzrq;      // 就诊日期
    private String jzsd;      // 就诊时段
    private String jzksbzdm;  // 就诊科室标准代码
    private String jzksyynbmc; // 就诊科室医院内部名称
    private String jzkswz;    // 就诊科室位置
    private String jzysxm;    // 接诊医师姓名
    private String mzhylx;    // 门诊号源类型
    private String czsj;      // 操作时间
    private String scbs;      // 删除标识
    private String status;    // 订单状态
    private String txnr;      // 提醒内容
    private String qhcdsj;    // 取号迟到时间
    private String qhjzsj;    // 取号截止时间
    private String yyddh;     // 预约订单号
    private String pbbh;      // 排班编号
    private String sfxyqh;    // 是否需要取号
    private String sjgxsj;    // 数据更新时间

}