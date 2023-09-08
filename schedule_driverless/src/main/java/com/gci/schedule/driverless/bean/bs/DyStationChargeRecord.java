package com.gci.schedule.driverless.bean.bs;

import lombok.Data;

import java.util.Date;

@Data
public class DyStationChargeRecord {
    private Long id;

    private Long routeId;

    private Long busId;

    private String busName;

    private String serviceType;

    private Long taskId;

    private Long fromStationId;

    private Long toStationId;

    private Date updateTime;

    private String serviceName;

    private String direction;

    private String taskName;
    //0：中途补电，1:补电，2：置换，
    private String type;


}