package com.gci.schedule.driverless.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-04-08 1:50 下午
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Forecast {

    private Long busId;

    private String busName;

    @JSONField(name = "planTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date planTime;

    @JSONField(name = "arrivalTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date arrivalTime;

    private String planType;

    private Integer orderNum;

    private Boolean adjustPosition;

    private Integer offLine;

    private Long eatTime;               //吃饭时间(分钟)

    private Long sysEatTime;            //吃饭时间(分钟)

    private Long minStop;               //最小停站时间(分钟)

    private Long sysMinStop;            //系统最小停站时间(分钟)

    private Long maxStop;               //最大停站时间

    private String rDirection;          //是否另个方向

    @JSONField(serialize = false)
    public Boolean isAbnormalForecast() {
        return orderNum != null && orderNum == -1;
    }

    @JSONField(serialize = false)
    public Boolean isRDirection() {
        return "1".equals(rDirection);
    }

    @JSONField(serialize = false)
    public Boolean isOffLine() {
        return getOffLine() != null && getOffLine() == 1;
    }

    @JSONField(serialize = false)
    public Boolean isAdjustPosition() {
        return getAdjustPosition() != null && getAdjustPosition();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"busId\":")
                .append(busId);
        sb.append(",\"busName\":\"")
                .append(busName).append('\"');
        sb.append(",\"planTime\":\"")
                .append(planTime).append('\"');
        sb.append(",\"arrivalTime\":\"")
                .append(arrivalTime).append('\"');
        sb.append(",\"planType\":")
                .append(planType);
        sb.append(",\"orderNum\":")
                .append(orderNum);
        sb.append(",\"adjustPosition\":")
                .append(adjustPosition);
        sb.append(",\"offLine\":")
                .append(offLine);
        sb.append(",\"eatTime\":")
                .append(eatTime);
        sb.append(",\"sysEatTime\":")
                .append(sysEatTime);
        sb.append(",\"minStop\":")
                .append(minStop);
        sb.append(",\"sysMinStop\":")
                .append(sysMinStop);
        sb.append(",\"maxStop\":")
                .append(maxStop);
        sb.append('}');
        return sb.toString();
    }
}
