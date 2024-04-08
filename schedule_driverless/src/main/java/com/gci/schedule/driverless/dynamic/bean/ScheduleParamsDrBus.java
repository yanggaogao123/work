package com.gci.schedule.driverless.dynamic.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ScheduleParamsDrBus {

    private String busNameWY;

    private List<ScheduleParamsDrPlan> planList = new ArrayList<>();

}