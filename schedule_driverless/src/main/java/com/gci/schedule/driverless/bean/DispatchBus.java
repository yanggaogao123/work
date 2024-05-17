package com.gci.schedule.driverless.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class DispatchBus {
    private String obuid;
    private String package_type;
    private String control_type;
    private String route_code;
    private String task_type;
    private String operator;
    private String control_plantime;
    private String seqno;
    private String priority;
    private String media_option;
    private String text;
    private String task_id;
    private String plan_date;
    private String service_type;
    private String remark;
    private Long reply_sn;
    @JSONField(format = "yyyyMMdd HHmmss")
    private Date dispatchSendTime;
}
