package com.gci.schedule.driverless.bean.schedule;

import lombok.Data;

import java.util.Date;

@Data
public class DyTriplogRunFirst {
    private Long triplogId;
    private String busId;
    private Long employeeId;
    private String employeeName;
    private Long fromStationId;
    private String fromStationName;
    private Date updateTime;
}
