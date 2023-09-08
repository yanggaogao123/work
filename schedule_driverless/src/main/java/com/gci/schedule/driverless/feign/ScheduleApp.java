package com.gci.schedule.driverless.feign;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.DispatchTimeRequest;
import com.gci.schedule.driverless.bean.common.CodeResp;
import com.gci.schedule.driverless.bean.schedule.*;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-01-17 11:49 上午
 * @version: v1.0
 * @Modified by:
 **/
public interface ScheduleApp {

    @RequestLine("GET /schedulePlanReSet/doSyncPlan/{routeId}")
    CodeResp<Boolean> doSyncPlan(@Param("routeId") Long routeId);


    @RequestLine("GET /scheduleTemplate/selectDetailList/{routeId}?flag=gcikaifabu")
    CodeResp<JSONArray> selectDetailListByRouteId(@Param("routeId") Long routeId);

    @RequestLine("GET /scheduleParamsAnchor/selectList/{routeId}?flag=gcikaifabu")
    CodeResp<List<Anchor>> selectScheduleParamsAnchorListByRouteId(@Param("routeId") Long routeId);

    @RequestLine("GET /scheduleParamsSingle/selectList/{routeId}?flag=gcikaifabu")
    CodeResp<List<Single>> selectScheduleParamsSingleListByRouteId(@Param("routeId") Long routeId);

    @RequestLine("GET /scheduleParamsSLStaSet/selectList/{routeId}?flag=gcikaifabu")
    CodeResp<List<SLStaSet>> selectScheduleParamsSLStaSetByRouteId(@Param("routeId") Long routeId);

    @RequestLine("GET /scheduleParamsClasses/selectList/{routeId}?flag=gcikaifabu")
    CodeResp<List<Classes>> selectScheduleParamsClassesListByRouteId(@Param("routeId") Long routeId);

    @RequestLine("GET /scheduleParamsEat/selectList/{routeId}?flag=gcikaifabu")
    CodeResp<List<Eat>> selectScheduleParamsEatListByRouteId(@Param("routeId") Long routeId);

    @RequestLine("GET /scheduleParamsRoute/selectList/{routeId}?flag=gcikaifabu")
    CodeResp<List<RouteParams>> selectScheduleParamsRouteListByRouteId(@Param("routeId") Long routeId);

    @RequestLine("GET /scheduleParamsEndurance/selectList/{routeId}?flag=gcikaifabu")
    CodeResp<List<ScheduleParamsEndurance>> selectScheduleParamsChargeListByRouteId(@Param("routeId") Long routeId);


    @RequestLine("GET /dispatchTemplate/selectDetailList/{routeId}?flag=gcikaifabu")
    CodeResp<JSONArray> selectDispatchDetailListByRouteId(@Param("routeId") Long routeId);

    @RequestLine("GET /dispatchParamRuleShort/selectList/{routeId}?flag=gcikaifabu")
    CodeResp<List<RuleShort>> selectDispatchRuleShortListByRouteId(@Param("routeId") Long routeId);

    @RequestLine("GET /dispatchParamRuleParking/selectList/{routeId}?flag=gcikaifabu")
    CodeResp<List<RuleParking>> selectDispatchRuleParkingListByRouteId(@Param("routeId") Long routeId);


    @RequestLine("GET /bsSubRoute/selectList/{routeId}?flag=gcikaifabu")
    CodeResp<List<BsSubRoute>> selectSubRouteListByRouteId(@Param("routeId") Long routeId);

    @RequestLine("GET /bsSubRoute/selectDetailList/{routeId}?flag=gcikaifabu")
    CodeResp<JSONArray> selectSubRouteDetailListByRouteId(@Param("routeId") Long routeId);

    @RequestLine("GET /bsSubRoute/selectAnchorList/{routeId}?flag=gcikaifabu")
    CodeResp<List<BsSubRouteAnchor>> selectSubRouteAnchorListByRouteId(@Param("routeId") Long routeId);

    @RequestLine("GET /bsSubRoute/selectClassesList/{routeId}?flag=gcikaifabu")
    CodeResp<List<BsSubRouteClasses>> selectSubRouteClassesListByRouteId(@Param("routeId") Long routeId);

    @RequestLine("GET /bsSubRoute/selectEatList/{routeId}?flag=gcikaifabu")
    CodeResp<List<BsSubRouteEat>> selectSubRouteEatListByRouteId(@Param("routeId") Long routeId);


    @RequestLine("POST /simulation/getIntersiteTime")
    @Headers("Content-Type: application/json;charset=UTF-8")
    JSONObject getIntersiteTime(Map<String, Object> param);

    @RequestLine("POST /schedule/getLatestBusTripList?flag=gcikaifabu")
    @Headers("Content-Type: application/json;charset=UTF-8")
    CodeResp<Date> getLatestTripBeginTime(Map<String, Object> param);

    @RequestLine("POST /schedule/latestBusTrip/list?flag=gcikaifabu")
    @Headers("Content-Type: application/json;charset=UTF-8")
    CodeResp<List<LatestTrip>> getLatestTripList(Map<String, Object> param);

    @RequestLine("POST /dispatchPlan/getPlanTime?flag=gcikaifabu")
    @Headers("Content-Type: application/json;charset=UTF-8")
    JSONObject getPlanTime(DispatchTimeRequest request);

    @RequestLine("GET /routeDispatchMode/getModeConfigForDispatchAuto/{routeId}?flag=gcikaifabu")
    CodeResp<Integer> getRouteDispatchMode(@Param("routeId") Long routeId);
}
