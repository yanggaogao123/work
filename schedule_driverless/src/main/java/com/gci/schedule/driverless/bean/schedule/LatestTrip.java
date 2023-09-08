package com.gci.schedule.driverless.bean.schedule;

import com.alibaba.fastjson.annotation.JSONField;
import com.gci.schedule.driverless.bean.common.BsData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-12-23 14:26
 * @version: v1.0
 * @Modified by:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LatestTrip {

    private Long routeId;

    private String direction;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date tripBeginTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date tripBeginTimeLatest;

    private Long arriveEarlyEndTrip;

    @JSONField(serialize = false)
    public Boolean isLatestTime() {
        return BsData.isLatestTime(routeId, direction, tripBeginTimeLatest);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"direction\":\"")
                .append(direction).append('\"');
        sb.append(",\"tripBeginTime\":\"")
                .append(tripBeginTime).append('\"');
        sb.append(",\"tripBeginTimeLatest\":\"")
                .append(tripBeginTimeLatest).append('\"');
        sb.append(",\"arriveEarlyEndTrip\":")
                .append(arriveEarlyEndTrip);
        sb.append('}');
        return sb.toString();
    }
}
