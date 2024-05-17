package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-05-06 11:01 上午
 * @version: v1.0
 * @Modified by:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LostBus {
    private Long routeId; //": 98,
    private Long busId; //": 2131313,
    private String busName; //": "A345",
    @JSONField(format = "yyyy-MM-dd")
    private Date runDate; //": "2020-04-08",//运营日期
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime; //": "2020-04-08 09:00:00"//操作时间
    private Integer status; // 状态(1:已处理)

    private String lostBus;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"busId\":")
                .append(busId);
        sb.append(",\"busName\":\"")
                .append(busName).append('\"');
        sb.append(",\"runDate\":\"")
                .append(runDate).append('\"');
        sb.append(",\"operateTime\":\"")
                .append(operateTime).append('\"');
        sb.append(",\"status\":")
                .append(status);
        sb.append(",\"lostBus\":\"")
                .append(lostBus).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
