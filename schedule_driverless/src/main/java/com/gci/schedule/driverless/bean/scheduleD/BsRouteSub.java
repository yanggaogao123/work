package com.gci.schedule.driverless.bean.scheduleD;

import lombok.Data;

@Data
public class BsRouteSub {
    private Long routeId;
    private String routeName;
    private String routeCode;
    private Long routeSubId;
    private String routeSubName;
    private Long firstStationId;
    private String firstStationName;
    private Long lastStationId;
    private String lastStationName;
    private String serviceName;
    private String serviceNumber;
    private String serviceType;
    private String routeSubDirection;

}
