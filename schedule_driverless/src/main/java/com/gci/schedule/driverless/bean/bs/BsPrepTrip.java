package com.gci.schedule.driverless.bean.bs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-09-02 14:59
 * @version: v1.0
 * @Modified by:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BsPrepTrip {
    private Long preptripId;

    private String direction;

    private String serviceName;

    private String serviceType;

    private Long firstStationId;

    private String firstStationName;

    private Long lastStationId;

    private String lastStationName;

    private Long tripdefineId;

    private Long orderNum;

    private Long routeId;
}
