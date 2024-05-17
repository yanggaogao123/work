package com.gci.schedule.driverless.bean;

import lombok.Data;

@Data
public class ChangeBusInfo {
    private Long busId;

    private String busName;

    private Double soc;

    private String runStatus;

    private String runStatusName;

    //续航里程
    private Double predictmileage;

    private String fromStationName;

    private String direction;

    private String routeName;


}
