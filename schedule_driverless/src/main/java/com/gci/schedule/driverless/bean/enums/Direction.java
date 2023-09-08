package com.gci.schedule.driverless.bean.enums;

public enum Direction {
	
	UP(0,"上行"),
	DOWN(1,"下行"),
	NODIRECTION(2,"无方向");
	
	private int value;
	
	private String desc;
	
	private Direction(int value, String desc){
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
