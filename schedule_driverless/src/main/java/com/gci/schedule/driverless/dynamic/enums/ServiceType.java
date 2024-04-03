package com.gci.schedule.driverless.dynamic.enums;

public enum ServiceType {
	
	FULL_TRIP(1,"全程"),
	SHORT_TRIP(2,"短线"),
	ENTRANCE_PARKING(-9,"中停"),
	MIDDLE_STOP(-15,"中停"),
	SINGLE_CLASSES_MIDDLE_STOP(-32,"单班中停"),
	PULL_STOP(-33,"抽停"),
	DOUBLE_CLASSES_MIDDLE_STOP(-40,"双班中停"),
	ELECTRICITY_SUPPLEMENT(-78,"补电");
	
	private int value;
	
	private String desc;
	
	private ServiceType(int value, String desc){
		this.value = value;
		this.desc = desc;
	}

	public int getValue() {
		return value;
	}
	
	public String getStringValue() {
		return String.valueOf(value);
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
