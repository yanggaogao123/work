package com.gci.schedule.driverless.dynamic.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ScheduleParamsDrInoutTime {

    private Date outTime;

    private Long outStationId;

    private Date inTime;

    private Long inStationId;

}