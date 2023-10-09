package com.gci.schedule.driverless.controller;

import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.GenerateScheduleParams;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.vo.GenerateScheduleParams2;
import com.gci.schedule.driverless.bean.vo.ScheduleBySortParam;
import com.gci.schedule.driverless.service.schedule.GenerateScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 常规线支援排班生成
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
}
