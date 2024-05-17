package com.gci.schedule.driverless.bean;

import lombok.Data;

@Data
public class AdjustOrShortParams {
    private Long routeId;

    private Long nowBusId;

    private String nowBusName;

    private Long adjustBusId;

    private String adjustBusName;

    //1:调位确认，2:发短线确认
    private Integer flag;

    //是否调位/短线标识，0否，1是
    private Integer type;
}
