package com.gci.schedule.driverless.bean;

import lombok.Data;

/**
 * 充电量预估
 */
@Data
public class ForecastNeedCharge {
    private String obuid;
    private Integer charge_start_soc;
    //车辆历史最小剩余soc
    private Integer min_soc;
    /**
     * 还需充的电量
     * 如果当前电量够用，返回-1；如果充满电仍然不够用，返回-2；如果输入错误，返回-3，并在exception_prompt字段中查看错误详情
     */
    private Integer need_charge_soc;
    /**
     * 异常提示
     * 输入无异常时，该字段返回{}
     */
//    private String exception_prompt;
    /**
     * 接口请求次数记录
     */
    private Integer pcount;
}
