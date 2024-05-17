package com.gci.schedule.driverless.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-11-26 20:18
 * @version: v1.0
 * @Modified by:
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotDispatch {
    private Long routeId;
    private Long busId;
    private String runStatus;
    private String oldRunstatus;
    private String updateTime;
    private String adFlag;
    private String obuid;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"busId\":")
                .append(busId);
        sb.append(",\"runStatus\":\"")
                .append(runStatus).append('\"');
        sb.append(",\"oldRunstatus\":\"")
                .append(oldRunstatus).append('\"');
        sb.append(",\"updateTime\":\"")
                .append(updateTime).append('\"');
        sb.append(",\"adFlag\":\"")
                .append(adFlag).append('\"');
        sb.append(",\"obuid\":\"")
                .append(obuid).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
