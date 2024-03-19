package com.gci.schedule.driverless.controller;

import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.GenerateScheduleParams;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.enums.ScheduleStatus;
import com.gci.schedule.driverless.bean.scheduleD.*;
import com.gci.schedule.driverless.bean.vo.GenerateScheduleParams2;
import com.gci.schedule.driverless.bean.vo.ScheduleBySortParam;
import com.gci.schedule.driverless.service.schedule.GenerateScheduleService;
import com.gci.schedule.driverless.service.schedule.ScheduleTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/schedule")
public class GenerateScheduleController {
    @Autowired
    private GenerateScheduleService generateScheduleService;

    @Autowired
    private ScheduleTemplateService scheduleTemplateService;
    /**
     * 常规线排班生成
     * @param params
     * @return
     */
    @PostMapping("/generateSchedule")
    public R generateSchedule(@RequestBody GenerateScheduleParams params) {
        log.info("生成排班计划 - routeId:{} 入参:{}", params.getRouteId(), JSONObject.toJSONString(params));
        if (params.getRouteId() == null) {
            return R.error("线路id不能为空");
        }
        return generateScheduleService.generateSchedule(params);
    }

    /**
     * 支援排班生成
     * @param params
     * @return
     */
    @PostMapping("/generateSupportSchedule")
    public R generateSupportSchedule(@RequestBody GenerateScheduleParams2 params) {
        log.info("常规线支援生成排班计划 - routeId:{} 入参:{}", params.getRouteId(), JSONObject.toJSONString(params));
        if (params.getRouteId() == null) {
            return R.error("线路id不能为空");
        }
        return generateScheduleService.generateSupportSchedule(params);
    }

    @PostMapping("/getScheduleBySort")
    public R getScheduleBySort(@RequestBody ScheduleBySortParam params){
        log.info("查询排班计划 - routeId:{} 入参:{}", params.getRouteId(), JSONObject.toJSONString(params));
        if (params.getRouteId() == null) {
            return R.error("线路id不能为空");
        }
        return generateScheduleService.getScheduleBySort(params);
    }

    /**
     * 查询生成排班车辆配置
     * @param params
     * @return
     */
    @PostMapping("/getBusConfig")
    public R getBusConfig(@RequestBody GenerateScheduleParams2 params) {
        log.info("查询生成排班车辆配置 - routeId:{} 入参:{}", params.getRouteId(), JSONObject.toJSONString(params));
        if (params.getRouteId() == null) {
            return R.error("线路id不能为空");
        }
        return generateScheduleService.getBusConfig(params);
    }

    /**
     * 线路配车情况
     */
    @ResponseBody
    @RequestMapping("/busConfigure")
    public R busConfigure(@RequestBody Map<String, Object> params) {
        log.info("线路配车情况 - routeId:{} 入参:{}", params.get("routeId"), JSONObject.toJSONString(params));
        if (Objects.isNull(params.get("routeId"))) {
            return R.error("线路id不能为空");
        }

        try {
            BusConfigureVo vo = new BusConfigureVo();
            BusConfigure mainBusConfigure = null;
            BusConfigure subBusConfigure = null;
            if(Objects.nonNull(params.get("supportRouteId"))){
                Map<String, Object> mainMap = new HashMap<>();
                mainMap.put("routeId",params.get("routeId"));
                mainMap.put("runDate",params.get("runDate"));
                mainMap.put("status",ScheduleStatus.SUPPORTED_SCHEDULE.getValue());
                Map<String, Object> subMap = new HashMap<>();
                subMap.put("routeId",params.get("supportRouteId"));
                subMap.put("runDate",params.get("runDate"));
                subMap.put("status",ScheduleStatus.SUPPORT_SCHEDULE.getValue());
                mainBusConfigure = generateScheduleService.busConfigure(mainMap);
                subBusConfigure = generateScheduleService.busConfigure(subMap);
            }else {
                Map<String, Object> mainMap = new HashMap<>();
                mainMap.put("routeId",params.get("routeId"));
                mainMap.put("runDate",params.get("runDate"));
                mainMap.put("status",ScheduleStatus.DRIVERLESS_SCHEDULE.getValue());
                Map<String, Object> subMap = new HashMap<>();
                subMap.put("routeId",params.get("supportRouteId"));
                subMap.put("runDate",params.get("runDate"));
                subMap.put("status",ScheduleStatus.COMMON_SCHEDULE.getValue());
                mainBusConfigure = generateScheduleService.busConfigure(mainMap);
                subBusConfigure = generateScheduleService.busConfigure(subMap);
            }
            vo.setMainBusConfigure(mainBusConfigure);
            vo.setSubBusConfigure(subBusConfigure);
            return R.ok().put("data", vo);
        } catch (Exception e) {
            return R.error(500, "权限不足");
        }
    }

