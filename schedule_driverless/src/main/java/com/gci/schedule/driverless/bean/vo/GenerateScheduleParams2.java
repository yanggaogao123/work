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

    //下行配车数
    private Integer busNumberDown;

    //上行配车数
    private Integer busNumberUp;
}
