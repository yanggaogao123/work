package com.gci.schedule.driverless.bean;

import com.gci.schedule.driverless.bean.common.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2023-06-14 13:52
 * @version: v1.0
 * @Modified by:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunStatusSwitch {
    private String obuid;
    private String redis_time;
    private String run_status_old;
    private Long bus_id;
    private String trip_start_time;
    private String run_status;

    public Boolean isNotDispatchToDispatched() {
        return Constant.RunBusStatus.NOT_DISPATCH.equals(run_status_old)
                && Constant.RunBusStatus.DISPATCHED.equals(run_status);
    }
    /*{
        "obuid": "918305",
        "redis_time": "20230608 104453",
        "run_status_old": "3",
        "bus_id": "3016043",
        "trip_start_time": "20230608 104600",
        "run_status": "1"
    }*/
}
