package com.gci.schedule.driverless.dynamic.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleFixCheckTripTime {

	private int direction;

	private Date beginTime;

	private Date endTime;

	private Long routeId;

	private int type;

	@Override
	public int hashCode() {
		String key = this.getRouteId() + "_" + this.getBeginTime() + "_" + this.getEndTime() + "_" + this.getDirection() + "_" + this.getType();
		return key.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ScheduleFixCheckTripTime) {
			ScheduleFixCheckTripTime other = (ScheduleFixCheckTripTime) o;
			if ((this.getRouteId() == null && other.getRouteId() == null) || (this.getRouteId() != null && this.getRouteId().equals(other.getRouteId()))) {
				if ((this.getBeginTime() == null && other.getBeginTime() == null) || (this.getBeginTime() != null && this.getBeginTime().equals(other.getBeginTime()))) {
					if ((this.getEndTime() == null && other.getEndTime() == null) || (this.getEndTime() != null && this.getEndTime().equals(other.getEndTime()))) {
						if (this.getDirection() == other.getDirection() && this.getType() == other.getType()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
