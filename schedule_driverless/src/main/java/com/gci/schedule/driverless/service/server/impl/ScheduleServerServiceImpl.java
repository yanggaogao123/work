package com.gci.schedule.driverless.service.server.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.DispatchTimeRequest;
import com.gci.schedule.driverless.bean.common.CodeResp;
import com.gci.schedule.driverless.bean.schedule.*;
import com.gci.schedule.driverless.feign.ScheduleApp;
import com.gci.schedule.driverless.service.server.ScheduleServerService;
import com.gci.schedule.driverless.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Service
public class ScheduleServerServiceImpl implements ScheduleServerService {

    @Autowired
    private ScheduleApp scheduleApp;
    @Autowired
    private ScheduleApp scheduleApp2;


    @Override
    public List<SchedulePlan> getPlanTime(DispatchTimeRequest request) {
        List<SchedulePlan> res = new ArrayList<>();
        Long routeId = request.getRouteId();
        String busName = request.getBusArrivalList().isEmpty() ? "" : request.getBusArrivalList().get(0).getBusName();
        log.info("[排班计算] - routeId:{} busName:{} >>> {}", routeId, busName, JSONObject.toJSONString(request));
        JSONObject object = scheduleApp2.getPlanTime(request);
        log.info("[排班计算] - routeId:{} busName:{} <<< {}", routeId, busName, JSONObject.toJSONString(object));

        // 最大间隔设置异常提示
        if (object.containsKey("retMsg") && !StringUtils.isEmpty(object.getString("retMsg"))) {

        }

        if (!object.containsKey("dispatchPlanList")) {
            return res;
        }
        JSONArray array = object.getJSONArray("dispatchPlanList");
        for (int i = 0; i < array.size(); i++) {
            JSONObject o = array.getJSONObject(i);
            Long busId = o.getLong("busId");
            String bName = o.getString("busName");
            String planTime = o.getString("planTime");
            res.add(SchedulePlan.builder()
                    .busId(busId)
                    .busName(bName)
                    .planTime(DateUtil.str2Date(planTime, DateUtil.yyyyMMdd_HHmmss))
                    .build());
        }
        return res;
    }

    @Override
    public Double getIntersiteTime(Long routeId, String direction, Date adTime) {
        Map<String, Object> param = new HashMap<>();
        param.put("routeId", routeId);
        param.put("direction", direction);
        param.put("adTime", DateUtil.date2Str(adTime, DateUtil.time_sdf));
        return getIntersiteTime(param);
    }

    @Override
    public Double getIntersiteTime(Long routeId, String direction,
                                   Long fromRouteStaId, Long toRouteStaId, Date adTime) {
        Map<String, Object> param = new HashMap<>();
        param.put("routeId", routeId);
        param.put("direction", direction);
        param.put("fromRouteStaId", fromRouteStaId);
        param.put("toRouteStaId", toRouteStaId);
        param.put("adTime", DateUtil.date2Str(adTime, DateUtil.time_sdf));
        return getIntersiteTime(param);
    }

    private Double getIntersiteTime(Map<String, Object> param) {
        JSONObject result = scheduleApp.getIntersiteTime(param);
        if (!result.containsKey("code") || !"0".equals(result.getString("code"))) {
            return null;
        }
        return result.getDouble("runTime");
    }

    @Override
    public Date getLatestTripBeginTime(Long routeId, String direction, Date runDate) {
        Map<String, Object> param = new HashMap<>();
        param.put("routeId", routeId);
        param.put("direction", direction);
        param.put("runDate", DateUtil.date2Str(runDate, DateUtil.date_sdf));
        CodeResp<Date> codeResp = scheduleApp.getLatestTripBeginTime(param);
        if (codeResp.getCode() != 0) {
            return null;
        }
        return codeResp.getData();
    }

    @Override
    public List<LatestTrip> getLatestTripList(Long routeId, String direction, Date runDate) {
        Map<String, Object> param = new HashMap<>();
        param.put("routeId", routeId);
        param.put("direction", direction);
        param.put("runDate", DateUtil.date2Str(runDate, DateUtil.date_sdf));
        CodeResp<List<LatestTrip>> codeResp = scheduleApp.getLatestTripList(param);
        if (codeResp.getCode() != 0) {
            return null;
        }
        if (!CollectionUtils.isEmpty(codeResp.getData())) {
            for (LatestTrip d : codeResp.getData()) {
                d.setRouteId(routeId);
            }
        }
        return codeResp.getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "routeDispatchModeByRouteId")
    @Override
    public Integer getRouteDispatchMode(Long routeId) {
        CodeResp<Integer> routeDispatchMode = scheduleApp.getRouteDispatchMode(routeId);
        if (routeDispatchMode.getCode() == 0) {
            return routeDispatchMode.getData();
        }
        return null;
    }

