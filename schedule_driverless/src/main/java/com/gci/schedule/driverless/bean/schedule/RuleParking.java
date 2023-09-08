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
 * @Date: 2021-07-29 17:05
 * @version: v1.0
 * @Modified by:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleParking {

    private Long templateId;

    private Long stationId;

    private String beginTime;

    private String endTime;

    private Integer anchorDurationMin;

    private Integer parkingNumberMax;

    private String updateUser;

    private Date updateTime;


    @JSONField(serialize = false)
    public Date getBeginDateTime() {
        if (beginTime == null || endTime == null) {
            return null;
        }
        return BsData.getFirstTime(beginTime, endTime);
    }


    @JSONField(serialize = false)
    public Date getEndDateTime() {
        if (beginTime == null || endTime == null) {
            return null;
        }
        return BsData.getLatestTime(beginTime, endTime);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"templateId\":")
                .append(templateId);
        sb.append(",\"stationId\":")
                .append(stationId);
        sb.append(",\"beginTime\":\"")
                .append(beginTime).append('\"');
        sb.append(",\"endTime\":\"")
                .append(endTime).append('\"');
        sb.append(",\"anchorDurationMin\":")
                .append(anchorDurationMin);
        sb.append(",\"parkingNumberMax\":")
                .append(parkingNumberMax);
        sb.append(",\"updateUser\":\"")
                .append(updateUser).append('\"');
        sb.append(",\"updateTime\":\"")
                .append(updateTime).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
