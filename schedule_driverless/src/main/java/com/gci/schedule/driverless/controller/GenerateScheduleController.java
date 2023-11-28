package com.gci.schedule.driverless.controller;

import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.GenerateScheduleParams;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.enums.ScheduleStatus;
import com.gci.schedule.driverless.bean.scheduleD.BusConfigure;
import com.gci.schedule.driverless.bean.scheduleD.BusConfigureVo;
import com.gci.schedule.driverless.bean.vo.GenerateScheduleParams2;
import com.gci.schedule.driverless.bean.vo.ScheduleBySortParam;
import com.gci.schedule.driverless.service.schedule.GenerateScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/schedule")
public class GenerateScheduleController {
    @Autowired
    private GenerateScheduleService generateScheduleService;

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
}
