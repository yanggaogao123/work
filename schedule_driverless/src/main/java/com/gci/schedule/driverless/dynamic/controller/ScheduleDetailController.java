package com.gci.schedule.driverless.dynamic.controller;

import com.alibaba.fastjson.JSON;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.dynamic.bean.*;
import com.gci.schedule.driverless.dynamic.exception.MyException;
import com.gci.schedule.driverless.dynamic.service.SchedulePlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: allan
 * @Date: 2019/4/19 16:45
 */

@RestController
@RequestMapping("/dynamic")
public class ScheduleDetailController {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleDetailController.class);

    @Autowired
    private SchedulePlanService schedulePlanDynamicService;
    
    @ResponseBody
    @RequestMapping(value = "/generatePlan", method = RequestMethod.POST)
    public R generatePlan(@RequestBody ScheduleParamPreset scheduleParamPreset, HttpServletRequest request) {
    	return generatePlan(scheduleParamPreset);
    }
    
    public R generatePlan(ScheduleParamPreset scheduleParamPreset) {
    	try {
    		System.out.println(JSON.toJSONString(scheduleParamPreset));
            SchedulePlan4Mj schedulePlan4Mj = generateNew(scheduleParamPreset);
            return R.ok().put("data", schedulePlan4Mj);
		} catch (MyException e) {
			return R.error(-10, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return R.error(-1, "系统异常");
		}
	}

    private SchedulePlan4Mj generateNew(ScheduleParamPreset scheduleParamPreset) {
        scheduleParamPreset.setBusNumberOriginUp(scheduleParamPreset.getBusNumberUp());
        scheduleParamPreset.setBusNumberOriginDown(scheduleParamPreset.getBusNumberDown());
        SchedulePlan4Mj schedulePlan4Mj=null;
        try {
            schedulePlanDynamicService.generateNew(scheduleParamPreset);
        } catch (MyException e) {
            e.printStackTrace();
            throw e;
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("计划生成失败："+scheduleParamPreset.toString());
            throw e;
        }
        return schedulePlan4Mj;
    }
}
