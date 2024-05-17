package com.gci.schedule.driverless.service.schedule;

import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.vo.ScheduleCountParam;

import java.util.Map;

public interface ScheduleCountService {
    R getScheduleCountResult(ScheduleCountParam param);
}
