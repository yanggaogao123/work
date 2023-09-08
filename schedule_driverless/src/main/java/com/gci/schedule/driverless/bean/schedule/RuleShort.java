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
 * @Date: 2021-07-15 10:10
 * @version: v1.0
 * @Modified by:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleShort {

    private Long templateId;
    private Long routeId;
    private String direction;
    private String beginTime;
    private String endTime;
    private Long anchorDurationMin;
    private Long fullClassesInterval;
    private String ruleType;
    private Long taskId1;
    private Long taskId2;
    private Long taskId3;
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
        sb.append(",\"routeId\":")
                .append(routeId);
        sb.append(",\"direction\":\"")
                .append(direction).append('\"');
        sb.append(",\"beginTime\":\"")
                .append(beginTime).append('\"');
        sb.append(",\"endTime\":\"")
                .append(endTime).append('\"');
        sb.append(",\"anchorDurationMin\":")
                .append(anchorDurationMin);
        sb.append(",\"fullClassesInterval\":")
                .append(fullClassesInterval);
        sb.append(",\"ruleType\":\"")
                .append(ruleType).append('\"');
        sb.append(",\"taskId1\":")
                .append(taskId1);
        sb.append(",\"taskId2\":")
                .append(taskId2);
        sb.append(",\"taskId3\":")
                .append(taskId3);
        sb.append('}');
        return sb.toString();
    }
}
