package com.gci.schedule.driverless.dynamic.bean;

import lombok.Data;

import java.util.Date;

@Data
public class ScheduleDriverlessPlanTimePre {

    private Long id;

    private Long routeId;

    private Date runDate;

    private Long busId;

    private Long taskId;

    private Date tripBeginTime;

    private Date tripEndTime;

    private Long firstStationId;

    private Long lastStationId;

}