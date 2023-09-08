package com.gci.schedule.driverless.bean;

import lombok.Data;

@Data
public class NoDispatchChargeParams {
    private Long routeId;

    private Long busId;

    private String busName;

    //是否充电标识，0否，1是
    private Integer type;
}
