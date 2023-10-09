package com.gci.schedule.driverless.service.schedule;

import com.gci.schedule.driverless.bean.GenerateScheduleParams;
import com.gci.schedule.driverless.bean.common.R;
import com.gci.schedule.driverless.bean.vo.GenerateScheduleParams2;
import com.gci.schedule.driverless.bean.vo.ScheduleBySortParam;

public interface GenerateScheduleService {
    R generateSchedule(GenerateScheduleParams params);
    R generateSupportSchedule(GenerateScheduleParams2 params);
    R getScheduleBySort(ScheduleBySortParam params);
}
