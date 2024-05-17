package com.gci.schedule.driverless.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-09-01 18:58
 * @version: v1.0
 * @Modified by:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteAvgMileage {
    private Long routeId;
    private Double avgMileage;
}
