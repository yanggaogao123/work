package com.gci.schedule.driverless.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 总站开出的全程
 *
 * @author lindeyong
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BusDepart {

    private Long busId;

    private String busName;

    @JsonFormat(pattern = "yyyyMMdd HHmmss", timezone = "GMT+8")
    @JSONField(format = "yyyyMMdd HHmmss")
    private Date tripBeginTime;

    private int isQuitAfterNextTrip; //下一班执行后是否退出

    private int isNotFullAfterTrip;  //执行后是否退出或短线

    private Integer latestBusDirection;//尾车方向

    private Integer tripNumberRemain;  //剩余班次

    private Integer endDirection;

}
