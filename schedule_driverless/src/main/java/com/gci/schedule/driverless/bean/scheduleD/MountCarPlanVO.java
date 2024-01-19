package com.gci.schedule.driverless.bean.scheduleD;

import lombok.Data;

import java.util.List;

/**
 * @author liangzc
 * @date 2023/6/25 14:39
 **/
@Data
public class MountCarPlanVO {

    private Long templateId;

    private List<MountCarPlan> mountCarPlans;
}
