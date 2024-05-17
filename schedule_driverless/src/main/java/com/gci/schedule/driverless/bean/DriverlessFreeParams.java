package com.gci.schedule.driverless.bean;

import lombok.Data;

import java.util.Date;

@Data
public class DriverlessFreeParams {
    private Long busId;

    private String busName;

    private Date beginFreeTime;

    private Date endFreeTime;
}
