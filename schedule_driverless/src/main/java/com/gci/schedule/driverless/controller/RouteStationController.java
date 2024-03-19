package com.gci.schedule.driverless.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.scheduleD.DyDriverlessRouteSta;
import com.gci.schedule.driverless.service.schedule.RouteStationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
			List<DyDriverlessRouteSta> staList =routeStationService.selectByRouteId(Long.valueOf(routeId));
			Map<Long,List<DyDriverlessRouteSta>> staMap = new HashMap<>();
			if(!CollectionUtils.isEmpty(staList)){
				staMap = staList.stream().collect(Collectors.groupingBy(DyDriverlessRouteSta::getRouteStaId));
			}
			JSONObject json = JSONObject.parseObject(result);
			JSONArray jsonArray = json.getJSONArray("data");
			for(int i=0;i<jsonArray.size();i++){
				JSONObject jsonStation = jsonArray.getJSONObject(i);
				Long routeStationId = jsonStation.getLong("routeStationId");
				//设置简称
				if(staMap.containsKey(routeStationId)){
					jsonStation.put("routeStationName",staMap.get(routeStationId).get(0).getRouteStationSimplename());
				}
			}
			return R.ok().put("data",jsonArray);

		}else{
			return  R.error("权限不足");
		}
	}


}
