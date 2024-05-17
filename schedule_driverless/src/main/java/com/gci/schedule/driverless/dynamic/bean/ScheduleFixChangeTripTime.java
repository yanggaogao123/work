package com.gci.schedule.driverless.dynamic.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleFixChangeTripTime {

	private Date beginTime;

	private int direction;

	private int directionOut;

	private int type;//0:sub;1:add

	private boolean isAdd;

}
