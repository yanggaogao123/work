package com.gci.schedule.driverless.service.server;

import com.alibaba.fastjson.JSONArray;
import com.gci.schedule.driverless.bean.DispatchTimeRequest;
import com.gci.schedule.driverless.bean.schedule.*;

import java.util.Date;
import java.util.List;

public interface ScheduleServerService {

    List<SchedulePlan> getPlanTime(DispatchTimeRequest request);

    Boolean doSyncPlan(Long routeId);


    JSONArray selectDetailListByRouteId(Long routeId);

    List<Anchor> selectScheduleParamsAnchorListByRouteId(Long routeId);

    List<Classes> selectScheduleParamsClassesListByRouteId(Long routeId);

    List<Single> selectScheduleParamsSingleListByRouteId(Long routeId);

    List<SLStaSet> selectScheduleParamsSLStaSetByRouteId(Long routeId);

    List<Eat> selectScheduleParamsEatListByRouteId(Long routeId);

    List<RouteParams> selectScheduleParamsRouteListByRouteId(Long routeId);

    List<ScheduleParamsEndurance> selectScheduleParamsChargeListByRouteId(Long routeId);


    JSONArray selectDispatchDetailListByRouteId(Long routeId);

    List<RuleShort> selectDispatchRuleShortListByRouteId(Long routeId);

    List<RuleParking> selectDispatchRuleParkingListByRouteId(Long routeId);

    List<BsSubRoute> selectSubRouteListByRouteId(Long routeId);

    JSONArray selectSubRouteDetailListByRouteId(Long routeId);

    List<BsSubRouteAnchor> selectSubRouteAnchorListByRouteId(Long routeId);

    List<BsSubRouteClasses> selectSubRouteClassesListByRouteId(Long routeId);

    List<BsSubRouteEat> selectSubRouteEatListByRouteId(Long routeId);

    Double getIntersiteTime(Long routeId, String direction, Date adTime);

    Double getIntersiteTime(Long routeId, String direction,
                            Long fromRouteStaId, Long toRouteStaId, Date adTime);

    Date getLatestTripBeginTime(Long routeId, String direction, Date runDate);

    List<LatestTrip> getLatestTripList(Long routeId, String direction, Date runDate);

    Integer getRouteDispatchMode(Long routeId);
}
