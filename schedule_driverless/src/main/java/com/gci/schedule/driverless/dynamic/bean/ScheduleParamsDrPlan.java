package com.gci.schedule.driverless.dynamic.bean;

import com.gci.schedule.driverless.dynamic.exception.MyException;
import com.gci.schedule.driverless.dynamic.test.DateUtil;
import com.gci.schedule.driverless.dynamic.util.StringUtil;
import lombok.Data;

import java.util.Date;

@Data
public class ScheduleParamsDrPlan {

    private String busNameWY;

    private String dispatchDate;

    private String routeIdWY;

    private String beginTime;

    private String endTime;

    private Long firstStationId;

    private Long lastStationId;

    private Date tripBeginTime;

    private Date tripEndTime;

    private ScheduleParamsDrInout driverlessIn;

    private ScheduleParamsDrInout driverlessOut;

    public void setTripTime() {
        try {
            if (StringUtil.isNotEmpty(beginTime)) {
                tripBeginTime = DateUtil.getDateHM(beginTime);
            }
            if (StringUtil.isNotEmpty(endTime)) {
                tripEndTime = DateUtil.getDateHM(endTime);
            }
        } catch (Exception e) {}
        if (tripBeginTime == null || tripEndTime == null) {
            throw new MyException("-1", "自动驾驶车辆支援便民线行车计划，计划开始/结束时间有误！");
        }
//        tripBeginTime = DateUtil.DateFormatUtil.DATE_TIME2.getDate(dispatchDate + " " + beginTime);
//        tripEndTime = DateUtil.DateFormatUtil.DATE_TIME2.getDate(dispatchDate + " " + endTime);
        if (!tripEndTime.after(tripBeginTime)) {
            tripEndTime = DateUtil.getDateAddDay(tripEndTime, 1);
        }
    }

}