package com.gci.schedule.driverless.bean;

import lombok.Data;

@Data
public class ChangeBusParams {
    private Long routeId;

    private Long busId;

    private String busName;
}
