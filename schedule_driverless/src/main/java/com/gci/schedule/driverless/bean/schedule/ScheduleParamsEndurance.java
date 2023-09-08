package com.gci.schedule.driverless.bean.schedule;

import com.alibaba.fastjson.annotation.JSONField;
import com.gci.schedule.driverless.bean.common.BsData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleParamsEndurance {
    private Long routeId;

    private Long templateId;

    private Long stationId;

    private String stationName;

    private String beginTime;

    private String endTime;

    private Integer busNumber;

    private String updateUser;

    private Date updateTime;

    //续航里程
    private Integer enduranceMileage;

    private Integer supplyTime;//补电时长

    @JSONField(serialize = false)
    public Date getBeginDateTime() {
        if (beginTime == null || endTime == null) {
            return null;
        }
        return BsData.getFirstTime(beginTime, endTime);
    }

    @JSONField(serialize = false)
    public Date getEndDateTime() {
        if (beginTime == null || endTime == null) {
            return null;
        }
        return BsData.getLatestTime(beginTime, endTime);
    }

}
