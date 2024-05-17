package com.gci.schedule.driverless.bean.bs;

import lombok.Data;

import java.util.Date;

@Data
public class DyChargeConfig {
    private Long id;

    private String busCode;

    private String numberPlate;

    private String busName;

    private Long obuId;

    private Long limitSoc;

    private String isCharge;

    private Date updateTime;

    private Long mileagePercent;

    //任务结束后司机签到用时
    private Long signTime;

}