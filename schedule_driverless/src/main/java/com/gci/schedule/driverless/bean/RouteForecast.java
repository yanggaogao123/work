package com.gci.schedule.driverless.bean;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2023-08-04 15:41
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
public class RouteForecast {
    private Long routeId;
    private String direction;
    private String open;
    private List<Forecast> forecastPlans;
}
