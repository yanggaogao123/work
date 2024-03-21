package com.gci.schedule.driverless.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.GenerateScheduleParams;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.enums.ScheduleStatus;
import com.gci.schedule.driverless.bean.scheduleD.*;
import com.gci.schedule.driverless.bean.vo.GenerateScheduleParams2;
import com.gci.schedule.driverless.bean.vo.ScheduleBySortParam;
import com.gci.schedule.driverless.service.schedule.GenerateScheduleService;
import com.gci.schedule.driverless.service.schedule.QueryMonitorInfoService;
import com.gci.schedule.driverless.service.schedule.ScheduleTemplateService;
import com.gci.schedule.driverless.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/monitor")
public class QueryMonitorInfoController {
    @Autowired
    private QueryMonitorInfoService queryMonitorInfoService;
    @Value("${dispatch.auto.redispatch.url.byRouteAndDirection}")
    private String redispatch_url;
    @Value("${dispatch.auto.redispatch.url}")
    private String redispatch_url_save;


    private void setUser(Map param,HttpServletRequest req) {
        System.out.println(req.getSession().getId());
        String userId = String.valueOf(req.getSession().getAttribute("userId"));
        param.put("userId", userId);
        String userName = String.valueOf(req.getSession().getAttribute("userName"));
        param.put("userName", userName);
    }

    //实时车辆相关数据
    @RequestMapping(value = "/runbusRelatedData", method = RequestMethod.GET)
    @ResponseBody
    public R runbusRelatedData(@RequestParam String routeIdStr,HttpServletRequest req){
        String sessionId = (String) req.getSession().getAttribute("sessionId");
        String userId = String.valueOf(req.getSession().getAttribute("userId"));
        String userType = String.valueOf(req.getSession().getAttribute("userType"));
        String path = (String) req.getSession().getAttribute("path");
        JSONObject jsonObject = new JSONObject();
        //多条线路 逗号拼接
        //预测发班计划
        JSONArray forecastPlanResult = queryMonitorInfoService.getForecastPlan(routeIdStr,userId);
        //发班参数
        JSONObject tripParamsResult = queryMonitorInfoService.getTripParam(routeIdStr,userId);
        //重排
        JSONObject redispatchListResult = queryMonitorInfoService.redispatchList(routeIdStr, userId, userType);
        jsonObject.put("forecastPlanResult",forecastPlanResult);
        jsonObject.put("tripParamsResult",tripParamsResult);
        jsonObject.put("redispatchListResult",redispatchListResult);
        return R.ok("成功").put("data",jsonObject);

    }

    /**
     * 刷新时间
     * @param refreshTimeType
     * @param request
     * @return
     */
    @RequestMapping(value = "/getRefreshTime/{refreshTimeType}", method = RequestMethod.GET)
    @ResponseBody
    public String getRefreshTime(
            @PathVariable("refreshTimeType") String refreshTimeType, HttpServletRequest request) {
        return queryMonitorInfoService.getRefreshTime(refreshTimeType);
    }

    //前端按钮-获取重排数据状态，返回成功则直接读取redis数据。
    @RequestMapping(value = "/redispatchByRouteAndDirection/{routeId}/{direction}")
    @ResponseBody
    public String redispatchList(@PathVariable String routeId, @PathVariable String direction,
                                 HttpServletRequest request) {
        String userId = (String) request.getSession().getAttribute("userId");
        String url = redispatch_url.replaceAll("ROUTEID", routeId).replaceAll("DIRECTION", direction).replaceAll("USERID", userId);
        log.info("[重排数据状态]" + " 原始数据 routeId = " + routeId + " direction =  " + direction + " url = " + url);
        JSONObject result = new JSONObject();
        try {
            result = HttpClientUtils.httpPost(url, new JSONObject());
        } catch (Exception e) {
            log.error("网络异常，请稍后重试！");
            result.put("retMsg", "网络异常，请稍后重试！");
        } finally {
            log.info("[重排数据状态]" + " 原始数据 routeId = " + routeId + " direction =  " + direction + "結果 result = " + result.toString());
        }
        return result.toString();
    }

