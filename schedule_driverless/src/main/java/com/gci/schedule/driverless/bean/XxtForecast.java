package com.gci.schedule.driverless.bean;

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
 * @Date: 2020-08-26 11:51
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XxtForecast {

    private String forecastType;
    private Integer orderNum;
    private Date wxxtForecastTime;
    /**
     * 到站预测时间
     */
    private Date forecastTime;
    private Double predictTime;
    private Integer stationCount;
    private Integer isOffLine;

    @JSONField(serialize = false)
    public Boolean isAbnormalForecast() {
        return orderNum != null && -1 == orderNum;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"forecastType\":\"")
                .append(forecastType).append('\"');
        sb.append(",\"orderNum\":")
                .append(orderNum);
        sb.append(",\"wxxtForecastTime\":\"")
                .append(DateUtil.date2Str(wxxtForecastTime, DateUtil.time_sdf)).append('\"');
        sb.append(",\"forecastTime\":\"")
                .append(DateUtil.date2Str(forecastTime, DateUtil.time_sdf)).append('\"');
        sb.append(",\"predictTime\":")
                .append(predictTime);
        sb.append(",\"stationCount\":")
                .append(stationCount);
        sb.append(",\"isOffLine\":")
                .append(isOffLine);
        sb.append('}');
        return sb.toString();
    }

}
