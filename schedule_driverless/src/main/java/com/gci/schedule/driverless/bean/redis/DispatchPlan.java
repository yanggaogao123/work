package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import com.gci.schedule.driverless.bean.common.BsData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

/**
 * redis排班计划
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchPlan {

    private Long busId;
    private String direction;
    private Long dispatchPlanId;
    private Long firstRouteStaId;
    private Long lastRouteStaId;
    private Long firstStationId;
    @JSONField(name = "lastPlanTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date prePlanTime;//前一个计划开始时间
    @JSONField(name = "lastServiceType")
    private Integer preServiceType;//前一个计划任务类型
    @JSONField(name = "planTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date planTime;//计划开始时间
    private String planType;
    private String busFirstPlan;
    private Long routeId;
    private Integer serviceType;
    private Integer status;
    private Long taskId;
    @JSONField(name = "tripEndTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date tripEndTime;//计划结束时间

    private String dispatchPlan;

    @JSONField(serialize = false)
    public boolean isFirstTime() {
        return BsData.isFirstTime(routeId, direction, planTime);
    }

    @JSONField(serialize = false)
    public boolean isLatestTime() {
        return BsData.isLatestTime(routeId, direction, planTime);
    }

    /*{*/
    /*    "busId": 3015222,*/
    /*        "direction": 0,*/
    /*        "dispatchPlanId": 371474,*/
    /*        "firstRouteStaId": 1047182,*/
    /*        "firstStationId": 165768,*/
    /*        "lastPlanTime": "2020-03-19 21:44:00",*/
    /*        "lastRouteStaId": 89171,*/
    /*        "lastServiceType": 1,*/
    /*        "planTime": "2020-03-19 22:00:00",*/
    /*        "planType": 0,*/
    /*        "routeId": 710,*/
    /*        "serviceType": 1,*/
    /*        "status": 0,*/
    /*        "taskId": -857103,*/
    /*        "tripEndTime": "2020-03-19 22:50:00"*/
    /*}*/

    @Override
    public String toString() {
        return dispatchPlan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DispatchPlan that = (DispatchPlan) o;
        return Objects.equals(busId, that.busId) &&
                Objects.equals(direction, that.direction) &&
                Objects.equals(dispatchPlanId, that.dispatchPlanId) &&
                Objects.equals(firstRouteStaId, that.firstRouteStaId) &&
                Objects.equals(lastRouteStaId, that.lastRouteStaId) &&
                Objects.equals(firstStationId, that.firstStationId) &&
                Objects.equals(prePlanTime, that.prePlanTime) &&
                Objects.equals(preServiceType, that.preServiceType) &&
                Objects.equals(planTime, that.planTime) &&
                Objects.equals(planType, that.planType) &&
                Objects.equals(routeId, that.routeId) &&
                Objects.equals(serviceType, that.serviceType) &&
                Objects.equals(status, that.status) &&
                Objects.equals(taskId, that.taskId) &&
                Objects.equals(tripEndTime, that.tripEndTime) &&
                Objects.equals(dispatchPlan, that.dispatchPlan);
    }
}
