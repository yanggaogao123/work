package com.gci.schedule.driverless.dynamic.enums;

public enum ShiftType {
	
	MORNING_SHIFT(0,"早班"),
	EVENING_SHIFT(1,"晚班"),
	MIDDLE_SHIFT(2,"中班"),
	DOUBLE_SHIFT_MIDDLE_STOP(3,"双班中停"),
	SINGLE_SHIFT_MIDDLE_STOP(4,"单班中停"),
	DOUBLE_SHIFT_NOT_MIDDLE_STOP(5,"双班");
	
	private Integer value;
	
	private String desc;
	
	private ShiftType(Integer value, String desc){
		this.value = value;
		this.desc = desc;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public static ShiftType getShiftType(int shifeCode) {
		for(ShiftType shiftType:ShiftType.class.getEnumConstants()) {
			if(shiftType.getValue().equals(shifeCode)) {
				return shiftType;
			}
		}
		return null;
	}
}
