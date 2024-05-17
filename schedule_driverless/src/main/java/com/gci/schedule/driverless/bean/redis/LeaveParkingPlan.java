package com.gci.schedule.driverless.bean.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-09-22 17:54
 * @version: v1.0
 * @Modified by:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveParkingPlan {

    private Long routeId;
    private String direction;
    private Long busId;
    private String serviceType;
    private Long taskId;
    private Long firstStationId;
    private Date planDate;
    private Date planTime;
    private String status;//0:未调度 1:已调度

    public Boolean isDispatched() {
        return "1".equals(status);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"direction\":\"")
                .append(direction).append('\"');
        sb.append(",\"busId\":")
                .append(busId);
        sb.append(",\"serviceType\":\"")
                .append(serviceType).append('\"');
        sb.append(",\"taskId\":")
                .append(taskId);
        sb.append(",\"firstStationId\":")
                .append(firstStationId);
        sb.append(",\"planDate\":\"")
                .append(planDate).append('\"');
        sb.append(",\"planTime\":\"")
                .append(planTime).append('\"');
        sb.append(",\"status\":\"")
                .append(status).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
