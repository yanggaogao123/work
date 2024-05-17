package com.gci.schedule.driverless.bean;

import lombok.Data;

/**
 * 车辆挂起参数
 *
 * @author liangzc
 * @date 2023年4月10日
 */
@Data
public class HangUpBusVo {
    /**
     * 线路id
     */
    private Long routeId;

    /**
     * 挂起车辆id
     */
    private Long hangUpBusId;

}
