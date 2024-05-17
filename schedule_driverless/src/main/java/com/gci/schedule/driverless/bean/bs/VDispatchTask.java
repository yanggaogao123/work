package com.gci.schedule.driverless.bean.bs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-01-22 下午4:34
 * @version: v1.0
 * @Modified by:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VDispatchTask {
    private Long routeId;
    private Long routeSubId;
    private String serviceType;
    private String routeSubName;
    private Long firstStationId;
    private Long lastStationId;
    private String direction;
    private Long firstRouteStaId;
    private Long lastRouteStaId;
}
