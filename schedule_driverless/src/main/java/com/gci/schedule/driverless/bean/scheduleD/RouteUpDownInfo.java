package com.gci.schedule.driverless.bean.scheduleD;

import lombok.Data;

import java.util.Objects;

@Data
public class RouteUpDownInfo {
    private Integer direction;
    private String firstStationName;
    //首班时间，06:30
    private String firstTime;
    private String lastStationName;
    //晚班时间，22:30
    private String latestTime;
    private String organName;

    public String getFirstTimeNumStr(){
        if(Objects.isNull(this.firstTime)){
            return null;
        }
        return this.firstTime.replace(":","").trim();
    }
    public String getLatestTimeNumStr(){
        if(Objects.isNull(this.latestTime)){
            return null;
        }
        return this.latestTime.replace(":","").trim();
    }
}
