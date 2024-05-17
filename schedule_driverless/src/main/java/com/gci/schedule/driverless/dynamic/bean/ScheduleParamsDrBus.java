package com.gci.schedule.driverless.dynamic.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ScheduleParamsDrBus {

    private String busNameWY;

    private List<ScheduleParamsDrPlan> planList = new ArrayList<>();

    private List<ScheduleParamsDrInoutTime> inoutTimeList = new ArrayList<>();

    private Date tripBeginTime;

    private Date tripEndTime;

}