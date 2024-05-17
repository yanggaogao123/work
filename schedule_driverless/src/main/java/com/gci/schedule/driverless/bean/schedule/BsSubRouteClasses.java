package com.gci.schedule.driverless.bean.schedule;

import com.gci.schedule.driverless.bean.common.BsData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BsSubRouteClasses {

    private Long routeId;

    private Long templateId;

    private Long subRouteDetailId;

    private String beginTime;

    private String endTime;

    private Long maxDispatchInterval;

    private Integer isRegularBus;

    private String updateUser;

    private Date updateTime;

    public Boolean isRegularBus() {
        return isRegularBus != null && isRegularBus == 1;
    }

    public Date getBeginDateTime() {
        if (beginTime == null || endTime == null) {
            return null;
        }
        return BsData.getFirstTime(beginTime, endTime);
    }

    public Date getEndDateTime() {
        if (beginTime == null || endTime == null) {
            return null;
        }
        return BsData.getLatestTime(beginTime, endTime);
    }
}
