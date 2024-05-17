package com.gci.schedule.driverless.bean.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 预估充电时长请求类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastChargeTimeVo {
    private String obuid;
    //开始充电时的soc
    private Integer charge_start_soc;
    //充电结束时的soc
    private Integer charge_end_soc;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date nowtime;

}
