package com.gci.schedule.driverless.service.schedule;

import com.gci.schedule.driverless.bean.GenerateScheduleParams;
import com.gci.schedule.driverless.bean.common.R;

public interface GenerateScheduleService {
    R generateSchedule(GenerateScheduleParams params);
}
