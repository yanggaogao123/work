package com.gci.schedule.driverless.dynamic.bean;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.gci.schedule.driverless.dynamic.test.DateUtil.DateFormatUtil;

import java.util.Date;
import java.util.List;

public class ScheduleParamPreset {
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date runDate;//排班日期
	
	private Long routeId;//排班线路
	
	private Integer busNumberUp;//上行配车数
	
	private Integer busNumberDown;//下行配车数
	
	private Integer singleBusNumberUp;//上行单班车数
	
	private Integer singleBusNumberDown;//下行单班车数
	
	private Integer singleBusNumber;//单班车总数
	
	private Integer busNumberPreset;
	
	private List<ScheduleShiftPreset> shiftList;//班别列表
	
	@JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
	private Date startWorkRunDate;//出厂模板日期
	
	@JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
	private Date referenceRunDate;//客流和周转时间参考日期
	
	@JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
	private Date passengeReferenceRunDate;//客流参考日期
	
	@JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
	private Date runningTimeReferenceRunDate;//周转时间参考日期
	
	private Integer templateId;//参数模板ID

	private boolean isCompetitiveRoute;//是否为竞争线路

	private String comRouteIds;//竞争线路,以逗号隔开 1,2,3,4,5

	private int runtimeNum;//日志打印时段

	private Integer cover;
	
	private boolean isShuttleRoute;//分级短线线路
	
	private String accountName="sys";//操作账号
	
	private Route route;//线路信息
	
	private int createType;//生成方式(0:最优；1:预设)
	
	private Integer busNumberOriginUp;//上行配车数(原始)
	
	private Integer busNumberOriginDown;//下行配车数(原始)
	
	private List<RouteFirstAndLatestTime> multiPeriodList;//定点班车分时段，如21:00-01:00、4:00-6:00

	public Integer getCover() {
		return cover;
	}

	public void setCover(Integer cover) {
		this.cover = cover;
	}

	public String getComRouteIds() {
		return comRouteIds;
	}

	public void setComRouteIds(String comRouteIds) {
		this.comRouteIds = comRouteIds;
	}

	public int getRuntimeNum() {
		return runtimeNum;
	}

	public void setRuntimeNum(int runtimeNum) {
		this.runtimeNum = runtimeNum;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public Date getStartWorkRunDate() {
		return startWorkRunDate;
	}

	public void setStartWorkRunDate(Date startWorkRunDate) {
		this.startWorkRunDate = startWorkRunDate;
	}

	public Date getRunDate() {
		return runDate;
	}

	public void setRunDate(Date runDate) {
        runDate=DateFormatUtil.DATE.getDate(DateFormatUtil.DATE.getDateString(runDate));
		this.runDate = runDate;
	}

	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public Integer getBusNumberUp() {
		return busNumberUp;
	}

	public void setBusNumberUp(Integer busNumberUp) {
		this.busNumberUp = busNumberUp;
	}

	public Integer getBusNumberDown() {
		return busNumberDown;
	}

	public void setBusNumberDown(Integer busNumberDown) {
		this.busNumberDown = busNumberDown;
	}

	public Integer getSingleBusNumberUp() {
		return singleBusNumberUp;
	}

	public void setSingleBusNumberUp(Integer singleBusNumberUp) {
		this.singleBusNumberUp = singleBusNumberUp;
	}

	public Integer getSingleBusNumberDown() {
		return singleBusNumberDown;
	}

	public void setSingleBusNumberDown(Integer singleBusNumberDown) {
		this.singleBusNumberDown = singleBusNumberDown;
	}

	public Integer getSingleBusNumber() {
		return singleBusNumber;
	}

	public void setSingleBusNumber(Integer singleBusNumber) {
		this.singleBusNumber = singleBusNumber;
	}

	public Integer getBusNumberPreset() {
		return busNumberPreset;
	}

	public void setBusNumberPreset(Integer busNumberPreset) {
		this.busNumberPreset = busNumberPreset;
	}

	public Date getReferenceRunDate() {
		return referenceRunDate;
	}

	public void setReferenceRunDate(Date referenceRunDate) {
		this.referenceRunDate = referenceRunDate;
	}

	public List<ScheduleShiftPreset> getShiftList() {
		return shiftList;
	}

	public void setShiftList(List<ScheduleShiftPreset> shiftList) {
		this.shiftList = shiftList;
	}

	public Date getPassengeReferenceRunDate() {
		return passengeReferenceRunDate;
	}

	public void setPassengeReferenceRunDate(Date passengeReferenceRunDate) {
		this.passengeReferenceRunDate = passengeReferenceRunDate;
	}

	public Date getRunningTimeReferenceRunDate() {
		return runningTimeReferenceRunDate;
	}

	public void setRunningTimeReferenceRunDate(Date runningTimeReferenceRunDate) {
		this.runningTimeReferenceRunDate = runningTimeReferenceRunDate;
	}

	public boolean isCompetitiveRoute() {
		return isCompetitiveRoute;
	}

	public void setCompetitiveRoute(boolean competitiveRoute) {
		isCompetitiveRoute = competitiveRoute;
	}

	public boolean isShuttleRoute() {
		return isShuttleRoute;
	}

	public void setShuttleRoute(boolean isShuttleRoute) {
		this.isShuttleRoute = isShuttleRoute;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public int getCreateType() {
		return createType;
	}

	public void setCreateType(int createType) {
		this.createType = createType;
	}
	
	public Integer getBusNumberOriginUp() {
		return busNumberOriginUp;
	}

	public void setBusNumberOriginUp(Integer busNumberOriginUp) {
		this.busNumberOriginUp = busNumberOriginUp;
	}

	public Integer getBusNumberOriginDown() {
		return busNumberOriginDown;
	}

	public void setBusNumberOriginDown(Integer busNumberOriginDown) {
		this.busNumberOriginDown = busNumberOriginDown;
	}
	
	public List<RouteFirstAndLatestTime> getMultiPeriodList() {
		return multiPeriodList;
	}

	public void setMultiPeriodList(List<RouteFirstAndLatestTime> multiPeriodList) {
		this.multiPeriodList = multiPeriodList;
	}

	@Override
	public String toString() {
		return "ScheduleParamPreset [runDate=" + runDate + ", routeId=" + routeId + ", busNumberUp=" + busNumberUp
				+ ", busNumberDown=" + busNumberDown + ", singleBusNumberUp=" + singleBusNumberUp
				+ ", singleBusNumberDown=" + singleBusNumberDown + ", singleBusNumber=" + singleBusNumber
				+ ", busNumberPreset=" + busNumberPreset + ", shiftList=" + shiftList + ", startWorkRunDate="
				+ startWorkRunDate + ", referenceRunDate=" + referenceRunDate + ", passengeReferenceRunDate="
				+ passengeReferenceRunDate
				+ ", runningTimeReferenceRunDate=" + runningTimeReferenceRunDate
				+ ", isCompetitiveRoute=" + isCompetitiveRoute
				+ ", templateId=" + templateId + "]";
	}
	
}
