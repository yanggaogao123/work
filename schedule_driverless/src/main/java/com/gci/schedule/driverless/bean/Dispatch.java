package com.gci.schedule.driverless.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.gci.schedule.driverless.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 调度指令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dispatch {
    private Long routeId;
    private String routeCode;
    private Long taskId;
    private String startTime;
    private String planDate;
    private Long obuId;
    private Long busId;
    private String direction;
    private Long userId;
    private String dispatchAuto;
    private String mark;
    @JSONField(name = "createTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JSONField(serialize = false)
    public Boolean isDispatchAuto(RunBus runBus) {
        if (runBus == null
                || runBus.getTripBeginTime() == null) {
            return false;
        }
        String planDate = DateUtil.date2Str(runBus.getTripBeginTime(), DateUtil.yyyyMMdd);
        String startTime = DateUtil.date2Str(runBus.getTripBeginTime(), DateUtil.hhmm);
        return planDate.equals(getPlanDate())
                && startTime.equals(getStartTime())
                && runBus.getRouteSubId().equals(getTaskId());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"routeCode\":\"")
                .append(routeCode).append('\"');
        sb.append(",\"taskId\":")
                .append(taskId);
        sb.append(",\"startTime\":\"")
                .append(startTime).append('\"');
        sb.append(",\"planDate\":\"")
                .append(planDate).append('\"');
        sb.append(",\"obuId\":")
                .append(obuId);
        sb.append(",\"busId\":")
                .append(busId);
        sb.append(",\"direction\":\"")
                .append(direction).append('\"');
        sb.append(",\"userId\":")
                .append(userId);
        sb.append(",\"dispatchAuto\":\"")
                .append(dispatchAuto).append('\"');
        sb.append(",\"mark\":\"")
                .append(mark).append('\"');
        sb.append(",\"createTime\":\"")
                .append(createTime == null ? "" : DateUtil.date2Str(createTime, DateUtil.time_sdf)).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
