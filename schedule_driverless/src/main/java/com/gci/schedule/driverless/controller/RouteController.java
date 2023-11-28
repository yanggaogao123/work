package com.gci.schedule.driverless.controller;

import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.scheduleD.DriverlessUpDownInfoVo;
import com.gci.schedule.driverless.bean.scheduleD.RouteUpDownInfo;
import com.gci.schedule.driverless.service.schedule.GenerateScheduleService;
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

	@Autowired
	private GenerateScheduleService generateScheduleService;

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

	@RequestMapping(value="/getRouteUpDownInfo",method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	//@SysLog
	//根据线路和方向查询线路上下行信息（首末站、首末班发班时间和所属企业）
	public R getRouteUpDownInfo(HttpServletRequest request,@RequestBody Map<String, Object> json) {
		Long routeId = Long.valueOf(json.get("routeId").toString());
		Long supportRouteId = null;
		if(Objects.nonNull(json.get("supportRouteId"))){
			supportRouteId = Long.valueOf(json.get("supportRouteId").toString());
		}
		List<RouteUpDownInfo> mainList = routeService.getRouteUpDownInfo(routeId);
		List<RouteUpDownInfo> subList = null;
		if(Objects.nonNull(supportRouteId)){
			subList = routeService.getRouteUpDownInfo(supportRouteId);
		}else {
			subList = mainList;
		}
		DriverlessUpDownInfoVo vo = new DriverlessUpDownInfoVo();
		vo.setMainList(mainList);
		vo.setSubList(subList);
		return R.ok().put("data",vo);
	}

	@RequestMapping(value="/getUnionRouteInfo",method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public R getUnionRouteInfo(HttpServletRequest request,@RequestBody Map<String, Object> json) {
		Long routeId = Long.valueOf(json.get("routeId").toString());
		return generateScheduleService.getUnionRouteInfo(routeId);
	}


}
