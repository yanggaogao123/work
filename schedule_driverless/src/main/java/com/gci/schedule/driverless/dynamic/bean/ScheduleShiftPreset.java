package com.gci.schedule.driverless.dynamic.bean;

import com.gci.schedule.driverless.dynamic.enums.Direction;

public class ScheduleShiftPreset {

	//班别类型 0：早半班 1：晚半班 2：中班 3.双班中停
    private int shiftType;
    
    private Integer busNumberUp;//上行出车数
    
    private Integer busNumberDown;//下行出车数
    
    private Integer busNumber;//总出车数

	public int getShiftType() {
		return shiftType;
	}

	public void setShiftType(int shiftType) {
		this.shiftType = shiftType;
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
	
	public Integer getBusNumber(int direction) {
		if(direction==Direction.UP.getValue()) {
			return busNumberUp;
		}else if(direction==Direction.DOWN.getValue()) {
			return busNumberDown;
		}
		return null;
	}

	public Integer getBusNumber() {
		return busNumber;
	}

	public void setBusNumber(Integer busNumber) {
		this.busNumber = busNumber;
	}

}
