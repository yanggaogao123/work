package com.gci.schedule.driverless.service.schedule.impl;

import com.gci.schedule.driverless.bean.scheduleD.RouteSta;
import com.gci.schedule.driverless.service.schedule.RouteStationService;
import com.gci.schedule.driverless.util.HttpUtil;
import com.gci.schedule.driverless.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("routeStationService")
public class RouteStationServiceImpl implements RouteStationService {

	@Value("${aptsBase.server.url}")
	private String aptsBaseUrl;

	@Value("${routeStationsByRouteId}")
	private String routeStationsByRouteId;


	private Map<Long, String> routeStaListStr = new HashMap<>();
	private Map<Long, List<RouteSta>> routeStaList = new HashMap<>();
	private Map<Long, List<RouteSta>> routeStaTerminalMap = new HashMap<>();


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

}
