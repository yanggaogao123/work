package com.gci.schedule.driverless.bean.enums;

public enum ScheduleStatus {

	COMMON_SCHEDULE(0,"常规排班"),
	DRIVERLESS_SCHEDULE(1,"无人车支援排班"),
	SUPPORTED_SCHEDULE(2,"常规线被支援排班"),
	SUPPORT_SCHEDULE(3,"常规线支援排班");


	private int value;

	private String desc;

	private ScheduleStatus(int value, String desc){
		this.value = value;
		this.desc = desc;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
