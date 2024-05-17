package com.gci.schedule.driverless.dynamic.service.impl;

import com.gci.schedule.driverless.dynamic.bean.RouteSta;
import com.gci.schedule.driverless.dynamic.service.RouteStationService;
import com.gci.schedule.driverless.dynamic.util.HttpUtil;
import com.gci.schedule.driverless.dynamic.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.gci.schedule.driverless.dynamic.util.JsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("routeStationDynamicService")
public class RouteStationServiceImpl implements RouteStationService {

	@Value("${aptsBase.server.url}")
	private String aptsBaseUrl;

	@Value("${routeStationsByRouteId}")
	private String routeStationsByRouteId;

	private Map<Long, String> routeStaListStr = new HashMap<>();

	private Map<Long, List<RouteSta>> routeStaList = new HashMap<>();

	private Map<Long, List<RouteSta>> routeStaTerminalMap = new HashMap<>();

	@Override
	public void clearRouteStaCache(){
		routeStaListStr = new HashMap<>();
		routeStaList = new HashMap<>();
		routeStaTerminalMap.clear();
	}

	/**
	 * 查询线路站点
	 */
	@Override
	public String getListByRouteId(Long routeId) {
		String str = routeStaListStr.get(routeId);
		if (StringUtil.isNotBlank(str)) {
			return str;
		}
		String result = HttpUtil.getString(aptsBaseUrl + "/" + routeStationsByRouteId+"/"+routeId);
		//String result = restTemplate.getForObject(aptsBaseUrl + "/" + routeStationsByRouteId+"/"+routeId, String.class);
		routeStaListStr.put(routeId, result);
		return result;
	}

	@Override
	public List<RouteSta> getRouteStaListByRouteId(Long routeId) {
		List<RouteSta> routeStas = routeStaList.get(routeId);
		if (!CollectionUtils.isEmpty(routeStas)) {
			return routeStas;
		}
		String result = HttpUtil.getString(aptsBaseUrl + "/" + routeStationsByRouteId+"/"+routeId);
		//String result = restTemplate.getForObject(aptsBaseUrl + "/" + routeStationsByRouteId+"/"+routeId, String.class);
		List<RouteSta> list=JsonUtil.getListByKey(result, RouteSta.class, "data");
		routeStaList.put(routeId, list);
		return list;
	}

	@Override
	public Map<Long,RouteSta> getRouteStaMapByRouteId(Long routeId) {
		List<RouteSta> list=getRouteStaListByRouteId(routeId);
		Map<Long, RouteSta> map=new HashMap<Long, RouteSta>();
		for(RouteSta routeSta:list) {
			map.put(routeSta.getRouteStationId(), routeSta);
		}
		return map;
	}
}
