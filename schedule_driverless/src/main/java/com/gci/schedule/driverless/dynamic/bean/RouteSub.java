package com.gci.schedule.driverless.dynamic.bean;

import java.util.ArrayList;
import java.util.List;

public class RouteSub {
	
	private int direction;
	
	private List<RouteSta> routeStaList=new ArrayList<RouteSta>();

	public RouteSub(int direction) {
		super();
		this.direction = direction;
	}

	/*public RouteSub(List<RouteSta> routeStaList) {
		this.routeStaList = routeStaList;
	}
	*/
	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	public List<RouteSta> getRouteStaList() {
		return routeStaList;
	}

	public void addRouteSta(RouteSta routeSta) {
		routeStaList.add(routeSta);
	}
	
	public RouteSta getRouteSta(Long routeStaId) {
		for(RouteSta routeSta:routeStaList) {
			if(routeStaId.equals(routeSta.getRouteStationId())) {
				return routeSta;
			}
		}
		return null;
	}
	
	public RouteSta getLastRouteSta() {
		RouteSta lastRouteSta=null;
		if(!routeStaList.isEmpty()) {
			lastRouteSta=routeStaList.get(routeStaList.size()-1);
		}
		return lastRouteSta;
	}
}
