package com.gci.schedule.driverless.bean.scheduleD;

import lombok.Data;

@Data
public class RuningScheduleVo {
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

    //实际发车时间 12：13
    private String realTripBeginTime;

    private String direction;
}
