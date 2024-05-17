package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-09-08 18:02
 * @version: v1.0
 * @Modified by:
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResumeRun {

    private Long routeId;
    private String direction;
    private Long firstStationId;
    private Long busId;
    private String busName;
    private String canAdvance;
    private Long expectHowLong;
    @JSONField(name = "expectResumeRunTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date expectResumeRunTime;
    private String remark;
    private String userId;
    @JSONField(name = "createTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String invalid;//是否已失效 0:否 1:

    //是否充电复行任务，0:否，1:是
    private String isCharge;

    public Boolean isInvalid() {
        return "1".equals(invalid);
    }

    public Boolean isCanAdvance() {
        return "1".equals(canAdvance);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"direction\":\"")
                .append(direction).append('\"');
        sb.append(",\"firstStationId\":")
                .append(firstStationId);
        sb.append(",\"busId\":")
                .append(busId);
        sb.append(",\"busName\":\"")
                .append(busName).append('\"');
        sb.append(",\"canAdvance\":\"")
                .append(canAdvance).append('\"');
        sb.append(",\"expectHowLong\":")
                .append(expectHowLong);
        sb.append(",\"expectResumeRunTime\":\"")
                .append(expectResumeRunTime).append('\"');
        sb.append(",\"remark\":\"")
                .append(remark).append('\"');
        sb.append(",\"userId\":\"")
                .append(userId).append('\"');
        sb.append(",\"createTime\":\"")
                .append(createTime).append('\"');
        sb.append(",\"invalid\":\"")
                .append(invalid).append('\"');
        sb.append(",\"isCharge\":\"")
                .append(isCharge).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
