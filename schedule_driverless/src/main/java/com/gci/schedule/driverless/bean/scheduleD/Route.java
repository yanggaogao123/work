package com.gci.schedule.driverless.bean.scheduleD;


import com.gci.schedule.driverless.util.DateUtil;

import java.util.Date;
import java.util.Map;

public class Route {

	private Long routeId;
	
	private String routeCode;
	
	private String routeName;
	
	private Map<Integer, RouteSub> routeSubMap;
	
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
	
	public boolean isExpire() {
		if(expiresTime.before(new Date())) {
			return true;
		}
		return false;
	}
	
}
