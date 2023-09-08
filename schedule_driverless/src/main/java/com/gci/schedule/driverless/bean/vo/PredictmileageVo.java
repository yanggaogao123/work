package com.gci.schedule.driverless.bean.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 里程预测请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictmileageVo {
    private String obuid;
    private String route_id;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date nowtime;
    //车辆剩余soc
    private Integer discharge_start_soc;
    //如果该参数不传或传的值不符合历史取值范围，默认取值为车辆历史最小剩余soc
    private Integer discharge_end_soc;

}
