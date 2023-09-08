package com.gci.schedule.driverless.bean.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 充电量预估
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastNeedChargeVo {
    private String obuid;

    private String route_id;
    /**
     * 开始充电时的soc
     */
    private Integer charge_start_soc;
    /**
     * 容忍车辆电池剩余的最少电量,如果该参数不传或传的值不符合历史取值范围，默认取值为车辆历史最小剩余soc
     */
    private Integer left_soc;
    /**
     * 还需跑的里程数
     * 单位：km。精确到小数点后三位，如：120.000
     */
    private Double run_mileage;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date nowtime;

}
