package com.gci.schedule.driverless.dynamic.bean;

import lombok.Data;

import java.util.Date;

@Data
public class ScheduleParamsDrInout {

    private Long routeId;

    private Integer templateId; //模板Id

    private Integer updateUser;

    private Date updateTime;

    private Long routeSubIdIn;

    private Long routeSubIdOut;

    private String routeSubNameIn;

    private String routeSubNameOut;

    private Integer durationMinIn;

    private Integer durationMinOut;

    private Long stationIdInFirst;

    private Long stationIdInLast;

    private Long stationIdOutFirst;

    private Long stationIdOutLast;

    private String stationNameInFirst;

    private String stationNameInLast;

    private String stationNameOutFirst;

    private String stationNameOutLast;

}