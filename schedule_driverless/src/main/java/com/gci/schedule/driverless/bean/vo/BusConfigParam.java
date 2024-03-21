package com.gci.schedule.driverless.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data
public class BusConfigParam {
    private Long routeId;

    //关联线路id
    private Long supportRouteId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date runDate;

    //最优计划：1，预设计划：2
    private Integer[] planType;

    //模板id
    private Integer templateId;

    //支援车模板id
    private Integer supportTemplateId;
}
