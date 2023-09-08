package com.gci.schedule.driverless.bean;

import lombok.Data;

@Data
public class DeleteChargeDispatchParams {
    private Long routeId;

    private Long busId;

    private String busName;

    //是否删除任务标识，0：否，1：是
    private Integer type;
}
