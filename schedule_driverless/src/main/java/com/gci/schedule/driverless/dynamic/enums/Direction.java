package com.gci.schedule.driverless.dynamic.enums;

import java.util.HashMap;
import java.util.Map;

public enum Direction {

	UP(0,"上行"),
	DOWN(1,"下行"),
	NODIRECTION(2,"无方向");

	private int value;

	private String desc;

	private static Map<Integer, Direction> cache = new HashMap<>();

	static {
		for (Direction direction : values()) {
			cache.put(direction.getValue(), direction);
		}
	}

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

	public static Direction getByDirectionValue(String direction) {
		return cache.get(Integer.valueOf(direction));
	}

}
