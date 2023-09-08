package com.gci.schedule.driverless.bean;

import lombok.Data;

@Data
public class ChargeBusParams {
    private Long busId;

    private String busName;

    //调度方式，0：中途补电,1：充电调度，2：置换调度，
    private String type;

    //任务用时  :分钟
    private Integer taskTime;

    private Integer serviceType;

    private Long taskId;

    private Long fromStationId;

    private String serviceName;

    private String taskName;

    private Double predictmileage;

    //计划调位，0:按车的顺序发，1:按司机的顺序发
    private Integer busSort;
}
