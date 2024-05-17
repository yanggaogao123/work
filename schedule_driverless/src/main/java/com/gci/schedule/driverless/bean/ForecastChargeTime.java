package com.gci.schedule.driverless.bean;

import lombok.Data;

/**
 * 预估充电时长接收类
 */
@Data
public class ForecastChargeTime {

    private String obuid;
    //充电时长预估 单位：秒
    private Double charge_duration;

    private Integer pcount;

}