    /**
     * 重排弹窗
     * @param routeId
     * @param request
     * @return
     */
    @RequestMapping(value = "/redispatchList/{routeId}")
    @ResponseBody
    public R redispatchList(@PathVariable String routeId, HttpServletRequest request) {
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        String userType = String.valueOf(request.getSession().getAttribute("userType"));
        JSONObject redispatchListResult = queryMonitorInfoService.redispatchList(routeId, userId, userType);
        return R.ok("成功").put("data",redispatchListResult);
    }

    /**
     * 重排弹窗子线路列表
     * @param routeId
     * @param direction
     * @param sessionId
     * @param path
     * @param request
     * @return
     */
    @RequestMapping(value = "/dispatchTaskFromMainStation")
    @ResponseBody
    public JSONObject dispatchTaskFromMainStation(@RequestParam String routeId,
                                                  @RequestParam String direction,
                                                  @RequestParam String sessionId, @RequestParam String path,
                                                  HttpServletRequest request) {
        return queryMonitorInfoService.dispatchTaskFromMainStation(routeId, direction, sessionId, path);
    }

    /**
     * 重新计算
     * @param params
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/routeReDispatch", method = RequestMethod.POST)
    public String routeReDispatch(@RequestBody List<Map<String, Object>> params, HttpServletRequest request) {
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        String userName = String.valueOf(request.getSession().getAttribute("userName"));
        log.info("线路重排,操作人:" + userId + "|" + userName + " 参数：" + params);
        return queryMonitorInfoService.routeReDispatch(JSONObject.toJSONString(params));
    }

    /**
     * 发班重排
     * @param param
     * @param req
     * @return
     */
    @RequestMapping("/redispatch")
    @ResponseBody
    public String redispatch(@RequestBody Map<String, Object> param, HttpServletRequest req) {
        setUser(param, req);
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        JSONObject result = new JSONObject();
        try {
            result = HttpClientUtils.httpPost(redispatch_url_save, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            log.info("[发班重打时间]" +" 原始数据 data = "+ jsonObject.toString() + "結果 result = " + result.toString());
        }
        return result.toString();
    }

    /**
     * 首轮计划列表
     * @param routeId
     * @param req
     * @return
     */
    @RequestMapping("/getFrPlanList/{routeId}")
    @ResponseBody
    public String getFrPlanList(@PathVariable String routeId, HttpServletRequest req) {
        return queryMonitorInfoService.getFrPlanList(routeId);
    }

    /**
     * 失班设置
     * @param param
     * @param req
     * @return
     */
    @RequestMapping("/reCalculByLost")
    @ResponseBody
    public String reCalculByLost(@RequestBody Map param, HttpServletRequest req) {
        setUser(param, req);
        log.info("首轮失班: " + JSON.toJSONString(param));
        return queryMonitorInfoService.reCalculByLost(param);
    }

    /**
     * 保存首轮
     * @param param
     * @param req
     * @return
     */
    @RequestMapping("/saveFrPlanList")
    @ResponseBody
    public String saveFrPlanList(@RequestBody Map param, HttpServletRequest req) {
        setUser(param, req);
        log.info("首轮保存: " + JSON.toJSONString(param));
        return queryMonitorInfoService.saveFrPlanList(param);
    }

    /**
     * 获取距离
     * @param lon
     * @param lat
     * @param stationId
     * @param request
     * @return
     */
    @RequestMapping(value = "/getDistance/{lon}/{lat}/{stationId}")
    @ResponseBody
    public String getDistance(@PathVariable String lon, @PathVariable String lat, @PathVariable String stationId,
                              HttpServletRequest request) {
        try {
            return queryMonitorInfoService.getDistance(lon, lat, stationId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询发班参数设置
     * @param routeId
     * @param request
     * @return
     */
    @RequestMapping("/getTripParam/{routeId}")
    @ResponseBody
    public R getTripParam(@PathVariable String routeId, HttpServletRequest request) {
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        //发班参数
        JSONObject tripParamsResult = queryMonitorInfoService.getTripParam(routeId,userId);
        return R.ok("成功").put("data",tripParamsResult);
    }

    /**
     * 保存发班参数设置
     * @param param
     * @param req
     * @return
     */
    @RequestMapping("/saveTripParam")
    @ResponseBody
    public String saveTripParam(@RequestBody Map<String, Object> param, HttpServletRequest req) {
        setUser(param, req);
        return queryMonitorInfoService.saveTripParam(param);
    }

}
