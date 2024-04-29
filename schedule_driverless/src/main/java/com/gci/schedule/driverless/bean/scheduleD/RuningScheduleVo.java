package com.gci.schedule.driverless.bean.scheduleD;

import lombok.Data;

import java.util.Date;

@Data
public class RuningScheduleVo implements Cloneable{
    //1：未开始，2：执行中，3：已完成
    private Integer status;

    private Long busId;

    private String busName;

    private String employeeName;

    //发班时间12：12
    private String tripBeginTime;

    private String tripEndTime;

    //发车间隔 ：分钟
    private Integer interval;

    //运行时长 ：分钟
    private Integer fullTime;

    //计划时间 12：13
    private String planTime;

    private String direction;

    private Long scheduleId;

    private Date driverlessPlanTime;

    //是否准点，1:准点，2:不准点
    private Integer outStatus;

    @Override
    public RuningScheduleVo clone() throws CloneNotSupportedException {
        return (RuningScheduleVo) super.clone();
    }
}
