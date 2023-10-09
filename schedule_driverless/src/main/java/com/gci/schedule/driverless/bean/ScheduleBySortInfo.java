package com.gci.schedule.driverless.bean;

import lombok.Data;

import java.util.List;

@Data
public class ScheduleBySortInfo {
    private Long routeId;

    private List<ScheduleBusInfo> scheduleBusList;

    private List<FirstRouteStaInfo> firstRouteStaList;
}