    @Override
    public Boolean doSyncPlan(Long routeId) {
        return scheduleApp.doSyncPlan(routeId).getData();
    }


    @Cacheable(unless = "#result == null", cacheNames = "selectScheduleParamsDetailListByRouteId")
    @Override
    public JSONArray selectDetailListByRouteId(Long routeId) {
        return scheduleApp.selectDetailListByRouteId(routeId).getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectScheduleParamsAnchorListByRouteId")
    @Override
    public List<Anchor> selectScheduleParamsAnchorListByRouteId(Long routeId) {
        return scheduleApp.selectScheduleParamsAnchorListByRouteId(routeId).getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectScheduleParamsClassesListByRouteId")
    @Override
    public List<Classes> selectScheduleParamsClassesListByRouteId(Long routeId) {
        return scheduleApp.selectScheduleParamsClassesListByRouteId(routeId).getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectScheduleParamsSingleListByRouteId")
    @Override
    public List<Single> selectScheduleParamsSingleListByRouteId(Long routeId) {
        return scheduleApp.selectScheduleParamsSingleListByRouteId(routeId).getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectScheduleParamsSLStaSetByRouteId")
    @Override
    public List<SLStaSet> selectScheduleParamsSLStaSetByRouteId(Long routeId) {
        return scheduleApp.selectScheduleParamsSLStaSetByRouteId(routeId).getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectScheduleParamsEatListByRouteId")
    @Override
    public List<Eat> selectScheduleParamsEatListByRouteId(Long routeId) {
        return scheduleApp.selectScheduleParamsEatListByRouteId(routeId).getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectScheduleParamsRouteListByRouteId")
    @Override
    public List<RouteParams> selectScheduleParamsRouteListByRouteId(Long routeId) {
        return scheduleApp.selectScheduleParamsRouteListByRouteId(routeId).getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectScheduleParamsChargeListByRouteId")
    @Override
    public List<ScheduleParamsEndurance> selectScheduleParamsChargeListByRouteId(Long routeId) {
        return scheduleApp.selectScheduleParamsChargeListByRouteId(routeId).getData();
    }


    @Cacheable(unless = "#result == null", cacheNames = "selectDispatchParamsDetailListByRouteId")
    @Override
    public JSONArray selectDispatchDetailListByRouteId(Long routeId) {
        return scheduleApp.selectDispatchDetailListByRouteId(routeId).getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectDispatchParamsRuleShortListByRouteId")
    @Override
    public List<RuleShort> selectDispatchRuleShortListByRouteId(Long routeId) {
        return scheduleApp.selectDispatchRuleShortListByRouteId(routeId).getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectDispatchParamsRuleParkingListByRouteId")
    @Override
    public List<RuleParking> selectDispatchRuleParkingListByRouteId(Long routeId) {
        return scheduleApp.selectDispatchRuleParkingListByRouteId(routeId).getData();
    }


    @Cacheable(unless = "#result == null", cacheNames = "selectSubRouteListByRouteId")
    @Override
    public List<BsSubRoute> selectSubRouteListByRouteId(Long routeId) {
        return scheduleApp.selectSubRouteListByRouteId(routeId).getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectSubRouteDetailListByRouteId")
    @Override
    public JSONArray selectSubRouteDetailListByRouteId(Long routeId) {
        return scheduleApp.selectSubRouteDetailListByRouteId(routeId).getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectSubRouteAnchorListByRouteId")
    @Override
    public List<BsSubRouteAnchor> selectSubRouteAnchorListByRouteId(Long routeId) {
        return scheduleApp.selectSubRouteAnchorListByRouteId(routeId).getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectSubRouteClassesListByRouteId")
    @Override
    public List<BsSubRouteClasses> selectSubRouteClassesListByRouteId(Long routeId) {
        return scheduleApp.selectSubRouteClassesListByRouteId(routeId).getData();
    }

    @Cacheable(unless = "#result == null", cacheNames = "selectSubRouteEatListByRouteId")
    @Override
    public List<BsSubRouteEat> selectSubRouteEatListByRouteId(Long routeId) {
        return scheduleApp.selectSubRouteEatListByRouteId(routeId).getData();
    }

}
