package com.gci.schedule.driverless.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class GenerateScheduleParams2 {
    private Long routeId;

    //关联线路id
    private Long supportRouteId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date runDate;

    //客流参考日期
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date passengerData;

    //周转时间参考日期
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date turnaroundData;

    //最优计划：1，预设计划：2
    private Integer planType;

    //模板id
    private Integer templateId;

    //支援车客流参考日期
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date supportPassengerData;

    //支援车周转时间参考日期
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date supportTurnaroundData;

    //支援车模板id
    private Integer supportTemplateId;

    //下行配车数
    private Integer busNumberDown;

    //上行配车数
    private Integer busNumberUp;

    //上行单班车
    private Integer singleBusUp;

    //下行单班车
    private Integer singleBusDwon;

    //上行早半班车
    private Integer earlyHalfBusUp;

    //下行早半班车
    private Integer earlyHalfBusDown;

    //支援车上行配车数
    private Integer supportBusNumberUp;

    //支援车下行配车数
    private Integer supportBusNumberDown;

    //支援车上行单班车
    private Integer supportSingleBusUp;

    //支援车下行单班车
    private Integer supportSingleBusDwon;

    //支援车上行早半班车
    private Integer supportEarlyHalfBusUp;

    //支援车下行早半班车
    private Integer supportEarlyHalfBusDown;
}
