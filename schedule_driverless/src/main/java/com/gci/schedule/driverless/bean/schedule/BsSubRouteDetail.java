package com.gci.schedule.driverless.bean.schedule;


import com.gci.schedule.driverless.bean.common.BsData;
import com.gci.schedule.driverless.bean.common.CacheUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BsSubRouteDetail {

    private Long routeId;

    private Long templateId;

    private Long subRouteDetailId;

    private String firstTime;

    private String latestTime;

    private String direction;

    private Long taskId;

    private String protrudingTail;

    private Long arriveStaStopTime;

    private List<BsSubRouteAnchor> anchors;

    private List<BsSubRouteClasses> classes;

    private List<BsSubRouteEat> eats;

    private String updateUser;

    private Date updateTime;

    public Boolean isProtrudingTail() {
        return "1".equals(protrudingTail);
    }

    public Long getFirstStationId() {
        return BsData.getFirstStationIdByRouteSubId(taskId);
    }

    public Long getLastStationId() {
        return BsData.getLastStationIdByRouteSubId(taskId);
    }

    public BsSubRouteEat getEat(Date date) {
        BsSubRouteEat res = null;
        if (CollectionUtils.isEmpty(eats)) {
            return res;
        }
        for (BsSubRouteEat eat : eats) {
            long start = eat.getBeginDateTime().getTime();
            long end = start + CacheUtil.getSysEatInterval() * 60 * 1000;
            if (date.getTime() >= start && date.getTime() <= end) {
                res = eat;
                break;
            }
        }
        return res;
    }

    public Long getMaxInterval(Date tripBeginTime) {
        BsSubRouteClasses classesA = getClasses(tripBeginTime);
        if (classesA == null) {
            return CacheUtil.getSysMaxInterval(routeId);
        }
        long finalTripBeginTs = tripBeginTime.getTime();
        BsSubRouteClasses classesB = getClasses(new Date(finalTripBeginTs + classesA.getMaxDispatchInterval() * 60 * 1000));
        if (classesB != null
                && classesB.getMaxDispatchInterval() < classesA.getMaxDispatchInterval()) {
            long start = classesA.getEndDateTime().getTime() - classesA.getMaxDispatchInterval() * 60 * 1000;
            long end = classesA.getEndDateTime().getTime() - classesB.getMaxDispatchInterval() * 60 * 1000;
            if (finalTripBeginTs > start && finalTripBeginTs < end) {
                return (classesA.getEndDateTime().getTime() - finalTripBeginTs) / (60 * 1000);
            }
            if (finalTripBeginTs >= end) {
                return classesB.getMaxDispatchInterval();
            }
        }
        return classesA.getMaxDispatchInterval();
    }

    public BsSubRouteClasses getClasses(Date tripBeginTime) {
        if (CollectionUtils.isEmpty(classes) || tripBeginTime == null) {
            return null;
        }
        for (BsSubRouteClasses classes : classes) {
            Date beginTime = classes.getBeginDateTime();
            Date endTime = classes.getEndDateTime();
            if (tripBeginTime.getTime() >= beginTime.getTime()
                    && tripBeginTime.getTime() < endTime.getTime()) {
                return classes;
            }
        }
        return null;
    }

    public Long getMinStop(Date arrivalTime) {
        BsSubRouteAnchor anchor = getAnchor(arrivalTime);
        return anchor == null || anchor.getMinStop() == null ?
                CacheUtil.getSysMinStopMinute() : anchor.getMinStop();
    }

    public Long getMaxStop(Date arrivalTime) {
        BsSubRouteAnchor anchor = getAnchor(arrivalTime);
        return anchor == null || anchor.getMaxStop() == null ?
                CacheUtil.getSysMaxStopMinute() : anchor.getMaxStop();
    }

    private BsSubRouteAnchor getAnchor(Date arrivalTime) {
        if (CollectionUtils.isEmpty(anchors) || arrivalTime == null) {
            return null;
        }
        for (BsSubRouteAnchor anchor : anchors) {
            Date beginTime = anchor.getBeginDateTime();
            Date endTime = anchor.getEndDateTime();
            if (arrivalTime.getTime() >= beginTime.getTime()
                    && arrivalTime.getTime() < endTime.getTime()) {
                return anchor;
            }
        }
        return null;
    }

    public Date getFirstDateTime() {
        if (firstTime == null || latestTime == null) {
            return null;
        }
        return BsData.getFirstTime(firstTime, latestTime);
    }

    public Date getLatestDateTime() {
        if (firstTime == null || latestTime == null) {
            return null;
        }
        return BsData.getLatestTime(firstTime, latestTime);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"templateId\":")
                .append(templateId);
        sb.append(",\"subRouteDetailId\":")
                .append(subRouteDetailId);
        sb.append(",\"firstTime\":\"")
                .append(firstTime).append('\"');
        sb.append(",\"latestTime\":\"")
                .append(latestTime).append('\"');
        sb.append(",\"direction\":\"")
                .append(direction).append('\"');
        sb.append(",\"taskId\":")
                .append(taskId);
        sb.append(",\"arriveStaStopTime\":")
                .append(arriveStaStopTime);
        sb.append('}');
        return sb.toString();
    }
}