    @ResponseBody
    @RequestMapping("/getDriverlessDetail")
    public R getDriverlessDetail(@RequestBody ScheduleBySortParam params) {
        log.info("查询排班计划详情 - routeId:{} 入参:{}", params.getRouteId(), JSONObject.toJSONString(params));
        if (params.getRouteId() == null) {
            return R.error("线路id不能为空");
        }
        return generateScheduleService.getScheduleDetaiList(params);
    }

    @RequestMapping(value="/getJoinTemplateListByRouteId/{routeId}",method = RequestMethod.GET )
    @ResponseBody
    //@SysLog
    public R getJoinTemplateListByRouteId(HttpServletRequest request, @PathVariable Integer routeId) {
        List<ScheduleTemplateJoin> list=scheduleTemplateService.getJoinTemplateListByRouteId(routeId);
        return  R.ok().put("data", JSONObject.toJSON(list));
    }

    @RequestMapping("/mountCarPlan/{routeId}/{runMode}/{referenceDate}/{runDate}")
    @ResponseBody
    public R mountCarPlan(@PathVariable String routeId, @PathVariable String runMode,
                          @PathVariable String referenceDate, @PathVariable String runDate) {
        if (StringUtils.isEmpty(routeId) || StringUtils.isEmpty(runMode) || StringUtils.isEmpty(referenceDate) || StringUtils.isEmpty(runDate)) {
            return R.error("参数有误");
        }
        List<MountCarPlan> list = generateScheduleService.mountCarPlan(routeId, runMode, referenceDate, runDate);
        return R.ok().put("data", list);
    }

    //查询过去n天电子路单
    //routeId-线路id,referenceDate-参考日期
    @RequestMapping("/recentRunBus/{routeId}/{referenceDate}")
    @ResponseBody
    public R recentRunBus(@PathVariable String routeId, @PathVariable String referenceDate) {
        if (StringUtils.isEmpty(routeId) || StringUtils.isEmpty(referenceDate)) {
            return R.error("参数有误");
        }
        List<MountCarPlan> recentRunBus = generateScheduleService.recentRunBus(routeId, referenceDate);
        return R.ok().put("data", recentRunBus);
    }

    @RequestMapping(value = "/runBus/{routeId}", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    //@SysLog
    public String queryRunBus(@PathVariable String routeId,
                              HttpServletRequest request) {
        return generateScheduleService.runBus(routeId);
    }

    //保存挂车
    @RequestMapping("/saveMountCar")
    @ResponseBody
    public R saveMountCar(@RequestBody List<MountCarPlan> list, HttpServletRequest req) {
        if (CollectionUtils.isNotEmpty(list)) {
            try {
                String userId = String.valueOf(req.getSession().getAttribute("userId"));
                String userName = String.valueOf(req.getSession().getAttribute("userName"));
                return R.ok().put("data", generateScheduleService.saveMountCar(list, userId, userName));
            } catch (Exception e) {
                e.printStackTrace();
                return R.error(e.getMessage());
            }
        }
        return R.error();
    }

    @RequestMapping(value = "/runBusAndInfoNewRunBus/{routeId}", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    //@SysLog
    public R queryRunBusAndInfoByRouteNewRunBus(@PathVariable String routeId,
                              HttpServletRequest request) {
        return generateScheduleService.runBusAndInfoByRouteNewRunBus(routeId);
    }

    //获取批量挂车任务弹出框数据-> 类型：中途出场任务，首轮发班任务
    //params -> 方向，类型，线路，日期范围
    //type 0->中途出场任务 1->首轮发班任务
    @RequestMapping("/dispatchTask/{type}/{routeId}/{direction}/{referenceDate}/{runDate}")
    @ResponseBody
    public R dispatchTask(
            @PathVariable String routeId, @PathVariable String type, @PathVariable String direction,
            @PathVariable String referenceDate, @PathVariable String runDate) {
        List<DispatchTask> dispatchTasks = generateScheduleService.dispatchTask(type, routeId, direction, referenceDate,
                runDate);
        return R.ok().put("data", dispatchTasks);
    }

    /**
     * 简图监控信息
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/getMonitorInfo")
    public R getMonitorInfo(@RequestBody Map<String, Object> params) throws CloneNotSupportedException{
        log.info("监控调度支援计划 - routeId:{} 入参:{}", params.get("routeId"), JSONObject.toJSONString(params));
        if (Objects.isNull(params.get("routeId"))) {
            return R.error("线路id不能为空");
        }
        return generateScheduleService.getMonitorInfo(params);
    }

    /**
     * 监控调度车辆配置信息
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping("/getRuningScheduleConfig")
    public R getRuningScheduleConfig(@RequestBody Map<String, Object> params) {
        log.info("监控调度车辆配置信息 - routeId:{} 入参:{}", params.get("routeId"), JSONObject.toJSONString(params));
        if (Objects.isNull(params.get("routeId"))) {
            return R.error("线路id不能为空");
        }
        return generateScheduleService.getRuningScheduleConfig(params);
    }


}
