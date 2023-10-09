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

    private List<ScheduleInfo> scheduleList;
}
