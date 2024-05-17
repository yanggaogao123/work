package com.gci.schedule.driverless.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BusArrival {

    private Long busId;

    private String busName;

    @JsonFormat(pattern = "yyyyMMdd HHmmss", timezone = "GMT+8")
    @JSONField(format = "yyyyMMdd HHmmss")
    private Date arrivalTime;

    private Integer eatTimeSchedule;//排班吃饭时长

    private Integer eatTimeDispatch;//发班吃饭时长

    private int isEat;//是否有吃饭标识(这边总站吃饭或或到对面吃)

    @JsonFormat(pattern = "yyyyMMdd HHmmss", timezone = "GMT+8")
    @JSONField(format = "yyyyMMdd HHmmss")
    private Date eatBeginTime;//吃饭开始时间(这边总站设置不能吃饭传空)

    private int isQuitAfterNextTrip;//下一班执行后是否退出

    @JsonFormat(pattern = "yyyyMMdd HHmmss", timezone = "GMT+8")
    @JSONField(format = "yyyyMMdd HHmmss")
    private Date tripBeginTime;

    private int restTimeMin;//最小停站时长

    private int restTimeMax;//最大停站时长

    private Integer latestBusDirection;//尾车方向

    private Integer tripNumberRemain;//剩余班次

    @JsonFormat(pattern = "yyyyMMdd HHmmss", timezone = "GMT+8")
    @JSONField(format = "yyyyMMdd HHmmss")
    private Date planTimeOrig;

    private Integer endDirection;

    private String serviceType;

    private String isReverseBus;//反向车(0:否，1:是)
}
