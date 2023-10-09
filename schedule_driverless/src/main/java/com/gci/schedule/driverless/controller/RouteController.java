package com.gci.schedule.driverless.controller;

import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.service.schedule.RouteService;
import com.gci.schedule.driverless.service.schedule.ScheduleRouteConfigService;
import com.gci.schedule.driverless.util.HttpRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author: zhouyanjie
  * @param
 * @Description:线路
 * @Date: Created in 2019/4/19  17:05
 */
@Controller("routeController")
@RequestMapping("/route")
public class RouteController {


	@Autowired
	private ScheduleRouteConfigService scheduleRouteConfigService;

	@Resource
	private RouteService routeService;

	/**
	 * 获取机构线路
	 */
	@RequestMapping("/getAllRouteList")
	@ResponseBody
	public R getAllRouteList(){
		List<Map<String, String>> result = scheduleRouteConfigService.getAllRoute();
		return R.ok().put("data",result);
	}

	/**
	 * 获取机构线路
	 */
	@RequestMapping("/getRouteList")
	@ResponseBody
	//@SysLog
	public R getRouteList(@RequestParam Map<String, String> params, HttpServletRequest req){
		String organId = HttpRequestUtil.getOrganId(req);
		List<Map> list = routeService.getRouteListNew(organId);
//		List<Map> list = routeService.getRouteListNew("20");//区间排班计划 262
//		List<Map> list = routeService.getRouteListNew("18");//921路 359  测试
		return R.ok().put("data",list);
	}

}
