package com.gci.schedule.driverless.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ScheduleInfo {
    private Long scheduleId;

    private String serviceType;

    private String serviceName;

    private Integer direction;

    private Long firstRouteStaId;

    private Long lastRouteStaId;

    private String firstRouteStaName;

    private String lastRouteStaName;

    //开始时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date tripBeginTime;
    //结束时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date tripEndTime;
}
