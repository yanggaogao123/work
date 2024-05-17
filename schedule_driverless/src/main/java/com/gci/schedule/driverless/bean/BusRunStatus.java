package com.gci.schedule.driverless.bean;

import lombok.Data;

import java.util.Date;

@Data
public class BusRunStatus {
    private String run_status;
    private String old_run_status;
    private String obuid;
    private String bus_id;
    private Date redis_time;
    private Date trip_start_time;

}
