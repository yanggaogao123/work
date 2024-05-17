package com.gci.schedule.driverless.bean.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.schedule.*;
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
 * @Date: 2023-05-17 16:35
 * @version: v1.0
 * @Modified by:
 **/
public class SubData {

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

    public static Long getSubTemplateId(Long routeId) {
        String key = getRouteIdApplyDay(routeId);
        JSONArray details = getScheduleServerService().selectSubRouteDetailListByRouteId(routeId);
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


    public static List<BsSubRoute> getBsSubRoutes(Long routeId, Long templateId) {
        List<BsSubRoute> bsSubRoutes = getScheduleServerService().selectSubRouteListByRouteId(routeId);
        return bsSubRoutes.stream().filter(s -> templateId.equals(s.getTemplateId())).collect(Collectors.toList());
    }

    public static List<BsSubRouteAnchor> getBsSubRouteAnchor(Long routeId, Long detailId) {
        List<BsSubRouteAnchor> bsSubRouteAnchors = getScheduleServerService().selectSubRouteAnchorListByRouteId(routeId);
        return bsSubRouteAnchors.stream().filter(a -> detailId.equals(a.getSubRouteDetailId())).collect(Collectors.toList());
    }

    public static List<BsSubRouteClasses> getBsSubRouteClasses(Long routeId, Long detailId) {
        List<BsSubRouteClasses> bsSubRouteClasses = getScheduleServerService().selectSubRouteClassesListByRouteId(routeId);
        return bsSubRouteClasses.stream().filter(a -> detailId.equals(a.getSubRouteDetailId())).collect(Collectors.toList());
    }

    public static List<BsSubRouteEat> getBsSubRouteEat(Long routeId, Long detailId) {
        List<BsSubRouteEat> bsSubRouteEats = getScheduleServerService().selectSubRouteEatListByRouteId(routeId);
        return bsSubRouteEats.stream().filter(a -> detailId.equals(a.getSubRouteDetailId())).collect(Collectors.toList());
    }

    public static List<BsSubRoute> getBsSubRoutes(Long routeId) {
        Long templateId = getSubTemplateId(routeId);
        if (templateId == null) {
            return null;
        }
        List<BsSubRoute> bsSubRoutes = getBsSubRoutes(routeId, templateId);
        if (CollectionUtils.isEmpty(bsSubRoutes)) {
            return null;
        }
        for (BsSubRoute subRoute : bsSubRoutes) {
            if (subRoute.getGoSubRouteDetail() != null) {
                BsSubRouteDetail goDetail = subRoute.getGoSubRouteDetail();
                goDetail.setAnchors(getBsSubRouteAnchor(routeId, goDetail.getSubRouteDetailId()));
                goDetail.setClasses(getBsSubRouteClasses(routeId, goDetail.getSubRouteDetailId()));
                goDetail.setEats(getBsSubRouteEat(routeId, goDetail.getSubRouteDetailId()));
            }
            if (subRoute.getBackSubRouteDetail() != null) {
                BsSubRouteDetail backDetail = subRoute.getBackSubRouteDetail();
                backDetail.setAnchors(getBsSubRouteAnchor(routeId, backDetail.getSubRouteDetailId()));
                backDetail.setClasses(getBsSubRouteClasses(routeId, backDetail.getSubRouteDetailId()));
                backDetail.setEats(getBsSubRouteEat(routeId, backDetail.getSubRouteDetailId()));
            }
        }
        return bsSubRoutes;
    }

    public static Boolean isBsSubRoute(Long routeId) {
        return getBsSubRoutes(routeId) != null;
    }
}
