package com.gci.schedule.driverless.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 失班参数
 *
 * @author liangzc
 */
@Data
public class LostBusVo {
    private Long routeId;
    private List<Long> busId = new ArrayList<>();

    /**
     * 失班重打方式标识
     *
     * 1-重打已发时间车辆
     * 0-过滤已发时间车辆
     */
    private Integer changeInvalid;


    /**
     * 是否重打已发时间计划
     *
     * @return
     */
    public boolean canChangeInvalidPlan() {
        return changeInvalid == 1;
    }
}
