package com.gci.schedule.driverless.dynamic.bean;

import lombok.Data;

import java.util.Date;

@Data
public class ScheduleParamsDrRouteSub {

    private Long routeId;

    private Integer templateId; //模板Id

    private Integer updateUser;

    private Date updateTime;

    private Long firstRouteStaId;

    private Long lastRouteStaId;

    private Integer direction;

    private String firstRouteStaName;

    private String lastRouteStaName;

    private Integer turnAroundTime;

    private Long routeSubId;

    private String routeSubName;

    private String serviceType;

    private String serviceName;

    private Long nextFirstRouteStaId;

    private String nextFirstRouteStaName;

}