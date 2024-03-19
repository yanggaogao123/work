package com.gci.schedule.driverless.bean.scheduleD;


import com.gci.schedule.driverless.util.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Route {

	private Long routeId;
	
	private String routeCode;
	
	private String routeName;

	private Long organId;
	
	private Map<Integer, RouteSub> routeSubMap;

	private Map<Long, List<RouteSta>> routeSubStaListMap=new HashMap<Long, List<RouteSta>>();
	
	private Date expiresTime;//失效时间

	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public String getRouteCode() {
		return routeCode;
	}

	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public RouteSub getRouteSub(int direction) {
		if(routeSubMap!=null)
			return routeSubMap.get(direction);
		return null;
	}

	public Map<Integer, RouteSub> getRouteSubMap() {
		return routeSubMap;
	}

	public void setRouteSubMap(Map<Integer, RouteSub> routeSubMap) {
		this.routeSubMap = routeSubMap;
		setExpiresTime(DateUtil.getDateAddMinute(new Date(), 60));
	}

	private void setExpiresTime(Date expiresTime) {
		this.expiresTime = expiresTime;
	}

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}

	public boolean isExpire() {
		if(expiresTime != null && expiresTime.before(new Date())) {
			return true;
		}
		return false;
	}

	public void addRouteSubStaList(Long routeSubId,List<RouteSta> routeSubStaList) {
		routeSubStaListMap.put(routeSubId, routeSubStaList);
	}

	public List<RouteSta> getRouteSubStaList(Long routeSubId) {
		return routeSubStaListMap.get(routeSubId);
	}
	
}
