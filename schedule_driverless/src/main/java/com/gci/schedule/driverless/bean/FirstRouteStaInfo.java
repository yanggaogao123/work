package com.gci.schedule.driverless.bean;

import lombok.Data;

@Data
public class FirstRouteStaInfo {
    private Integer direction;

    private Long routeStationId;

    private String routeStationName;

    private Integer classes;

    private Integer supportClasses;

    private String beginTime;

    private String endTime;

    private Integer upInterval;

    private Integer downInterval;

    private Integer parkPercent;

    private Integer fullPercent;

    //趟次数
    private Integer busClasses;


}
