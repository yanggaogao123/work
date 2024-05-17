package com.gci.schedule.driverless.controller;

import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.vo.GenerateScheduleParams2;
import com.gci.schedule.driverless.bean.vo.ScheduleCountParam;
import com.gci.schedule.driverless.service.schedule.ScheduleCountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/scheduleCount")
public class ScheduleCountController {
    @Autowired
    private ScheduleCountService scheduleCountService;

    /**
     * 获取仿真统计数据
     * @param params
     * @return
     */
    @PostMapping("/getScheduleCountResult")
    public R getScheduleCountResult(@RequestBody ScheduleCountParam params) {
        log.info("获取仿真统计数据 - routeId:{} 入参:{}", params.getRouteId(), JSONObject.toJSONString(params));
        if (params.getRouteId() == null) {
            return R.error("线路id不能为空");
        }
        return scheduleCountService.getScheduleCountResult(params);
    }
}
