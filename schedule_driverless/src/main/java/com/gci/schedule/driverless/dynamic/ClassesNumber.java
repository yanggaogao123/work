package com.gci.schedule.driverless.dynamic;

public class ClassesNumber {

	private Short runTimeNum;
	
	private int direction;
	
	private int fullClassesNumber;
	
	private int shortClassesNumber;

	public ClassesNumber(Short runTimeNum, int direction) {
		super();
		this.runTimeNum = runTimeNum;
		this.direction=direction;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}



	public Short getRunTimeNum() {
		return runTimeNum;
	}

	public void setRunTimeNum(Short runTimeNum) {
		this.runTimeNum = runTimeNum;
	}

	public int getFullClassesNumber() {
		return fullClassesNumber;
	}

	public void setFullClassesNumber(int fullClassesNumber) {
		this.fullClassesNumber = fullClassesNumber;
	}

	public int getShortClassesNumber() {
		return shortClassesNumber;
	}

	public void setShortClassesNumber(int shortClassesNumber) {
		this.shortClassesNumber = shortClassesNumber;
	}

}
