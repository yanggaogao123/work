package com.gci.schedule.driverless.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * LOG_FORECAST_TIME
 *
 * @author
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogForecastTime {

    private Date runDate;

    private Long routeId;

    private String routeName;

    private Long busId;

    private String busName;

    private String direction;

    private Long stationId;

    private String stationName;

    private Long lastStationId;

    private String lastStationName;

    private String serviceType;

    private String serviceName;

    private Date tripBeginTime;

    private Date forecastTime;

    private String forecastType;

    private String runStatus;

    private Double predictTime;

    private Integer stationCount;

}