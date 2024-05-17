package com.gci.schedule.driverless.dynamic.test;

public class LatestBusPair {

	private Bus latestBusUp;
	
	private Bus latestBusDown;
	
	public LatestBusPair(Bus latestBusUp, Bus latestBusDown) {
		super();
		this.latestBusUp = latestBusUp;
		this.latestBusDown = latestBusDown;
	}

	public Bus getLatestBusUp() {
		return latestBusUp;
	}

	public void setLatestBusUp(Bus latestBusUp) {
		this.latestBusUp = latestBusUp;
	}

	public Bus getLatestBusDown() {
		return latestBusDown;
	}

	public void setLatestBusDown(Bus latestBusDown) {
		this.latestBusDown = latestBusDown;
	}

}
