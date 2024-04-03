package com.gci.schedule.driverless.dynamic.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class SchedulePlan {

    private Long scheduleId;

    private Long routeId;

	@JsonIgnore
    private String routeCode;

	@JsonIgnore
    private Date planDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
    private Date planTime;

    @JsonIgnore
    private String startDirection;

    @JsonIgnore
    private Short startOrderNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  
    private Date tripEndTime;

//    @JsonIgnore
    private String serviceType;

    @JsonIgnore
    private String serviceName;

//    @JsonIgnore
    private String direction;

    @JsonIgnore
    private String busCode;

    @JsonIgnore
    private Integer busId;

    @JsonIgnore
    private String busName;

    @JsonIgnore
    private String busNameFull;//车辆全称

    @JsonIgnore
    private Integer busNumber ;//车序

    private Long firstRouteStaId;

    private Long lastRouteStaId;
    
    @JsonIgnore
    private String firstRouteStaName;

    @JsonIgnore
    private String lastRouteStaName;

    private Boolean singleBus;
    
    private Double runMileage;//里程
    
    private String peakType;//高平低峰设置(1:早高峰,2:晚高峰,3:平峰)
    
    private Date firstRoundPlanTime;//首轮时间
    
    private Long firstRoundTaskId;//首轮任务

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

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
        this.routeCode = routeCode == null ? null : routeCode.trim();
    }

    public Date getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    public Date getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Date planTime) {
        this.planTime = planTime;
    }

    public String getStartDirection() {
        return startDirection;
    }

    public void setStartDirection(String startDirection) {
        this.startDirection = startDirection == null ? null : startDirection.trim();
    }

    public Short getStartOrderNumber() {
        return startOrderNumber;
    }

    public void setStartOrderNumber(Short startOrderNumber) {
        this.startOrderNumber = startOrderNumber;
    }

    public Date getTripEndTime() {
        return tripEndTime;
    }

    public void setTripEndTime(Date tripEndTime) {
        this.tripEndTime = tripEndTime;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType == null ? null : serviceType.trim();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName == null ? null : serviceName.trim();
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction == null ? null : direction.trim();
    }

    public String getBusCode() {
        return busCode;
    }

    public void setBusCode(String busCode) {
        this.busCode = busCode == null ? null : busCode.trim();
    }

    public Long getFirstRouteStaId() {
        return firstRouteStaId;
    }

    public void setFirstRouteStaId(Long firstRouteStaId) {
        this.firstRouteStaId = firstRouteStaId;
    }

    public Long getLastRouteStaId() {
        return lastRouteStaId;
    }

    public void setLastRouteStaId(Long lastRouteStaId) {
        this.lastRouteStaId = lastRouteStaId;
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusNameFull() {
        return busNameFull;
    }

    public void setBusNameFull(String busNameFull) {
        this.busNameFull = busNameFull;
    }

    public Integer getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(Integer busNumber) {
        this.busNumber = busNumber;
    }

	public String getFirstRouteStaName() {
		return firstRouteStaName;
	}

	public void setFirstRouteStaName(String firstRouteStaName) {
		this.firstRouteStaName = firstRouteStaName;
	}

	public String getLastRouteStaName() {
		return lastRouteStaName;
	}

	public void setLastRouteStaName(String lastRouteStaName) {
		this.lastRouteStaName = lastRouteStaName;
	}

    public Boolean isSingleBus() {
        return singleBus;
    }

    public void setSingleBus(Boolean singleBus) {
        this.singleBus = singleBus;
    }

	public Double getRunMileage() {
		return runMileage;
	}

	public void setRunMileage(Double runMileage) {
		this.runMileage = runMileage;
	}

	public String getPeakType() {
		return peakType;
	}

	public void setPeakType(String peakType) {
		this.peakType = peakType;
	}

	public Date getFirstRoundPlanTime() {
		return firstRoundPlanTime;
	}

	public void setFirstRoundPlanTime(Date firstRoundPlanTime) {
		this.firstRoundPlanTime = firstRoundPlanTime;
	}

	public Long getFirstRoundTaskId() {
		return firstRoundTaskId;
	}

	public void setFirstRoundTaskId(Long firstRoundTaskId) {
		this.firstRoundTaskId = firstRoundTaskId;
	}

	/*public Boolean getSingleBus() {
		return singleBus;
	}*/

}