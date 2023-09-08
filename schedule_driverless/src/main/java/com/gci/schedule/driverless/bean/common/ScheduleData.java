package com.gci.schedule.driverless.bean.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.schedule.*;
import com.gci.schedule.driverless.service.server.ScheduleServerService;
import com.gci.schedule.driverless.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2023-05-16 16:44
 * @version: v1.0
 * @Modified by:
 **/
@Slf4j
public class ScheduleData {

    private static ScheduleServerService getScheduleServerService() {
        return SpringUtil.getBean(ScheduleServerService.class);
    }

    private static String getRouteIdApplyDay(Long routeId, Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        String applyDay = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));
        return routeId + "-" + applyDay;
    }

    public static Long getTemplateId(Long routeId) {
        Long minFirstTimeTs = BsData.getMinFirstTimeTs(routeId);
        String key = getRouteIdApplyDay(routeId, new Date(minFirstTimeTs));
        JSONArray details = getScheduleServerService().selectDetailListByRouteId(routeId);
        for (int i = 0; i < details.size(); i++) {
            JSONObject detail = details.getJSONObject(i);
            String k = detail.getString("routeId") + "-" + detail.getString("applyDay");
            Long v = detail.getLong("templateId");
            if (key.equals(k)) {
                return v;
            }
        }
        return null;
    }

    public static List<Anchor> getAnchor(Long routeId, String direction, Long templateId) {
        List<Anchor> anchors = getScheduleServerService().selectScheduleParamsAnchorListByRouteId(routeId);
        return anchors.stream().filter(a -> templateId.equals(a.getTemplateId())
                && direction.equals(a.getDirection())).collect(Collectors.toList());
    }

    /**
     * 停站时长/满载率参数
     *
     * @param routeId
     * @param direction
     * @return
     */
    public static Anchor getAnchor(Long routeId, String direction, Date now) {
        Long templateId = getTemplateId(routeId);
        if (templateId == null) {
            return null;
        }
        List<Anchor> anchors = getAnchor(routeId, direction, templateId);
        if (CollectionUtils.isEmpty(anchors)) {
            return null;
        }
        for (Anchor anchor : anchors) {
            Date beginTime = anchor.getBeginDateTime();
            Date endTime = anchor.getEndDateTime();
            if (now.getTime() >= beginTime.getTime()
                    && now.getTime() < endTime.getTime()) {
                return anchor;
            }
        }
        return null;
    }

    public static List<Classes> getClasses(Long routeId, String direction, Long templateId) {
        List<Classes> classes = getScheduleServerService().selectScheduleParamsClassesListByRouteId(routeId);
        return classes.stream().filter(a -> templateId.equals(a.getTemplateId())
                && direction.equals(a.getDirection())).collect(Collectors.toList());
    }

    public static Classes getClasses(Long routeId, String direction, Date now) {
        Long templateId = getTemplateId(routeId);
        if (templateId == null) {
            return null;
        }
        List<Classes> classesList = getClasses(routeId, direction, templateId);
        if (CollectionUtils.isEmpty(classesList)) {
            return null;
        }
        for (Classes classes : classesList) {
            Date beginTime = classes.getBeginDateTime();
            Date endTime = classes.getEndDateTime();
            if (now.getTime() >= beginTime.getTime()
                    && now.getTime() < endTime.getTime()) {
                return classes;
            }
        }
        return null;
    }

    public static List<SLStaSet> getSLStaSet(Long routeId, String direction, Long templateId) {
        List<SLStaSet> slStaSets = getScheduleServerService().selectScheduleParamsSLStaSetByRouteId(routeId);
        return slStaSets.stream().filter(a -> templateId.equals(a.getTemplateId())
                && direction.equals(a.getDirection())).collect(Collectors.toList());
    }

    /**
     * 短线站点参数
     *
     * @param routeId
     * @param direction
     * @return
     */
    public static List<SLStaSet> getSLStaSet(Long routeId, String direction) {
        Long templateId = getTemplateId(routeId);
        if (templateId == null) {
            return null;
        }
        return getSLStaSet(routeId, direction, templateId);
    }

    public static List<Eat> getEat(Long routeId, Long templateId) {
        List<Eat> eats = getScheduleServerService().selectScheduleParamsEatListByRouteId(routeId);
        return eats.stream().filter(e -> templateId.equals(e.getTemplateId())).collect(Collectors.toList());
    }

    /**
     * 吃饭(多时段) 参数
     *
     * @param routeId
     * @return
     */
    public static List<Eat> getEat(Long routeId) {
        Long templateId = getTemplateId(routeId);
        if (templateId == null) {
            return null;
        }
        return getEat(routeId, templateId);
    }

    public static List<RouteParams> getRouteParams(Long routeId, Long templateId) {
        List<RouteParams> routeParams = getScheduleServerService().selectScheduleParamsRouteListByRouteId(routeId);
        return routeParams.stream().filter(e -> templateId.equals(e.getTemplateId())).collect(Collectors.toList());
    }

    /**
     * 获取线路发班模式
     * AUTO_DISPATCH(((short) 0), "自动发班", null),
     * ALL_DAY_FIX_POINT(((short) 1), "全天定点", null),
     * FIXED_TIME_PERIOD(((short) 2), "时段定点", 7);
     *
     * @param routeId
     * @return
     */
    public static Integer getRouteDispatchMode(Long routeId) {
        return getScheduleServerService().getRouteDispatchMode(routeId);
    }

    /**
     * 线路运营参数
     *
     * @param routeId
     * @return
     */
    public static RouteParams getRouteParams(Long routeId) {
        Long templateId = getTemplateId(routeId);
        if (templateId == null) {
            return null;
        }
        List<RouteParams> routeParams = getRouteParams(routeId, templateId);
        return routeParams.isEmpty() ? null : routeParams.get(0);
    }

    public static List<ScheduleParamsEndurance> getCharges(Long routeId, Long templateId) {
        List<ScheduleParamsEndurance> endurances = getScheduleServerService().selectScheduleParamsChargeListByRouteId(routeId);
        return endurances.stream().filter(e -> templateId.equals(e.getTemplateId())).collect(Collectors.toList());
    }

    /**
     * 获取站点补电时间参数
     *
     * @param routeId
     * @param stationIds
     * @return
     */
    public static List<ScheduleParamsEndurance> getChargeParamList(Long routeId, List<Long> stationIds) {
        Long templateId = getTemplateId(routeId);
        log.info("【充电调度】--补电模板id:{}", templateId);
        if (templateId == null) {
            return null;
        }
        List<ScheduleParamsEndurance> chargeParamList = getCharges(routeId, templateId);
        List<ScheduleParamsEndurance> result = chargeParamList.stream()
                .filter(e -> stationIds.contains(e.getStationId())).collect(Collectors.toList());
        log.info("【充电调度】--补电模板参数值chargeParamList:{},result:{}",
                JSONObject.toJSONString(chargeParamList), JSONObject.toJSONString(result));
        return result;
    }


    /**
     * 是否双边出场
     *
     * @param routeId
     * @return
     */
    public static Boolean isAllEndDirection(Long routeId) {
        RouteParams routeParams = getRouteParams(routeId);
        if (routeParams == null || routeParams.getEndDirection() == null) {
            return false;
        }
        return Constant.Direction.ALL.equals(routeParams.getEndDirection());
    }

    /**
     * 是否到站挤走
     *
     * @param routeId
     * @param direction
     * @return
     */
    public static Boolean getArriveGoAtOnce(Long routeId, String direction) {
        RouteParams routeParams = getRouteParams(routeId);
        if (Objects.isNull(routeParams)) {
            return false;
        }
        Integer stopTime = Constant.Direction.UP.equals(direction) ?
                routeParams.getArriveStaStopTimeUp() : routeParams.getArriveStaStopTimeDown();
        return !Objects.isNull(stopTime);
    }

    /**
     * 获取时段最大间隔
     *
     * @param routeId
     * @param direction
     * @param finalTripBeginTime
     * @return
     */
    public static Long getMaxInterval(Long routeId, String direction, Date finalTripBeginTime) {
        long finalTripBeginTs = finalTripBeginTime.getTime();
        Classes classesA = getClasses(routeId, direction, finalTripBeginTime);
        if (classesA == null) {
            return CacheUtil.getSysMaxInterval(routeId);
        }
        Classes classesB = getClasses(routeId, direction,
                new Date(finalTripBeginTs + classesA.getMaxDispatchInterval() * 60 * 1000));
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

    /**
     * 获取时段最小停站
     *
     * @param routeId
     * @param direction
     * @param now
     * @return
     */
    public static Long getMinStopMinute(Long routeId, String direction, Date now) {
        Anchor anchor = getAnchor(routeId, direction, now);
        return anchor == null || anchor.getAnchorDurationMin() == null ?
                CacheUtil.getSysMinStopMinute() : anchor.getAnchorDurationMin();
    }

    /**
     * 获取时段最小停站
     *
     * @param routeId
     * @param direction
     * @param now
     * @return
     */
    public static Long getMaxStopMinute(Long routeId, String direction, Date now) {
        Anchor anchor = getAnchor(routeId, direction, now);
        return anchor == null || anchor.getAnchorDurationMax() == null ?
                CacheUtil.getSysMaxStopMinute() : anchor.getAnchorDurationMax();
    }

    public static Eat getEat(Long routeId, String direction, Date forecastTime) {
        List<Eat> eatList = getEat(routeId);
        if (!CollectionUtils.isEmpty(eatList)) {
            eatList.sort(Comparator.comparing(Eat::getBeginDateTime));
            for (Eat eat : eatList) {
                if (!direction.equals(eat.getDirection())
                        && !"2".equals(eat.getDirection())) {
                    continue;
                }
                long start = eat.getBeginDateTime().getTime();
                long end = start + CacheUtil.getSysEatInterval() * 60 * 1000;
                if (forecastTime.getTime() <= end) {
                    return eat;
                }
            }
        }
        return null;
    }

    /**
     * 获取调头时间(分钟)
     *
     * @param routeId
     * @param direction
     * @param firstStationId
     * @return
     */
    public static Integer getTurnAroundByFirst(Long routeId, String direction, Long firstStationId) {
        Integer res = 5;
        List<SLStaSet> slStaSet = getSLStaSet(routeId, direction);
        if (CollectionUtils.isEmpty(slStaSet)) {
            return res;
        }
        for (SLStaSet staSet : slStaSet) {
            if (firstStationId.equals(BsData.getStationIdByRouteStaId(staSet.getNextFirstStaId()))) {
                res = staSet.getTurnAroundTime();
                break;
            }
        }
        return res;
    }

    public static Integer getTurnAroundByLast(Long routeId, String direction, Long lastStationId) {
        Integer res = 5;
        List<SLStaSet> slStaSet = getSLStaSet(routeId, direction);
        if (CollectionUtils.isEmpty(slStaSet)) {
            return res;
        }
        for (SLStaSet staSet : slStaSet) {
            if (lastStationId.equals(BsData.getStationIdByRouteStaId(staSet.getLastStaId()))) {
                res = staSet.getTurnAroundTime();
                break;
            }
        }
        return res;
    }

    /**
     * 是否全天定点班车
     *
     * @return
     */
    public static boolean isRegularBus(Long routeId) {
        Integer routeDispatchMode = getRouteDispatchMode(routeId);
        if (Objects.isNull(routeDispatchMode)) {
            return false;
        }
        // 线路发班模式没配置，走原逻辑
//        if(routeDispatchMode == -1) {
//            RouteParams routeParams = getRouteParams(routeId);
//            return routeParams != null && routeParams.getIsRegularBus() != null && routeParams.getIsRegularBus() == 1;
//        }
        return routeDispatchMode == 1;
    }

    /**
     * 是否时段定点
     *
     * @param routeId
     * @param direction
     * @param finalTripBeginTime
     * @return
     */
    public static Boolean isRegularBusParams(Long routeId, String direction, Date finalTripBeginTime) {
        Classes classes = getClasses(routeId, direction, finalTripBeginTime);
        return classes != null && classes.isRegularBus();
    }

    /**
     * 是否反插
     *
     * @param routeId
     * @return
     */
    public static boolean isBackInsert(Long routeId) {
        RouteParams routeParams = getRouteParams(routeId);
        return routeParams != null
                && routeParams.getIsBackInsert() != null
                && routeParams.getIsBackInsert() == 1;
    }
}
