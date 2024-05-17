package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import com.gci.schedule.driverless.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-09-10 09:54
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TailBus {

    private Long routeId;
    private Long busId;
    private String busName;
    private String direction;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"busId\":")
                .append(busId);
        sb.append(",\"busName\":\"")
                .append(busName).append('\"');
        sb.append(",\"direction\":\"")
                .append(direction).append('\"');
        sb.append(",\"operateTime\":\"")
                .append(DateUtil.date2Str(operateTime, DateUtil.time_sdf)).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
