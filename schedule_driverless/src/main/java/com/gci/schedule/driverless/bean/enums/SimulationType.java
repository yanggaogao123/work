package com.gci.schedule.driverless.bean.enums;

public enum SimulationType {

	SAME_FIRST(0,"共首站"),
	SAME_LAST(1,"共末站"),
	SAME_FIRST_LAST(2,"共首末站"),
	SAME_FIRST_TO_LAST(3,"首战为支援线路末站"),
	SAME_LAST_TO_FIRST(4,"末站为支援线路首战"),
	CLOSE_FIST_LAST(5,"首末站相邻,方向相同"),
	CLOSE_FIST_LAST_REVERSE(6,"首末站相邻,方向不同"),
	DRIVERLESS(7,"无人车支援");


	private int value;

	private String desc;

	private SimulationType(int value, String desc){
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
