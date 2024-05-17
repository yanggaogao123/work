package com.gci.schedule.driverless.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-08-23 15:41
 * @version: v1.0
 * @Modified by:
 **/
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RouteSub {

    private Long routeId;
    private String direction;
    private String serviceType;
    private String routeSubName;
    private Long routeSubId;
    private Long firstStationId;
    private Long lastStationId;
    private Long firstRouteStaId;
    private Long lastRouteStaId;

    @JSONField(serialize = false)
    public boolean isRefuelType() {
        String[] refuelTypes = {"-5", "-16", "-45", "-56", "-78", "-96", "-97", "-106", "-109", "-115", "-116", "-124" };
        for (String t : refuelTypes) {
            if (t.equals(serviceType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"direction\":\"")
                .append(direction).append('\"');
        sb.append(",\"serviceType\":\"")
                .append(serviceType).append('\"');
        sb.append(",\"routeSubName\":\"")
                .append(routeSubName).append('\"');
        sb.append(",\"routeSubId\":")
                .append(routeSubId);
        sb.append(",\"firstStationId\":")
                .append(firstStationId);
        sb.append(",\"lastStationId\":")
                .append(lastStationId);
        sb.append(",\"firstRouteStaId\":")
                .append(firstRouteStaId);
        sb.append(",\"lastRouteStaId\":")
                .append(lastRouteStaId);
        sb.append('}');
        return sb.toString();
    }
}