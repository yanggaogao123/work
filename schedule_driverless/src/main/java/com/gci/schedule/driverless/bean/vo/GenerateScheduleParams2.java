package com.gci.schedule.driverless.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class GenerateScheduleParams2 {
    private Long routeId;

    private Long supportRouteId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date runDate;

    //客流参考日期
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date passengerData;

    //最优计划：1，预设计划：2
    private Integer planType;
}
