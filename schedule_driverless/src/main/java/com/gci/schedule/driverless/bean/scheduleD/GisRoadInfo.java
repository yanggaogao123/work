package com.gci.schedule.driverless.bean.scheduleD;

import lombok.Data;

@Data
public class GisRoadInfo {
    private Long routeStaId;

    private Long stationId;

    private Long roadsegId;

    private String stationMark;

    private String routeStationName;

    private Integer orderNumber;

    private Integer orderNum;

    private Double longitude;

    private Double latitude;
}
