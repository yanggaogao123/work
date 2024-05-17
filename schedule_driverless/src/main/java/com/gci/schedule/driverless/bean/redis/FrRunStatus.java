package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import com.gci.schedule.driverless.bean.common.BsData;
import com.gci.schedule.driverless.bean.common.Constant;
import com.gci.schedule.driverless.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO: 首轮运行计划
 * @Date: 2020-05-14 2:15 下午
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrRunStatus {

    private Long routeId;
    private String direction;
    private Long busId;
    private String busName;
    private String serviceType;
    private String serviceName;
    private Long taskId;
    private String routeSubName;
    private Long firstStationId;
    private String firstStationName;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date preSchePlanTime; //前一个计划排班计划时间
    private String preServiceType;//前一个计划任务类型
    private String preServiceName;//前一个计划任务名称

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date schePlanTime;//排班计划时间

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date realPlanTime;//实际发班时间

    private String ckType;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date ckTime;
    private String distance;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private Long planInterval;//排班计划间隔

    private String loss;   //是否失班
    private String invalid;//是否已失效 0:否 1:是
    private String hangUp;   //是否已挂起 0:否 1:是

    @JSONField(serialize = false)
    public Boolean isLoss() {
        return "1".equals(loss);
    }

    @JSONField(serialize = false)
    public Boolean isInvalid() {
        return "1".equals(invalid);
    }

    @JSONField(serialize = false)
    public Boolean isHangUp() {
        return "1".equals(hangUp);
    }

    @JSONField(serialize = false)
    public Boolean isFullLine() {
        return Constant.ServiceType.FULL_LINE.equals(serviceType);
    }

    @JSONField(serialize = false)
    public Long getOriginalInterval() {
        if (planInterval != null) {
            return planInterval * 60 * 1000;
        }
        if (preSchePlanTime == null) {
            return 0L;
        }
        return schePlanTime.getTime() - preSchePlanTime.getTime();
    }

    @JSONField(serialize = false)
    public boolean isFirstTime() {
        return BsData.isFirstTime(routeId, direction, schePlanTime);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"direction\":\"")
                .append(direction).append('\"');
        sb.append(",\"busId\":")
                .append(busId);
        sb.append(",\"busName\":\"")
                .append(busName).append('\"');
        sb.append(",\"serviceType\":\"")
                .append(serviceType).append('\"');
        sb.append(",\"serviceName\":\"")
                .append(serviceName).append('\"');
        sb.append(",\"taskId\":")
                .append(taskId);
        sb.append(",\"routeSubName\":\"")
                .append(routeSubName).append('\"');
        sb.append(",\"firstStationId\":")
                .append(firstStationId);
        sb.append(",\"firstStationName\":\"")
                .append(firstStationName).append('\"');
        sb.append(",\"preSchePlanTime\":\"")
                .append(preSchePlanTime == null ? "" : DateUtil.date2Str(preSchePlanTime, DateUtil.time_sdf)).append('\"');
        sb.append(",\"preServiceType\":\"")
                .append(preServiceType).append('\"');
        sb.append(",\"preServiceName\":\"")
                .append(preServiceName).append('\"');
        sb.append(",\"schePlanTime\":\"")
                .append(schePlanTime == null ? "" : DateUtil.date2Str(schePlanTime, DateUtil.time_sdf)).append('\"');
        sb.append(",\"realPlanTime\":\"")
                .append(realPlanTime == null ? "" : DateUtil.date2Str(realPlanTime, DateUtil.time_sdf)).append('\"');
        sb.append(",\"ckType\":\"")
                .append(ckType).append('\"');
        sb.append(",\"ckTime\":\"")
                .append(ckTime == null ? "" : DateUtil.date2Str(ckTime, DateUtil.time_sdf)).append('\"');
        sb.append(",\"distance\":\"")
                .append(distance).append('\"');
        sb.append(",\"updateTime\":\"")
                .append(updateTime == null ? "" : DateUtil.date2Str(updateTime, DateUtil.time_sdf)).append('\"');
        sb.append(",\"loss\":\"")
                .append(loss).append('\"');
        sb.append(",\"invalid\":\"")
                .append(invalid).append('\"');
        sb.append(",\"hangUp\":\"")
                .append(hangUp).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
