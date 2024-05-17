package com.gci.schedule.driverless.bean.scheduleD;

import lombok.Data;

import java.util.List;

@Data
public class DriverlessUpDownInfoVo {
    private List<RouteUpDownInfo> mainList;

    private List<RouteUpDownInfo> subList;
}
