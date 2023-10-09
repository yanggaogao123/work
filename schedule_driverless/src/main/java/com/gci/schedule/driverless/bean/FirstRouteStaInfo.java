package com.gci.schedule.driverless.bean;

import lombok.Data;

@Data
public class FirstRouteStaInfo {
    private Integer direction;

    private Long routeStationId;

    private String routeStationName;
}
