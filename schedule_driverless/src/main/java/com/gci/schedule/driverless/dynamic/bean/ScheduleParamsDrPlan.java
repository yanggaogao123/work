package com.gci.schedule.driverless.dynamic.bean;

import lombok.Data;

import java.util.Date;

@Data
public class ScheduleParamsDrPlan {

    private String busNameWY;

    private String dispatchDate;

    private String routeIdWY;

    private String beginTime;

    private String endTime;

    private Long firstStationId;

    private Long lastStationId;

}