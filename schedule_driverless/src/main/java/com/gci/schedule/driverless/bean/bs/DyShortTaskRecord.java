package com.gci.schedule.driverless.bean.bs;

import lombok.Data;

import java.util.Date;

@Data
public class DyShortTaskRecord {
    private Long id;
    private Long routeId;
    private String serviceType;
    private Long taskId;
    private Long stationId;
    private Date updateTime;
    //不带日期的时间字符串，例如1430
    private String beginTime;
    private String endTime;
}
