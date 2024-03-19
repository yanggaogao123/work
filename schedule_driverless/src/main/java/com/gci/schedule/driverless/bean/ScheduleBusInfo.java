package com.gci.schedule.driverless.bean;

import lombok.Data;

import java.util.List;

@Data
public class ScheduleBusInfo {
    private Long busId;

    private String busCode;

    private String busName;

    //车序
    private Integer firstBusNumber;

    //出车方向
    private String firstDirection;

    //例如 上行第1号车
    private String busNameFull;

    //总的行车时间 单位：分钟
    private Double totalTripTime;

    //营运时间，单位：分钟
    private Double totalRunTime;

    private List<ScheduleInfo> scheduleList;
}
