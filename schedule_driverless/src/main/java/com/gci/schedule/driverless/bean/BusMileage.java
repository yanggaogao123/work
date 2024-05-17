package com.gci.schedule.driverless.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-09-01 17:44
 * @version: v1.0
 * @Modified by:
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BusMileage {

    private Long busId;
    @JSONField(name = "obuid")
    private Long obuId;
    private Double totalMileage;
    private Double runMileage;
    private Double remainMileage;
    private Double unRunMileage;

    @JSONField(name = "runDate", format = "yyyyMMdd HHmmss")
    private Date runDate;
    @JSONField(name = "energyBaseTime", format = "yyyyMMdd HHmmss")
    private Date energyBaseTime;
    @JSONField(name = "createTime", format = "yyyyMMdd HHmmss")
    private Date createTime;
    @JSONField(name = "updateTime", format = "yyyyMMdd HHmmss")
    private Date updateTime;

    /*{
        "busId": "3010868",
        "runDate": "20210902 000000",
        "runMileage": "56.3",
        "createTime": "20210902 000000",
        "obuid": "983474",
        "energyBaseTime": "20210902 000000",
        "unRunMileage": "4",
        "updateTime": "20210902 095001",
        "totalMileage": "60.3",
        "remainMileage": "169.7"
    }*/
}
