package com.gci.schedule.driverless.dynamic.enums;

public enum StationMark {
	
	UP_FIRST(0,"上行-首站"),
	UP_TRIP(1,"上行-中途站"),
	UP_LAST(2,"上行-末站"),
	DOWN_FIRST(3,"下行-首站"),
	DOWN_TRIP(4,"下行-中途站"),
	DOWN_LAST(5,"下行-末站");
	
	private int value;
	
	private String desc;
	
	private StationMark(int value, String desc){
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
