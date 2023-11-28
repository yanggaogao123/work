package com.gci.schedule.driverless.controller;

import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.service.schedule.RouteStationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: zhouyanjie
  * @param
 * @Description:线路站点
 * @Date: Created in 2019/4/19  17:05
 */
@Controller("routeStationController")
@RequestMapping("/routeStation")
public class RouteStationController {
	
	private static final Logger log = LoggerFactory.getLogger(RouteStationController.class);
	
	@Resource
	private RouteStationService routeStationService;

	@RequestMapping(value="/getListByRouteId/{routeId}",method = RequestMethod.GET , produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	//@SysLog
	//查询线路站点
	public R getListByRouteId(HttpServletRequest request, @PathVariable Integer routeId) {
		if(routeId!=null){
			String result=routeStationService.getListByRouteId(Long.valueOf(routeId));
			JSONObject json = JSONObject.parseObject(result);
			return R.ok().put("data",json.get("data"));

		}else{
			return  R.error("权限不足");
		}
	}


}
