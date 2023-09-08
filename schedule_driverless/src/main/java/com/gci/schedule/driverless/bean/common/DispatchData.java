package com.gci.schedule.driverless.bean.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.schedule.RuleParking;
import com.gci.schedule.driverless.bean.schedule.RuleShort;
import com.gci.schedule.driverless.service.server.ScheduleServerService;
import com.gci.schedule.driverless.util.SpringUtil;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2023-05-17 15:49
 * @version: v1.0
 * @Modified by:
 **/
public class DispatchData {

    private static ScheduleServerService getScheduleServerService() {
        return SpringUtil.getBean(ScheduleServerService.class);
    }

    private static String getRouteIdApplyDay(Long routeId, Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        String applyDay = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));
        return routeId + "-" + applyDay;
    }

    private static String getRouteIdApplyDay(Long routeId) {
        return getRouteIdApplyDay(routeId, new Date());
    }

    public static Long getDispatchTemplateId(Long routeId) {
        String key = getRouteIdApplyDay(routeId);
        JSONArray details = getScheduleServerService().selectDispatchDetailListByRouteId(routeId);
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

    public static List<RuleShort> getRuleShort(Long routeId, String direction, Long templateId) {
        List<RuleShort> ruleShorts = getScheduleServerService().selectDispatchRuleShortListByRouteId(routeId);
        return ruleShorts.stream().filter(r -> templateId.equals(r.getTemplateId())
                && direction.equals(r.getDirection())).collect(Collectors.toList());
    }


    /**
     * 获取时段短线发班规则
     *
     * @param routeId
     * @param direction
     * @param time
     * @return
     */
    public static RuleShort getRuleShort(Long routeId, String direction, Date time) {
        Long templateId = getDispatchTemplateId(routeId);
        if (templateId == null) {
            return null;
        }
        List<RuleShort> ruleShorts = getRuleShort(routeId, direction, templateId);
        if (CollectionUtils.isEmpty(ruleShorts)) {
            return null;
        }
        for (RuleShort ruleShort : ruleShorts) {
            Date b = ruleShort.getBeginDateTime();
            Date e = ruleShort.getEndDateTime();
            long tmp = time.getTime() + ruleShort.getFullClassesInterval() * 60 * 1000;
            if (tmp >= b.getTime() && tmp < e.getTime()) {
                return ruleShort;
            }
        }
        return null;
    }

    public static List<RuleParking> getRuleParking(Long routeId, Long stationId, Long templateId) {
        List<RuleParking> ruleParkings = getScheduleServerService().selectDispatchRuleParkingListByRouteId(routeId);
        return ruleParkings.stream()
                .filter(r -> templateId.equals(r.getTemplateId()) && stationId.equals(r.getStationId()))
                .collect(Collectors.toList());

    }

    public static RuleParking getRuleParking(Long routeId, Long stationId, Date now) {
        Long templateId = getDispatchTemplateId(routeId);
        if (templateId == null) {
            return null;
        }
        List<RuleParking> ruleParkings = getRuleParking(routeId, stationId, templateId);
        if (CollectionUtils.isEmpty(ruleParkings)) {
            return null;
        }
        for (RuleParking ruleParking : ruleParkings) {
            Date b = ruleParking.getBeginDateTime();
            Date e = ruleParking.getEndDateTime();
            if (now.getTime() >= b.getTime()
                    && now.getTime() < e.getTime()) {
                return ruleParking;
            }
        }
        return null;
    }
}
