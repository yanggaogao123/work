package com.gci.schedule.driverless.bean.schedule;

import com.gci.schedule.driverless.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BsSubRouteEat {

    private Long routeId;

    private Long templateId;

    private Long subRouteDetailId;

    private String beginTime;

    private Long eatTime;

    private String updateUser;

    private Date updateTime;

    public Date getBeginDateTime() {
        String today = DateUtil.date2Str(new Date(), "yyyy-MM-dd");
        return new Date(DateUtil.str2Timestamp(today + " " + beginTime.substring(0, 2)
                + ":" + beginTime.substring(2, 4) + ":00", "yyyy-MM-dd HH:mm:ss"));
    }
}
