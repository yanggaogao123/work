package com.gci.schedule.driverless.bean.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieqingduan
 * @Description: TODO: 出场任务首末站
 * @Date: 2021-08-18 12:03
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveParking {

    private Long fromStationId;
    private String fromStationName;
    private Long toStationId;
    private String toStationName;
    private Long avgRunDuration;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"fromStationId\":")
                .append(fromStationId);
        sb.append(",\"fromStationName\":\"")
                .append(fromStationName).append('\"');
        sb.append(",\"toStationId\":")
                .append(toStationId);
        sb.append(",\"toStationName\":\"")
                .append(toStationName).append('\"');
        sb.append(",\"avgRunDuration\":")
                .append(avgRunDuration);
        sb.append('}');
        return sb.toString();
    }
}
