package com.gci.schedule.driverless.bean.vo;

import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsAnchor;
import com.gci.schedule.driverless.bean.scheduleD.ScheduleParamsRoute;
import lombok.Data;

import java.util.List;

@Data
public class ScheduleCountParam02 {
    private List<ScheduleParamsAnchor> anchorList;

    private ScheduleParamsRoute scheduleParamsRoute;
}
